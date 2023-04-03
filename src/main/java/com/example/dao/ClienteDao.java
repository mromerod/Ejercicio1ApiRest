package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Cliente;

public interface ClienteDao extends JpaRepository<Cliente,Long> {


     //Recupera lista de Clientes ordenados y trae los hoteles y mascotas
     @Query(value = "select c from Cliente c inner join fetch c.hotel left join fetch c.mascota") //consultas a las entidades hql
     public List<Cliente> findAll(Sort sort);


    //recupera una pagina de cliente
    @Query(value = "select c from Cliente c left join fetch c.hotel left join fetch c.mascota",
     countQuery = "select count(c) from Cliente c left join c.hotel left join c.mascota")
     public Page<Cliente> findAll(Pageable pageable);

    //El metodo siguiente recupera el cliente por ID, para que nos traiga hotel y mascota

    @Query(value = "select c from Cliente c left join fetch c.hotel left join fetch c.mascota where c.id= :id")
    
    public Cliente findById(long id);

    
}
