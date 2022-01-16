package br.com.matheuspadilha.libraryapi.api.resource;

import br.com.matheuspadilha.libraryapi.api.dto.BookDTO;
import br.com.matheuspadilha.libraryapi.exception.BusinessException;
import br.com.matheuspadilha.libraryapi.model.entity.Book;
import br.com.matheuspadilha.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    void createBookTest() throws Throwable {
        //cenario
        BookDTO dto = createNewBookDTO();
        Book savedBook = Book.builder().id(1L).author("Matheus").title("Rock Balboa").isbn("0123").build();
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
    void createInvalidBookTest() throws Exception {
        //cenario
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    void createBookWithDuplicatedIsbn() throws Exception {
        //cenario
        BookDTO dto = createNewBookDTO();
        String mensagemErro = "Isbn já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));
        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro))
        ;
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    void getBookDetailsTest() throws Exception {
        // cenario (given)
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .author(createNewBookDTO().getAuthor())
                .title(createNewBookDTO().getTitle())
                .isbn(createNewBookDTO().getIsbn())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(createNewBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createNewBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBookDTO().getIsbn()))
        ;
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não existir")
    void bookNotFoundTest() throws Exception {
        // cenario (given)
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void deleteBookTest() throws Exception {
        // cenario (given)
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        // verificacao (then)
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar")
    void deleteInexistentBookTest() throws Exception {
        // cenario (given)
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        // verificacao (then)
        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    void updateBookTest() throws Exception {
        // cenario (given)
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBookDTO());

        Book updatingBook = Book.builder().id(1L).title("Outro title").author("Sem Author").build();
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(1L).author("Matheus").title("Rock Balboa").isbn("0123").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(createNewBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createNewBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value("0123"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    void updateInexistentBookTest() throws Exception {
        // cenario (given)
        String json = new ObjectMapper().writeValueAsString(createNewBookDTO());
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros")
    void findBookTest() throws Exception {
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBookDTO().getTitle())
                .author(createNewBookDTO().getAuthor())
                .isbn(createNewBookDTO().getIsbn())
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Book>(List.of(book), PageRequest.of(0,100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(),
                book.getAuthor()
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }


    private BookDTO createNewBookDTO() {
        return BookDTO.builder().author("Matheus").title("Rock Balboa").isbn("0123").build();
    }
}
