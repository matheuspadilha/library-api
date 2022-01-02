package br.com.matheuspadilha.libraryapi.service;

import br.com.matheuspadilha.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book any);
}
