package com.banquito.gateway.transacciones.banquito.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionDTO {
    
    private String codTransaccion;

    @NotBlank(message = "El tipo de transacción es requerido")
    @Size(min = 3, max = 3, message = "El tipo debe tener exactamente 3 caracteres")
    private String tipo;

    @NotBlank(message = "La marca es requerida")
    @Size(min = 1, max = 4, message = "La marca debe tener entre 1 y 4 caracteres")
    private String marca;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    private BigDecimal monto;

    private String codigoUnicoTransaccion;

    private LocalDateTime fecha;

    @Pattern(regexp = "ACT|INA|PEN", message = "El estado debe ser ACT, INA o PEN")
    private String estado;

    @NotBlank(message = "La moneda es requerida")
    @Size(min = 3, max = 3, message = "La moneda debe tener exactamente 3 caracteres")
    private String moneda;

    @NotBlank(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El país debe tener exactamente 2 caracteres")
    private String pais;

    @NotBlank(message = "El número de tarjeta es requerido")
    @Size(min = 16, max = 16, message = "El número de tarjeta debe tener 16 dígitos")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe contener solo dígitos")
    private String tarjeta;

    @NotNull(message = "La fecha de caducidad es requerida")
    @Future(message = "La fecha de caducidad debe ser futura")
    private LocalDate fechaCaducidad;

    private String transaccionEncriptada;

    @Size(max = 11, message = "El código SWIFT del banco no puede exceder 11 caracteres")
    private String swiftBanco;

    @Size(max = 28, message = "La cuenta IBAN no puede exceder 28 caracteres")
    private String cuentaIban;

    private Boolean diferido;
    
    private Boolean esDiferido;

    private Integer diaMesPago;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Integer codigoSeguridad;

    private Integer frecuenciaDias;
} 