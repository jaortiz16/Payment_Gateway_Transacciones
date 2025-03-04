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
        
        try {
            // Verificar primero si el servicio está disponible
            try {
                ResponseEntity<Object> pingResponse = this.transaccionRecurrenteClient.ping();
                log.info("Ping al servicio de transacciones recurrentes: {}", pingResponse.getStatusCode());
            } catch (Exception e) {
                log.error("Error al hacer ping al servicio de transacciones recurrentes: {}", e.getMessage());
                log.warn("Continuando con el intento de envío a pesar del error de ping");
            }
            
            // Crear y configurar el DTO específico para transacciones recurrentes
            TransaccionRecurrenteDTO recurrenteDTO = new TransaccionRecurrenteDTO();
            
            // Mapear los campos básicos
            recurrenteDTO.setMonto(transaccionDTO.getMonto());
            recurrenteDTO.setMarca(transaccionDTO.getMarca());
            recurrenteDTO.setEstado("ACT"); // Estado activo por defecto
            recurrenteDTO.setMoneda(transaccionDTO.getMoneda());
            recurrenteDTO.setPais(transaccionDTO.getPais());
            
            // Convertir el número de tarjeta a Long
            try {
                recurrenteDTO.setTarjeta(Long.parseLong(transaccionDTO.getTarjeta().replaceAll("\\D", "")));
            } catch (NumberFormatException e) {
                log.warn("No se pudo convertir el número de tarjeta a Long: {}", transaccionDTO.getTarjeta());
                // Usar un valor por defecto
                recurrenteDTO.setTarjeta(4111111111111111L);
            }
            
            // Configurar campos específicos de recurrencia
            recurrenteDTO.setFechaInicio(transaccionDTO.getFechaInicio());
            recurrenteDTO.setFechaFin(transaccionDTO.getFechaFin());
            recurrenteDTO.setDiaMesPago(transaccionDTO.getDiaMesPago());
            recurrenteDTO.setFechaCaducidad(transaccionDTO.getFechaCaducidad());
            
            // Configurar campos opcionales con valores predeterminados si son nulos
            recurrenteDTO.setSwiftBanco(transaccionDTO.getSwiftBanco() != null ? 
                                   transaccionDTO.getSwiftBanco() : "PICHEERT");
            recurrenteDTO.setCuentaIban(transaccionDTO.getCuentaIban() != null ? 
                                   transaccionDTO.getCuentaIban() : "EC123456789012345678905678");
            
            // Añadir los nuevos campos requeridos
            recurrenteDTO.setCvv(transaccionDTO.getCodigoSeguridad() != null ? 
                               transaccionDTO.getCodigoSeguridad().toString() : "123");
            recurrenteDTO.setFrecuenciaDias(transaccionDTO.getFrecuenciaDias());
            
            log.info("Enviando transacción recurrente al servicio externo: {}", recurrenteDTO);
            ResponseEntity<Object> respuesta = this.transaccionRecurrenteClient.crearTransaccionRecurrente(recurrenteDTO);
            log.info("Respuesta del servicio de transacciones recurrentes: {}", respuesta.getStatusCode());
        } catch (Exception e) {
            log.error("Error al enviar transacción recurrente: {}", e.getMessage());
            // No lanzamos la excepción para evitar que falle la transacción principal
            log.warn("La transacción principal continuará a pesar del error en la recurrencia");
        }
    }
} 