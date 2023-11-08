package com.bolsadeideas.springboot.app.models.service;

import com.bolsadeideas.springboot.app.models.dao.EstadoDao;
import com.bolsadeideas.springboot.app.models.entity.Estados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface EstadoService {


    public List<Estados> findAll();
}
