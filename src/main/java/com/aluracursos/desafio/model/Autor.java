package com.aluracursos.desafio.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String fechaNac;
    private String fechaMuerte;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libros libro;

    public Autor() {}

    public Autor(DatosAutor d) {
        this.nombre = d.nombre();
        this.fechaNac = d.fechaNac();
        this.fechaMuerte = d.fechaMuerte();
    }

    // Getters y setters para todos los campos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getFechaMuerte() {
        return fechaMuerte;
    }

    public void setFechaMuerte(String fechaMuerte) {
        this.fechaMuerte = fechaMuerte;
    }

    public Libros getLibro() {
        return libro;
    }

    public void setLibro(Libros libro) {
        this.libro = libro;
    }
}
