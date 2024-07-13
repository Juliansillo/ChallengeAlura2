package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Autor;
import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.model.Libros;
import com.aluracursos.desafio.repository.AutorRepository;
import com.aluracursos.desafio.repository.LibrosRepository;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private LibrosRepository librosRepository;
    private AutorRepository autorRepository;

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner scanner = new Scanner(System.in);

    public Principal(LibrosRepository librosRepositorio, AutorRepository autorRepositorio) {
        this.librosRepository = librosRepositorio;
        this.autorRepository = autorRepositorio;
    }

    public void muestraMenu() {
        try {
            while (true) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Buscar libro por título e insertar en la base de datos");
                System.out.println("2. Mostrar autores vivos en un año determinado");
                System.out.println("3. Mostrar libros por idioma");
                System.out.println("4. Mostrar libros registrados");
                System.out.println("5. Mostrar autores registrados");
                System.out.println("6. Salir");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        buscarYGuardarLibro();
                        break;
                    case 2:
                        mostrarAutoresVivos();
                        break;
                    case 3:
                        mostrarLibrosPorIdioma();
                        break;
                    case 4:
                        mostrarLibrosRegistrados();
                        break;
                    case 5:
                        mostrarAutoresRegistrados();
                        break;
                    case 6:
                        System.out.println("Saliendo...");
                        return;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } finally {
            scanner.close();
        }
    }
    public void buscarYGuardarLibro() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String titulo = scanner.nextLine();
        String url = URL_BASE + "?search=" + titulo.replace(" ", "+");

        // Obtener datos JSON de la API
        String json = consumoAPI.obtenerDatos(url);

        // Imprimir la respuesta JSON completa para depuración
        System.out.println("Respuesta JSON de la API: " + json);

        // Convertir JSON a objeto Datos
        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        // Imprimir los datos convertidos para depuración
        System.out.println("Datos convertidos: " + datosBusqueda);

        // Buscar el libro por título en los datos obtenidos
        Optional<DatosLibros> libroBuscado = datosBusqueda.Libros().stream()
                .filter(l -> l.titulo() != null && l.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibros datosLibros = libroBuscado.get();
            System.out.println("Libro encontrado:");
            System.out.println(datosLibros);

            // Verificar si el libro ya está en la base de datos
            Optional<Libros> libroExistente = datosLibros.idioma().isEmpty()
                    ? Optional.empty()
                    : librosRepository.findByTituloAndLanguage(datosLibros.titulo(), datosLibros.idioma().get(0));

            if (libroExistente.isPresent()) {
                System.out.println("El libro ya está registrado en la base de datos.");
            } else {
                // Convertir y guardar en la base de datos
                Libros libro = new Libros(datosLibros);

                // Convertir y agregar autores al libro
                List<Autor> autores = datosLibros.autor().stream()
                        .map(datosAutor -> {
                            Autor autor = new Autor(datosAutor);
                            autor.setLibro(libro); // Asignar el libro al autor
                            return autor;
                        })
                        .collect(Collectors.toList());

                libro.setAutores(autores);

                // Guardar libro y autores
                librosRepository.save(libro);

                System.out.println("Libro y autores guardados exitosamente.");
                // Imprimir autores guardados para depuración
                autores.forEach(autor -> {
                    System.out.println("Autor guardado: " + autor.getNombre() + ", Fecha de Nacimiento: " + autor.getFechaNac());
                });
            }
        } else {
            System.out.println("Libro NO encontrado!");
        }
    }



    public void mostrarAutoresVivos() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        int año = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Obtener todos los autores de la base de datos
        List<Autor> autores = autorRepository.findAllWithLibros();

        // Filtrar los autores que estaban vivos en el año especificado
        List<Autor> autoresVivos = autores.stream()
                .filter(autor -> {
                    try {
                        int añoNacimiento = Integer.parseInt(autor.getFechaNac());
                        int añoMuerte = autor.getFechaMuerte() != null ? Integer.parseInt(autor.getFechaMuerte()) : Integer.MAX_VALUE;
                        return año >= añoNacimiento && año <= añoMuerte;
                    } catch (NumberFormatException e) {
                        // En caso de que la fecha no sea un número válido
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // Mostrar los autores vivos en el año especificado
        if (!autoresVivos.isEmpty()) {
            System.out.println("Autores vivos en el año " + año + ":");
            for (Autor autor : autoresVivos) {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Fecha de Nacimiento: " + autor.getFechaNac());
                System.out.println("Fecha de Muerte: " + autor.getFechaMuerte());
                System.out.println("----------------------");
            }
        } else {
            System.out.println("No se encontraron autores vivos en el año " + año + ".");
        }
    }



    public void mostrarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAllWithLibros();

        if (!autores.isEmpty()) {
            System.out.println("Autores registrados en la base de datos:");
            for (Autor autor : autores) {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Fecha de Nacimiento: " + autor.getFechaNac());
                System.out.println("Fecha de Muerte: " + autor.getFechaMuerte());

                if (autor.getLibro() != null) {
                    System.out.println("Libro Asociado: " + autor.getLibro().getTitulo());
                } else {
                    System.out.println("Sin libro asociado.");
                }

                System.out.println("----------------------");
            }
        } else {
            System.out.println("No se encontraron autores registrados en la base de datos.");
        }
    }



    public void mostrarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma (por ejemplo, 'es' para español):");
        String idioma = scanner.nextLine();
        List<Libros> librosPorIdioma = librosRepository.findByLanguage(idioma);

        if (!librosPorIdioma.isEmpty()) {
            System.out.println("Libros en " + idioma + ":");
            librosPorIdioma.forEach(libro -> System.out.println(libro.getTitulo()));
        } else {
            System.out.println("No se encontraron libros en " + idioma + ".");
        }
    }
    public void mostrarLibrosRegistrados() {
        List<Libros> libros = librosRepository.findAll();

        if (!libros.isEmpty()) {
            System.out.println("Libros registrados:");
            libros.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Idioma: " + libro.getLanguage());
                System.out.println("Numero de descargas: " + libro.getNumeroDescargas());
                System.out.println("Autores: ");
                libro.getAutores().forEach(autor -> System.out.println("\t" + autor.getNombre()));
                System.out.println("---------------------------------------");
            });
        } else {
            System.out.println("No hay libros registrados.");
        }
    }
}
