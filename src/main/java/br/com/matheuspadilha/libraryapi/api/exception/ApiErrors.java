package br.com.matheuspadilha.libraryapi.api.exception;

import br.com.matheuspadilha.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {
    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();

        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException ex) {
        this.errors = List.of(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
