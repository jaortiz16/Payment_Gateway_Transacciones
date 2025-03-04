package com.banquito.gateway.transacciones.banquito.client.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcesadorPagosDTO {
    
    private String tipo;
    private String marca;
    private String modalidad;
    private BigDecimal monto;
    private String moneda;
    private String pais;
    private String swift_banco;
    private String cuenta_iban;
    private String numeroTarjeta;
    private String nombreTitular;
    private Integer codigoSeguridad;
    private String fechaExpiracion;
    private String codigoUnicoTransaccion;
    private String referencia;
    private String transaccion_encriptada;
    private String codigoGtw;
    private Boolean diferido;
    private Integer cuotas;
    
    // Constructor para convertir desde TransaccionPosDTO
    public ProcesadorPagosDTO(com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionPosDTO posDTO, 
                             ComercioDTO comercioDTO) {
        this.tipo = posDTO.getTipo();
        this.marca = posDTO.getMarca();
        this.modalidad = posDTO.getModalidad();
        this.monto = posDTO.getMonto();
        this.moneda = posDTO.getMoneda();
        this.pais = posDTO.getPais();
        this.swift_banco = comercioDTO.getSwift_banco();
        this.cuenta_iban = comercioDTO.getCuenta_iban();
        this.numeroTarjeta = posDTO.getNumeroTarjeta();
        this.nombreTitular = posDTO.getNombreTitular();
        this.codigoSeguridad = posDTO.getCodigoSeguridad();
        this.fechaExpiracion = posDTO.getFechaExpiracion();
        this.codigoUnicoTransaccion = posDTO.getCodigoUnicoTransaccion();
        this.referencia = posDTO.getReferencia() != null ? posDTO.getReferencia() : "Compra en línea";
        this.transaccion_encriptada = "datos_no_encriptados_" + posDTO.getCodigoUnicoTransaccion();
        this.codigoGtw = "1234567890";
        
        // Solo asignar estos campos si es una transacción diferida
        if ("DIF".equals(posDTO.getModalidad())) {
            this.diferido = true;
            this.cuotas = posDTO.getPlazo();
        } else {
            this.diferido = false;
            this.cuotas = null;
        }
    }
} 