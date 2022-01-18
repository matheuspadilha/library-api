package br.com.matheuspadilha.libraryapi.api.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {

    private String isbn;
    private String customer;
}
