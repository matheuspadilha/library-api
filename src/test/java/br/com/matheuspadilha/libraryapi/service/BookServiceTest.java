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
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        book.setId(1L);
        Mockito.when(repository.save(book)).thenReturn(book);

        // execucao
        Book savedBook = service.save(book);

        // verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("01432123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("El Padilhon");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado ")
    void shouldNotSaveABookWithDuplicatedISBN() {
        // cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    void getByIdTest() {
        // cenario
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

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
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base")
    void bookNotFoundByIdTest() {
        // cenario
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

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
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    void deleteInvalidBookTest() {
        // cenario
        Book book = new Book();

        // execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // verificacao
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    private Book createValidBook() {
        return Book.builder().isbn("01432123").author("El Padilhon").title("As aventuras").build();
    }
}
