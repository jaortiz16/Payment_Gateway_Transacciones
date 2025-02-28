package com.banquito.payment_gateway.transacciones.banquito.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.banquito.gateway.transacciones.banquito.client.TransaccionRecurrenteClient;
import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transacciones.banquito.client.mapper.TransaccionRecurrenteMapper;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionRecurrenteException;
import com.banquito.gateway.transacciones.banquito.service.TransaccionRecurrenteService;

@ExtendWith(MockitoExtension.class)
public class TransaccionRecurrenteServiceTest {

    @Mock
    private TransaccionRecurrenteClient transaccionRecurrenteClient;

    @Mock
    private TransaccionRecurrenteMapper transaccionRecurrenteMapper;

    @InjectMocks
    private TransaccionRecurrenteService transaccionRecurrenteService;

    private TransaccionDTO transaccionDTO;
    private TransaccionRecurrenteDTO transaccionRecurrenteDTO;

    @BeforeEach
    void setUp() {
        // Configurar un DTO de transacción
        transaccionDTO = new TransaccionDTO();
        transaccionDTO.setTipo("PAG");
        transaccionDTO.setMarca("VISA");
        transaccionDTO.setMonto(new BigDecimal("100.00"));
        transaccionDTO.setMoneda("USD");
        transaccionDTO.setPais("EC");
        transaccionDTO.setTarjeta("1234567890123456");
        transaccionDTO.setFechaCaducidad(LocalDate.now().plusYears(2));
        transaccionDTO.setDiferido(true);

        // Configurar un DTO de transacción recurrente
        transaccionRecurrenteDTO = new TransaccionRecurrenteDTO();
        // Configurar propiedades según la estructura de TransaccionRecurrenteDTO
    }

    @Test
    void testEnviarTransaccionRecurrenteExitoso() {
        // Configurar comportamiento de los mocks
        when(transaccionRecurrenteMapper.toTransaccionRecurrenteDTO(any(TransaccionDTO.class)))
                .thenReturn(transaccionRecurrenteDTO);
        when(transaccionRecurrenteClient.crearTransaccionRecurrente(any(TransaccionRecurrenteDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        // Ejecutar el método a probar
        assertDoesNotThrow(() -> {
            transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
        });

        // Verificar que se llamaron los métodos esperados
        verify(transaccionRecurrenteMapper, times(1)).toTransaccionRecurrenteDTO(any(TransaccionDTO.class));
        verify(transaccionRecurrenteClient, times(1)).crearTransaccionRecurrente(any(TransaccionRecurrenteDTO.class));
    }

    @Test
    void testEnviarTransaccionRecurrenteRespuestaError() {
        // Configurar comportamiento de los mocks
        when(transaccionRecurrenteMapper.toTransaccionRecurrenteDTO(any(TransaccionDTO.class)))
                .thenReturn(transaccionRecurrenteDTO);
        when(transaccionRecurrenteClient.crearTransaccionRecurrente(any(TransaccionRecurrenteDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Verificar que se lance la excepción
        assertThrows(TransaccionRecurrenteException.class, () -> {
            transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
        });
    }

    @Test
    void testEnviarTransaccionRecurrenteExcepcion() {
        // Configurar comportamiento de los mocks
        when(transaccionRecurrenteMapper.toTransaccionRecurrenteDTO(any(TransaccionDTO.class)))
                .thenReturn(transaccionRecurrenteDTO);
        when(transaccionRecurrenteClient.crearTransaccionRecurrente(any(TransaccionRecurrenteDTO.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Verificar que se lance la excepción
        assertThrows(TransaccionRecurrenteException.class, () -> {
            transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
        });
    }
} 