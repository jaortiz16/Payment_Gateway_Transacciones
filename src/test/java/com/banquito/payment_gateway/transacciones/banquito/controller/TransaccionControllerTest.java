package com.banquito.payment_gateway.transacciones.banquito.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.banquito.gateway.transacciones.banquito.controller.TransaccionController;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.controller.mapper.TransaccionMapper;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.service.TransaccionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(TransaccionController.class)
public class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransaccionService transaccionService;

    @MockBean
    private TransaccionMapper mapper;

    private ObjectMapper objectMapper;
    private Transaccion transaccion;
    private TransaccionDTO transaccionDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
        transaccionDTO.setCodTransaccion("TX12345678");
        transaccionDTO.setTipo("PAG");
        transaccionDTO.setMarca("VISA");
        transaccionDTO.setMonto(new BigDecimal("100.00"));
        transaccionDTO.setCodigoUnicoTransaccion("UUID-12345-67890");
        transaccionDTO.setFecha(LocalDateTime.now());
        transaccionDTO.setEstado("PEN");
        transaccionDTO.setMoneda("USD");
        transaccionDTO.setPais("EC");
        transaccionDTO.setTarjeta("1234567890123456");
        transaccionDTO.setFechaCaducidad(LocalDate.now().plusYears(2));
        transaccionDTO.setDiferido(false);
    }

    @Test
    void testCrearTransaccion() throws Exception {
        // Configurar comportamiento de los mocks
        when(transaccionService.crearTransaccionConDTO(any(TransaccionDTO.class))).thenReturn(transaccion);
        when(mapper.toDTO(any(Transaccion.class))).thenReturn(transaccionDTO);

        // Ejecutar la solicitud POST
        mockMvc.perform(post("/api/v1/transacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaccionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codTransaccion").value("TX12345678"))
                .andExpect(jsonPath("$.estado").value("PEN"));
    }

    @Test
    void testObtenerTransaccion() throws Exception {
        // Configurar comportamiento de los mocks
        when(transaccionService.obtenerTransaccionPorId("TX12345678")).thenReturn(transaccion);
        when(mapper.toDTO(any(Transaccion.class))).thenReturn(transaccionDTO);

        // Ejecutar la solicitud GET
        mockMvc.perform(get("/api/v1/transacciones/TX12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codTransaccion").value("TX12345678"))
                .andExpect(jsonPath("$.estado").value("PEN"));
    }

    @Test
    void testObtenerTransaccionNoEncontrada() throws Exception {
        // Configurar comportamiento de los mocks
        when(transaccionService.obtenerTransaccionPorId("NOEXISTE"))
                .thenThrow(new TransaccionNotFoundException("NOEXISTE"));

        // Ejecutar la solicitud GET
        mockMvc.perform(get("/api/v1/transacciones/NOEXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerTransaccionesPorEstado() throws Exception {
        // Configurar comportamiento de los mocks
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        when(transaccionService.obtenerTransaccionesPorEstado("PEN")).thenReturn(transacciones);
        when(mapper.toDTO(any(Transaccion.class))).thenReturn(transaccionDTO);

        // Ejecutar la solicitud GET
        mockMvc.perform(get("/api/v1/transacciones?estado=PEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codTransaccion").value("TX12345678"))
                .andExpect(jsonPath("$[0].estado").value("PEN"));
    }

    @Test
    void testObtenerTransaccionesPorFecha() throws Exception {
        // Configurar comportamiento de los mocks
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
        
        String fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_DATE_TIME);
        String fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_DATE_TIME);
        
        when(transaccionService.obtenerTransaccionesPorFecha(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transacciones);
        when(mapper.toDTO(any(Transaccion.class))).thenReturn(transaccionDTO);

        // Ejecutar la solicitud GET
        mockMvc.perform(get("/api/v1/transacciones")
                .param("fechaInicio", fechaInicioStr)
                .param("fechaFin", fechaFinStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codTransaccion").value("TX12345678"));
    }

    @Test
    void testActualizarEstado() throws Exception {
        // Configurar comportamiento de los mocks
        when(transaccionService.actualizarEstadoTransaccion(anyString(), anyString())).thenReturn(transaccion);
        when(mapper.toDTO(any(Transaccion.class))).thenReturn(transaccionDTO);

        // Ejecutar la solicitud PATCH
        mockMvc.perform(patch("/api/v1/transacciones/TX12345678/estado")
                .param("nuevoEstado", "ACT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codTransaccion").value("TX12345678"));
    }
} 