package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.validation.Valid;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.bolsadeideas.springboot.app.models.entity.Estado;
import com.bolsadeideas.springboot.app.models.service.EstadoService;
import com.bolsadeideas.springboot.app.models.service.EstadoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private EstadoService estadoService;
	@Autowired
	private IUploadFileService uploadFileService;

	@Autowired
	private RestTemplate restTemplate;


	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombre());
		return "ver";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);



		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {


		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}
	@RequestMapping(value = "/home")
	public String home(Map<String, Object> model) {

		return "home";
	}
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {

				uploadFileService.delete(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");

			cliente.setFoto(uniqueFilename);
		}

		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
		Estado estado = estadoService.findById(1);
		cliente.setEstado(estado);
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}

	@RequestMapping(value = "/enviar/{id}", method = RequestMethod.GET)
	public String enviar(@PathVariable(value = "id") Long id) {
		Cliente cliente = clienteService.findOne(id);

		if(!cliente.getFoto().isEmpty()){
			String url;
			if(cliente.getId() % 2 == 0){
				 url = "https://run.mocky.io/v3/b9a4b435-e479-4369-a0f1-7e222f88f9d6";
			}else{
				 url = "https://run.mocky.io/v3/824d579f-4e0f-4a83-856d-dc61341550e4";
			}

			String respuesta = restTemplate.getForObject(url, String.class);

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> jsonMap = objectMapper.readValue(respuesta, Map.class);

				Long numeroCuentaFromResponse = ((Number) jsonMap.get("numeroCuenta")).longValue();
				Long numeroCuentaFromCliente = cliente.getNumeroCuenta();
				String nombreFoto = cliente.getFoto();

				if (numeroCuentaFromResponse != null && numeroCuentaFromResponse.equals(numeroCuentaFromCliente) && nombreFoto.toLowerCase().contains("cedula") ) {
					System.out.println("Los números de cuenta coinciden y la foto tiene la palabra cedula");
					Estado estadoAprobado = estadoService.findById(2);
					cliente.setEstado(estadoAprobado);
					clienteService.save(cliente);
				} else {
					System.out.println("Los números de cuenta no coinciden.");
					Estado estadoRechazado = estadoService.findById(3);
					cliente.setEstado(estadoRechazado);
					clienteService.save(cliente);
				}
			} catch (IOException e) {
				System.err.println("Error al analizar la respuesta JSON: " + e.getMessage());
			}

		}else{
			System.out.println("FALLO");
			return "redirect:/listar";
		}
		return "redirect:/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito!");
			}

		}
		return "redirect:/listar";
	}
}