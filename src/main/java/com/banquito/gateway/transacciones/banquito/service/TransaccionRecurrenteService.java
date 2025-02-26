package com.banquito.gateway.transacciones.banquito.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.banquito.gateway.transacciones.banquito.client.TransaccionRecurrenteClient;
import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transacciones.banquito.client.mapper.TransaccionRecurrenteMapper;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionRecurrenteException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransaccionRecurrenteService {

    private final TransaccionRecurrenteClient transaccionRecurrenteClient;
    private final TransaccionRecurrenteMapper transaccionRecurrenteMapper;

    public TransaccionRecurrenteService(TransaccionRecurrenteClient transaccionRecurrenteClient,
                                       TransaccionRecurrenteMapper transaccionRecurrenteMapper) {
        this.transaccionRecurrenteClient = transaccionRecurrenteClient;
        this.transaccionRecurrenteMapper = transaccionRecurrenteMapper;
    }

    public void enviarTransaccionRecurrente(TransaccionDTO transaccionDTO) {
        log.info("Enviando transacción recurrente para la tarjeta: {}", transaccionDTO.getTarjeta());
        
        try {
            TransaccionRecurrenteDTO transaccionRecurrenteDTO = transaccionRecurrenteMapper.toTransaccionRecurrenteDTO(transaccionDTO);
            
            log.debug("Datos de transacción recurrente a enviar: {}", transaccionRecurrenteDTO);
            
            ResponseEntity<Object> respuesta = transaccionRecurrenteClient.crearTransaccionRecurrente(transaccionRecurrenteDTO);
            
            if (!respuesta.getStatusCode().is2xxSuccessful()) {
                log.error("Error al enviar transacción recurrente. Código de respuesta: {}", respuesta.getStatusCode());
                throw new TransaccionRecurrenteException("Error al enviar transacción recurrente. Código de respuesta: " + respuesta.getStatusCode());
            }
            
            log.info("Transacción recurrente enviada exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar transacción recurrente", e);
            throw new TransaccionRecurrenteException("Error al procesar transacción recurrente: " + e.getMessage());
        }
    }
} 