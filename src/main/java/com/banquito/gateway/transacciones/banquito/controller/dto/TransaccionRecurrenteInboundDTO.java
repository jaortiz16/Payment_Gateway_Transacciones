package com.banquito.gateway.transacciones.banquito.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionRecurrenteInboundDTO {
    
    @NotBlank(message = "La marca es requerida")
    private String marca;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    private BigDecimal monto;
    
    @NotBlank(message = "La moneda es requerida")
    @Pattern(regexp = "USD|EUR|GBP", message = "La moneda debe ser USD, EUR o GBP")
    private String moneda;
    
    @NotBlank(message = "El país es requerido")
    @Pattern(regexp = "[A-Z]{2}", message = "El país debe tener 2 caracteres ISO")
    private String pais;
    
    @NotBlank(message = "El número de tarjeta es requerido")
    @Pattern(regexp = "\\d{15,19}", message = "El número de tarjeta debe contener entre 15 y 19 dígitos")
    private String numeroTarjeta;
    
    @NotBlank(message = "La fecha de expiración es requerida")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "La fecha de expiración debe tener el formato MM/YY")
    private String fechaExpiracion;
    
    @NotBlank(message = "El código SWIFT del banco es requerido")
    private String swift_banco;
    
    @NotBlank(message = "La cuenta IBAN es requerida")
    private String cuenta_iban;
    
    @NotNull(message = "El CVV es requerido")
    @Digits(integer = 4, fraction = 0, message = "El CVV debe ser un número de hasta 4 dígitos")
    private Integer cvv;
    
    @NotNull(message = "La frecuencia en días es requerida")
    @Digits(integer = 3, fraction = 0, message = "La frecuencia debe ser un número de hasta 3 dígitos")
    private Integer frecuenciaDias;
} 