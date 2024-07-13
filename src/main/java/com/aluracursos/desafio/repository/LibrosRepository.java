package com.aluracursos.desafio.repository;

import com.aluracursos.desafio.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface LibrosRepository extends JpaRepository<Libros, Long> {
    Optional<Libros> findByTituloAndLanguage(String titulo, String language);
    List<Libros> findByLanguage(String language);
}