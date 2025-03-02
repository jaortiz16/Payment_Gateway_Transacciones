package com.banquito.gateway.transacciones.banquito.client.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcesadorPagosDTO {
    
    private String codigoPOS;
    private String codigoComercio;
    private String tipo;
    private String marca;
    private BigDecimal monto;
    private String moneda;
    private String numeroTarjeta;
    private String nombreTitular;
    private Integer codigoSeguridad;
    private String fechaExpiracion;
    private String codigoUnicoTransaccion;
    
    public ProcesadorPagosDTO(com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionPosDTO posDTO) {
        this.codigoPOS = posDTO.getCodigoPOS();
        this.codigoComercio = posDTO.getCodigoComercio();
        this.tipo = posDTO.getTipo();
        this.marca = posDTO.getMarca();
        this.monto = posDTO.getMonto();
        this.moneda = posDTO.getMoneda();
        this.numeroTarjeta = posDTO.getNumeroTarjeta();
        this.nombreTitular = posDTO.getNombreTitular();
        this.codigoSeguridad = posDTO.getCodigoSeguridad();
        this.fechaExpiracion = posDTO.getFechaExpiracion();
        this.codigoUnicoTransaccion = posDTO.getCodigoUnicoTransaccion();
    }
} 