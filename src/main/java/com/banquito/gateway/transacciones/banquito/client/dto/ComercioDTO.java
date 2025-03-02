package com.banquito.gateway.transacciones.banquito.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComercioDTO {
    
    private String codigo_comercio;
    private String nombre_comercio;
    private String swift_banco;
    private String cuenta_iban;
    private String estado;
} 