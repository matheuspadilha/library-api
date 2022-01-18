package br.com.matheuspadilha.libraryapi.api.resource;

import br.com.matheuspadilha.libraryapi.model.entity.Loan;
import br.com.matheuspadilha.libraryapi.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@RequestBody Loan dto){
       return service.save(dto);
    }
}
