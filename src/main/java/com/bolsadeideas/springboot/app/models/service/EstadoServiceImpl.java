package com.bolsadeideas.springboot.app.models.service;

import com.bolsadeideas.springboot.app.models.dao.EstadoDao;
import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Estados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EstadoServiceImpl implements EstadoService {

    @Autowired
    private EstadoDao estadoDao;
    @Override
    public List<Estados> findAll() {
        return (List<Estados>) estadoDao.findAll();
    }
}
