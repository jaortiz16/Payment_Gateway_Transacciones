package com.banquito.gateway.transacciones.banquito.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionRecurrenteDTO {
    
    private String codigo;
    private BigDecimal monto;
    private String marca;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diaMesPago;
    private String swiftBanco;
    private String cuentaIban;
    private String moneda;
    private String pais;
    private Long tarjeta;
    private LocalDate fechaCaducidad;
} 