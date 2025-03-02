package com.banquito.gateway.transacciones.banquito.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionPosDTO {
    
    @NotBlank(message = "El código del POS es requerido")
    private String codigoPOS;
    
    @NotBlank(message = "El código del comercio es requerido")
    private String codigoComercio;
    
    @NotBlank(message = "El tipo de transacción es requerido")
    @Size(min = 3, max = 3, message = "El tipo debe tener exactamente 3 caracteres")
    private String tipo;
    
    @NotBlank(message = "La marca es requerida")
    @Size(min = 1, max = 4, message = "La marca debe tener entre 1 y 4 caracteres")
    private String marca;
    
    @NotBlank(message = "La modalidad es requerida")
    @Pattern(regexp = "SIM|DIF|REC", message = "La modalidad debe ser SIM (simple), DIF (diferida) o REC (recurrente)")
    private String modalidad;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    private BigDecimal monto;
    
    @NotBlank(message = "La moneda es requerida")
    @Size(min = 3, max = 3, message = "La moneda debe tener exactamente 3 caracteres")
    private String moneda;
    
    @NotBlank(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El país debe tener exactamente 2 caracteres (código ISO)")
    private String pais;
    
    private Integer plazo;
    
    @NotBlank(message = "El número de tarjeta es requerido")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe contener 16 dígitos")
    private String numeroTarjeta;
    
    @NotBlank(message = "El nombre del titular es requerido")
    private String nombreTitular;
    
    @NotNull(message = "El código de seguridad es requerido")
    @Digits(integer = 4, fraction = 0, message = "El código de seguridad debe ser un número de hasta 4 dígitos")
    private Integer codigoSeguridad;
    
    @NotBlank(message = "La fecha de expiración es requerida")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "La fecha de expiración debe tener el formato MM/YY")
    private String fechaExpiracion;
    
    @NotBlank(message = "El código único de transacción es requerido")
    private String codigoUnicoTransaccion;
    
    private String referencia;
    
    private Boolean recurrente;
    
    private Integer frecuenciaDias;
} 