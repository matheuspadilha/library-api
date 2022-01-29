package br.com.matheuspadilha.libraryapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
		title = "Library API",
		description = "API do Projeto de controle de aluguel de livros",
		version = "1.0",
		contact = @Contact(
				name = "Matheus Padilha",
				url = "https://github.com/matheuspadilha",
				email = "contato@matheuspadilha.com.br"
		)
))
public class LibraryApiApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
