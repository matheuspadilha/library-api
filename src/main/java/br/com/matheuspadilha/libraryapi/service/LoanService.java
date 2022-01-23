package br.com.matheuspadilha.libraryapi.service;

import br.com.matheuspadilha.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save (Loan loan);

    Optional<Loan> getById(Long id);

    void update(Loan loan);
}
