package br.com.matheuspadilha.libraryapi.model.repository;

import br.com.matheuspadilha.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
