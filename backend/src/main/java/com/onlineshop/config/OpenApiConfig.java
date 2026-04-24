package com.onlineshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Online Shop API")
                                                .version("1.0.0")
                                                .description("REST API for Online Shop project. " +
                                                                "Demonstrates clean architecture, JPA, MapStruct, Testcontainers, "
                                                                +
                                                                "Flyway and proper testing practices.")
                                                .contact(new Contact()
                                                                .name("Mariusz Andrzejewski")
                                                                .email("mariusz.andrzejewski.it@gmail.com")
                                                                .url("https://github.com/marand85/online-shop"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")));
        }
}
