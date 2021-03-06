package br.com.matheuspadilha.libraryapi.service;

import br.com.matheuspadilha.libraryapi.exception.BusinessException;
import br.com.matheuspadilha.libraryapi.model.entity.Book;
import br.com.matheuspadilha.libraryapi.model.repository.BookRepository;
import br.com.matheuspadilha.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    void saveBookTest() {
        // cenario
        Book book = createValidBook();
        Book createBook = createValidBook();
        createBook.setId(1L);
        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(createBook);

        // execucao
        Book savedBook = service.save(book);

        // verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo(createBook.getIsbn());
        assertThat(savedBook.getTitle()).isEqualTo(createBook.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(createBook.getAuthor());
    }

    @Test
    @DisplayName("Deve lan??ar erro de negocio ao tentar salvar um livro com isbn duplicado ")
    void shouldNotSaveABookWithDuplicatedISBN() {
        // cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        // execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn j?? cadastrado");

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    void getByIdTest() {
        // cenario
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        // execucao
        Optional<Book> foundBook = service.getById(id);

        // verificacao
        assertThat(foundBook.isEmpty()).isFalse();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele n??o existe na base")
    void bookNotFoundByIdTest() {
        // cenario
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Optional<Book> book = service.getById(id);

        // verificacao
        assertThat(book.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Deve delete um livro")
    void deleteBookTest() {
        // cenario
        Book book = Book.builder().id(1L).build();

        // execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        // verificacao
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    void deleteInvalidBookTest() {
        // cenario
        Book book = new Book();

        // execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // verificacao
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    void updateInvalidBookTest() {
        // cenario
        Book book = new Book();

        // execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        // verificacao
        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    void updateBookTest() {
        // cenario
        Long id = 1L;
        // livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        // simulacao
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        when(repository.save(updatingBook)).thenReturn(updatedBook);

        // execucao
        Book book = service.update(updatingBook);

        // verificacao
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    void findBookTest() {
        // cenario
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = List.of(book);
        Page<Book> page = new PageImpl<>(list, pageRequest, 1);
        when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        // execucao
        Page<Book> result = service.find(book, pageRequest);

        // verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    void getBookByIsbnTest() {
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }


    private Book createValidBook() {
        return Book.builder().isbn("01432123").author("El Padilhon").title("As aventuras").build();
    }
}
