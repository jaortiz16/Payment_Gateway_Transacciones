package com.banquito.gateway.transacciones.banquito.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.controller.mapper.TransaccionMapper;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.service.TransaccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/transacciones")
@Slf4j
@Tag(name = "Transacciones", description = "API para gestionar transacciones del payment gateway")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final TransaccionMapper mapper;

    public TransaccionController(TransaccionService transaccionService, TransaccionMapper mapper) {
        this.transaccionService = transaccionService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva transacción")
    public ResponseEntity<TransaccionDTO> crearTransaccion(@Valid @RequestBody TransaccionDTO transaccionDTO) {
        log.info("Recibiendo petición para crear transacción");
        Transaccion transaccion = this.transaccionService.crearTransaccionConDTO(transaccionDTO);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una transacción por su ID")
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(@PathVariable("id") String id) {
        log.info("Buscando transacción con ID: {}", id);
        Transaccion transaccion = this.transaccionService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping
    @Operation(summary = "Buscar transacciones por estado o rango de fechas")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorEstado(
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        List<Transaccion> transacciones;
        if (estado != null) {
            log.info("Buscando transacciones por estado: {}", estado);
            transacciones = this.transaccionService.obtenerTransaccionesPorEstado(estado);
        } else if (fechaInicio != null && fechaFin != null) {
            log.info("Buscando transacciones entre {} y {}", fechaInicio, fechaFin);
            transacciones = this.transaccionService.obtenerTransaccionesPorFecha(fechaInicio, fechaFin);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        return ResponseEntity.ok(transaccionesDTO);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de una transacción")
    public ResponseEntity<TransaccionDTO> actualizarEstado(
            @PathVariable("id") String id,
            @RequestParam("nuevoEstado") String nuevoEstado) {
        log.info("Actualizando estado de transacción {} a {}", id, nuevoEstado);
        Transaccion transaccion = this.transaccionService.actualizarEstadoTransaccion(id, nuevoEstado);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }
} 