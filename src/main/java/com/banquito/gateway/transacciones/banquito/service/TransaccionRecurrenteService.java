package com.banquito.gateway.transacciones.banquito.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.banquito.gateway.transacciones.banquito.client.TransaccionRecurrenteClient;
import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransaccionRecurrenteService {

    private final TransaccionRecurrenteClient transaccionRecurrenteClient;

    public TransaccionRecurrenteService(TransaccionRecurrenteClient transaccionRecurrenteClient) {
        this.transaccionRecurrenteClient = transaccionRecurrenteClient;
    }

    public void enviarTransaccionRecurrente(TransaccionDTO transaccionDTO) {
        log.info("Preparando envío de transacción recurrente para tarjeta: {}", transaccionDTO.getTarjeta());
        
        TransaccionRecurrenteDTO recurrenteDTO = new TransaccionRecurrenteDTO();
        recurrenteDTO.setCodigo(transaccionDTO.getCodigoUnicoTransaccion());
        recurrenteDTO.setTarjeta(Long.parseLong(transaccionDTO.getTarjeta()));
        recurrenteDTO.setMonto(transaccionDTO.getMonto());
        recurrenteDTO.setMoneda(transaccionDTO.getMoneda());
        recurrenteDTO.setPais(transaccionDTO.getPais());
        recurrenteDTO.setMarca(transaccionDTO.getMarca());
        recurrenteDTO.setEstado("ACT");
        recurrenteDTO.setDiaMesPago(transaccionDTO.getDiaMesPago());
        recurrenteDTO.setFechaInicio(transaccionDTO.getFechaInicio());
        recurrenteDTO.setFechaFin(transaccionDTO.getFechaFin());
        recurrenteDTO.setFechaCaducidad(transaccionDTO.getFechaCaducidad());
        
        try {
            log.info("Enviando transacción recurrente al servicio externo con código: {}", recurrenteDTO.getCodigo());
            ResponseEntity<Object> respuesta = this.transaccionRecurrenteClient.crearTransaccionRecurrente(recurrenteDTO);
            log.info("Respuesta del servicio de transacciones recurrentes: {}", respuesta.getStatusCode());
        } catch (Exception e) {
            log.error("Error al enviar transacción recurrente: {}", e.getMessage());
            throw e;
        }
    }
} 