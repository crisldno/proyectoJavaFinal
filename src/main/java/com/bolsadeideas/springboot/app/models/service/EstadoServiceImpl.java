package com.bolsadeideas.springboot.app.models.service;

import com.bolsadeideas.springboot.app.models.dao.EstadoDao;
import com.bolsadeideas.springboot.app.models.entity.Estado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EstadoServiceImpl implements EstadoService {

    @Autowired
    private EstadoDao estadoDao;
    @Override
    public List<Estado> findAll() {
        return (List<Estado>) estadoDao.findAll();
    }

    @Override
    public Estado findById(long id) {
        return  estadoDao.findById(id).orElse(null);
    }


}
