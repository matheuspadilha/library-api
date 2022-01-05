package br.com.matheuspadilha.libraryapi.service;

import br.com.matheuspadilha.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
