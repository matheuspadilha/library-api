package br.com.matheuspadilha.libraryapi.service.impl;

import br.com.matheuspadilha.libraryapi.model.entity.Loan;
import br.com.matheuspadilha.libraryapi.service.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    public LoanServiceImpl() {}

    @Override
    public Loan save(Loan loan) {
        return null;
    }
}