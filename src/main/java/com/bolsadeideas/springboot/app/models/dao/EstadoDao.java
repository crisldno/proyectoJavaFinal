package com.bolsadeideas.springboot.app.models.dao;


import com.bolsadeideas.springboot.app.models.entity.Estado;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EstadoDao extends PagingAndSortingRepository<Estado, Long> {
}
