package com.aluracursos.desafio.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String language;
    private Double numeroDescargas;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Autor> autores;

    public Libros() {}

    public Libros(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        if (datosLibros.idioma() != null && !datosLibros.idioma().isEmpty()) {
            this.language = datosLibros.idioma().get(0); // Obtener el primer idioma
        } else {
            this.language = null;
        }
        this.numeroDescargas = datosLibros.numeroDescargas();
    }

    // Getters y setters para todos los campos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
        // Asignar este libro a cada autor en la lista
        if (autores != null) {
            autores.forEach(autor -> autor.setLibro(this));
        }
    }
}
