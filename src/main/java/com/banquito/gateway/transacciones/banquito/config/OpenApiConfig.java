package com.banquito.gateway.transacciones.banquito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
            .url("http://localhost:8080/api")
            .description("Servidor Local");

        return new OpenAPI()
                .info(new Info()
                        .title("API de Payment Gateway - Transacciones")
                        .version("1.0")
                        .description("API para gestionar transacciones del payment gateway")
                        .contact(new Contact()
                                .name("Banquito")
                                .email("info@banquito.com")))
                .servers(List.of(localServer));
    }
}