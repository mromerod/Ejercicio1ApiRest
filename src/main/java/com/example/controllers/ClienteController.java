package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Cliente;
import com.example.model.FileUploadResponse;
import com.example.services.ClienteService;
import com.example.utilities.FileUploadUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor

public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(@RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        ResponseEntity<List<Cliente>> responseEntity = null;
        List<Cliente> clientes = new ArrayList<>();

        Sort sortByNombre = Sort.by("nombre");

        if (page != null && size != null) {

            try {
                Pageable pageable = PageRequest.of(page, size, sortByNombre);
                Page<Cliente> clientePaginados = clienteService.findAll(pageable);
                clientes = clientePaginados.getContent();
                responseEntity = new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            try {
                clientes = clienteService.findAll(sortByNombre);
                responseEntity = new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        }

        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        try {
            Cliente cliente = clienteService.findById(id);

            if (cliente != null) {
                String successMessage = "Se ha encontrado el cliente con id: " + id;
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("cliente", cliente);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            } else {
                String errorMessage = "No se ha encontrado el cliente con el id: " + id;
                responseAsMap.put("error", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {

            String errorGrave = "Error grave";
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

    //Guardar un cliente con su imagen,

@PostMapping(consumes = "multipart/form-data")
@Transactional
public ResponseEntity<Map<String, Object>> insert(
    @Valid @RequestPart(name = "cliente") Cliente cliente, BindingResult result,
    @RequestPart(name = "file") MultipartFile file) throws IOException {

Map<String, Object> responseAsMap = new HashMap<>();
ResponseEntity <Map<String,Object>> responseEntity = null;

/**
 * Primero: Comprobar si hay errores en el cliente recibido
 */


 if(result.hasErrors()){

    List<String> errorMessage = new ArrayList<>();


    for(ObjectError error : result.getAllErrors()){

        errorMessage.add(error.getDefaultMessage());

    }

    responseAsMap.put("errores", errorMessage);

    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

    return responseEntity;

 }

 //Si no hay errores ,se guarda si previamente nos han enviado una imagen

 if(!file.isEmpty()){
   
  String fileCode = fileUploadUtil.saveFile(file.getOriginalFilename(), file);
  cliente.setImagenMascota(fileCode+ "-" + file.getOriginalFilename());

  //DEvolver informacion respecto al producto recibido

    FileUploadResponse fileUploadResponse = FileUploadResponse.builder()
    .fileName(fileCode+ "-" + file.getOriginalFilename())
    .downloadURI("/productos/downloadFile/" + fileCode + "-" + file.getOriginalFilename())
    .size(file.getSize())
    .build();
    responseAsMap.put("info de la imagen", fileUploadResponse);
 }
 Cliente clienteDB = clienteService.save(cliente);
 
 try {
    if(clienteDB != null){

        String mensaje = "El cliente se ha guardado exitosamente";
        responseAsMap.put("mensaje", mensaje);
        responseAsMap.put("cliente", cliente);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
    
     }else {
    
        String mensaje = "El cliente no se ha creado";
        responseAsMap.put("mensaje", mensaje);
        
        responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    
     }
    
 } catch (DataAccessException e) { //cuidado porque le excepcion es para no dejarte acceder a los datos 
    String errorGrave = "Ha tenido lugar un error grave" + ", y la causa más probale puede ser"
    + e.getMostSpecificCause();

    responseAsMap.put("errorGrave", errorGrave);

    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
 }
return responseEntity;

}



}