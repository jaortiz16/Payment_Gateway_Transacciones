package com.banquito.payment_gateway.transacciones.banquito.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.repository.TransaccionRepository;
import com.banquito.gateway.transacciones.banquito.service.TransaccionRecurrenteService;
import com.banquito.gateway.transacciones.banquito.service.TransaccionService;

@ExtendWith(MockitoExtension.class)
public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private TransaccionRecurrenteService transaccionRecurrenteService;

    @InjectMocks
    private TransaccionService transaccionService;

    private Transaccion transaccion;
    private TransaccionDTO transaccionDTO;

    @BeforeEach
    void setUp() {
        // Configurar una transacción de prueba
        transaccion = new Transaccion();
        transaccion.setCodTransaccion("TX12345678");
        transaccion.setTipo("PAG");
        transaccion.setMarca("VISA");
        transaccion.setMonto(new BigDecimal("100.00"));
        transaccion.setCodigoUnicoTransaccion("UUID-12345-67890");
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setEstado("PEN");
        transaccion.setMoneda("USD");
        transaccion.setPais("EC");
        transaccion.setTarjeta("1234567890123456");
        transaccion.setFechaCaducidad(LocalDate.now().plusYears(2));
        transaccion.setDiferido(false);

        // Configurar un DTO de transacción
        transaccionDTO = new TransaccionDTO();
        transaccionDTO.setTipo("PAG");
        transaccionDTO.setMarca("VISA");
        transaccionDTO.setMonto(new BigDecimal("100.00"));
        transaccionDTO.setMoneda("USD");
        transaccionDTO.setPais("EC");
        transaccionDTO.setTarjeta("1234567890123456");
        transaccionDTO.setFechaCaducidad(LocalDate.now().plusYears(2));
        transaccionDTO.setDiferido(false);
    }

    @Test
    void testCrearTransaccion() {
        // Configurar comportamiento del mock
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        // Ejecutar el método a probar
        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals("PEN", resultado.getEstado());
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    void testCrearTransaccionConDTO() {
        // Configurar comportamiento del mock
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        // Ejecutar el método a probar
        Transaccion resultado = transaccionService.crearTransaccionConDTO(transaccionDTO);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals("PEN", resultado.getEstado());
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    void testCrearTransaccionConDTODiferido() {
        // Configurar transacción diferida
        transaccionDTO.setDiferido(true);
        
        // Configurar comportamiento del mock
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
        doNothing().when(transaccionRecurrenteService).enviarTransaccionRecurrente(any(TransaccionDTO.class));

        // Ejecutar el método a probar
        Transaccion resultado = transaccionService.crearTransaccionConDTO(transaccionDTO);

        // Verificar resultados
        assertNotNull(resultado);
        verify(transaccionRecurrenteService, times(1)).enviarTransaccionRecurrente(any(TransaccionDTO.class));
    }

    @Test
    void testObtenerTransaccionPorId() {
        // Configurar comportamiento del mock
        when(transaccionRepository.findById("TX12345678")).thenReturn(Optional.of(transaccion));

        // Ejecutar el método a probar
        Transaccion resultado = transaccionService.obtenerTransaccionPorId("TX12345678");

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals("TX12345678", resultado.getCodTransaccion());
    }

    @Test
    void testObtenerTransaccionPorIdNoEncontrada() {
        // Configurar comportamiento del mock
        when(transaccionRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        // Verificar que se lance la excepción
        assertThrows(TransaccionNotFoundException.class, () -> {
            transaccionService.obtenerTransaccionPorId("NOEXISTE");
        });
    }

    @Test
    void testObtenerTransaccionesPorEstado() {
        // Configurar comportamiento del mock
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        when(transaccionRepository.findByEstado("PEN")).thenReturn(transacciones);

        // Ejecutar el método a probar
        List<Transaccion> resultado = transaccionService.obtenerTransaccionesPorEstado("PEN");

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PEN", resultado.get(0).getEstado());
    }

    @Test
    void testActualizarEstadoTransaccion() {
        // Configurar comportamiento del mock
        when(transaccionRepository.findById("TX12345678")).thenReturn(Optional.of(transaccion));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        // Ejecutar el método a probar
        Transaccion resultado = transaccionService.actualizarEstadoTransaccion("TX12345678", "ACT");

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals("ACT", resultado.getEstado());
    }

    @Test
    void testActualizarEstadoTransaccionInvalido() {
        // Configurar comportamiento del mock
        when(transaccionRepository.findById("TX12345678")).thenReturn(Optional.of(transaccion));

        // Verificar que se lance la excepción
        assertThrows(TransaccionInvalidaException.class, () -> {
            transaccionService.actualizarEstadoTransaccion("TX12345678", "INVALIDO");
        });
    }

    @Test
    void testObtenerTransaccionesPorFecha() {
        // Configurar comportamiento del mock
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        when(transaccionRepository.findByFechaBetween(fechaInicio, fechaFin)).thenReturn(transacciones);

        // Ejecutar el método a probar
        List<Transaccion> resultado = transaccionService.obtenerTransaccionesPorFecha(fechaInicio, fechaFin);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
} 