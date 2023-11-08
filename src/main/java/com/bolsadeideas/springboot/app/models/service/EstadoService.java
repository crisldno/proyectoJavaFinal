package com.bolsadeideas.springboot.app.models.service;

import com.bolsadeideas.springboot.app.models.entity.Estado;

import java.util.List;

public interface EstadoService {


    public List<Estado> findAll();

    public Estado findById(long id);

}
