package com.example.entities;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mascota implements Serializable {
    private static final long serialVersionUID = 1L;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;

    @NotEmpty(message = "El campo <raza> no puede estar vacío")
    @Size(min = 2, max = 25, message = "El campo <raza> tiene que estar entre 2 y 25 caracteres")
    private String raza;


    @Enumerated(EnumType.STRING)
    private Genero genero;

    
    private LocalDate fechaDeNacimiento;

   

    public enum Genero {
        HEMBRA, MACHO
        
    }


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JsonIgnore
    @JsonManagedReference
    private Cliente cliente;

    
}
