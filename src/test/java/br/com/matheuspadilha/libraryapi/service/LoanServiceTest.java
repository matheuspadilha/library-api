package br.com.matheuspadilha.libraryapi.service;

import br.com.matheuspadilha.libraryapi.api.dto.LoanFilterDTO;
import br.com.matheuspadilha.libraryapi.exception.BusinessException;
import br.com.matheuspadilha.libraryapi.model.entity.Book;
import br.com.matheuspadilha.libraryapi.model.entity.Loan;
import br.com.matheuspadilha.libraryapi.model.repository.LoanRepository;
import br.com.matheuspadilha.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public
class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    void setUp() {
        service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    void saveLoanTest() {
        Loan savingLoan = createLoan();
        Long id = 1L;
        Loan savedLoan = createLoan();
        savedLoan.setId(id);

        when(repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lan??ar erro de negocio ao salvar um emprestimo com livro ja emprestado")
    void loanedBookSaveTest() {
        Loan savingLoan = createLoan();

        when(repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class);

        verify(repository, never()).save(savingLoan);
        verify(repository, times(1)).existsByBookAndNotReturned(savingLoan.getBook());
    }

    @Test
    @DisplayName("Deve obter as informa????es de um empr??stimo pelo ID")
    void getLoanDetailsTest() {
        //cenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execucao
        Optional<Loan> result = service.getById(id);

        //verificacao
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    void updateLoanTest() {
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository, times(1)).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    void findLoanTest() {
        // cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn("321").customer("Padilha").build();

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = List.of(loan);
        Page<Loan> page = new PageImpl<>(list, pageRequest, list.size());
        when(repository.findByBookIsbnOrCustomer(anyString(), anyString(), any(PageRequest.class))).thenReturn(page);

        // execucao
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        // verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Padilha";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
}
