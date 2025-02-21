package com.banquito.payment_gateway.transacciones.banquito.controller;

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

import com.banquito.payment_gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.payment_gateway.transacciones.banquito.controller.mapper.TransaccionMapper;
import com.banquito.payment_gateway.transacciones.banquito.model.Transaccion;
import com.banquito.payment_gateway.transacciones.banquito.service.TransaccionService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transacciones")
@Slf4j
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final TransaccionMapper mapper;

    public TransaccionController(TransaccionService transaccionService, TransaccionMapper mapper) {
        this.transaccionService = transaccionService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<TransaccionDTO> crearTransaccion(@Valid @RequestBody TransaccionDTO transaccionDTO) {
        log.info("Recibiendo petición para crear transacción");
        Transaccion transaccion = this.transaccionService.crearTransaccion(this.mapper.toModel(transaccionDTO));
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(@PathVariable("id") String id) {
        log.info("Buscando transacción con ID: {}", id);
        Transaccion transaccion = this.transaccionService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping
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
    public ResponseEntity<TransaccionDTO> actualizarEstado(
            @PathVariable("id") String id,
            @RequestParam("nuevoEstado") String nuevoEstado) {
        log.info("Actualizando estado de transacción {} a {}", id, nuevoEstado);
        Transaccion transaccion = this.transaccionService.actualizarEstadoTransaccion(id, nuevoEstado);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }
} 