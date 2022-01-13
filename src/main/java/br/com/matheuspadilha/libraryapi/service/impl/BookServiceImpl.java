package br.com.matheuspadilha.libraryapi.service.impl;

import br.com.matheuspadilha.libraryapi.exception.BusinessException;
import br.com.matheuspadilha.libraryapi.model.entity.Book;
import br.com.matheuspadilha.libraryapi.model.repository.BookRepository;
import br.com.matheuspadilha.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }

        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (Objects.isNull(book) || Objects.isNull(book.getId())) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (Objects.isNull(book) || Objects.isNull(book.getId())) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        return repository.save(book);
    }
}
