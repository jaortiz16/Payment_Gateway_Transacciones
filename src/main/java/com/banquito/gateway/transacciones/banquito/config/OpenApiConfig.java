package com.banquito.gateway.transacciones.banquito.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("API de Transacciones de Banquito")
                        .description("API para gestionar transacciones del payment gateway de Banquito")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo de Banquito")
                                .email("dev@banquito.com")
                                .url("https://www.banquito.com"))
                        .license(new License()
                                .name("Licencia Banquito")
                                .url("https://www.banquito.com/licencia")));
    }
}