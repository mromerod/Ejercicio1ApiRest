package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.dao.ClienteDao;
import com.example.entities.Cliente;

public class ClienteServiceImpl implements ClienteService {
    private ClienteDao clienteDao;

    @Override
    public List<Cliente> findAll(Sort sort) {
        return clienteDao.findAll(sort);
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
       return clienteDao.findAll(pageable);
    }

    @Override
    public Cliente findById(long id) {
       return clienteDao.findById(id);
    }

    @Override
    public Cliente save(Cliente cliente) {
        
        return clienteDao.save(cliente);
        
    }

    @Override
    public void delete(Cliente cliente) {
       clienteDao.delete(cliente);
    }
    
}
