package com.banquito.gateway.transacciones.banquito.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.repository.TransaccionRepository;

import lombok.extern.slf4j.Slf4j;
//test
@Service
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionRecurrenteService transaccionRecurrenteService;

    public TransaccionService(TransaccionRepository transaccionRepository, 
                             TransaccionRecurrenteService transaccionRecurrenteService) {
        this.transaccionRepository = transaccionRepository;
        this.transaccionRecurrenteService = transaccionRecurrenteService;
    }

    @Transactional
    public Transaccion crearTransaccion(Transaccion transaccion) {
        log.info("Iniciando creación de transacción");
        
        validarTransaccion(transaccion);
        
        transaccion.setCodTransaccion(UUID.randomUUID().toString().substring(0, 10));
        transaccion.setCodigoUnicoTransaccion(UUID.randomUUID().toString());
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setEstado("PEN");
        
        log.info("Guardando transacción con código: {}", transaccion.getCodTransaccion());
        return this.transaccionRepository.save(transaccion);
    }
    
    @Transactional
    public Transaccion crearTransaccionConDTO(TransaccionDTO transaccionDTO) {
        log.info("Iniciando creación de transacción desde DTO");
        
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(transaccionDTO.getTipo());
        transaccion.setMarca(transaccionDTO.getMarca());
        transaccion.setMonto(transaccionDTO.getMonto());
        transaccion.setMoneda(transaccionDTO.getMoneda());
        transaccion.setPais(transaccionDTO.getPais());
        transaccion.setTarjeta(transaccionDTO.getTarjeta());
        transaccion.setFechaCaducidad(transaccionDTO.getFechaCaducidad());
        transaccion.setSwiftBanco(transaccionDTO.getSwiftBanco());
        transaccion.setCuentaIban(transaccionDTO.getCuentaIban());
        transaccion.setDiferido(transaccionDTO.getDiferido());
        
        Transaccion transaccionGuardada = this.crearTransaccion(transaccion);
        
        // Si la transacción es diferida (recurrente), enviarla al microservicio de transacciones recurrentes
        if (Boolean.TRUE.equals(transaccionDTO.getDiferido())) {
            log.info("Transacción diferida detectada, enviando a servicio de transacciones recurrentes");
            try {
                this.transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
            } catch (Exception e) {
                log.error("Error al enviar transacción recurrente, pero la transacción principal fue guardada", e);
            }
        }
        
        return transaccionGuardada;
    }

    @Transactional(readOnly = true)
    public Transaccion obtenerTransaccionPorId(String id) {
        log.info("Buscando transacción con ID: {}", id);
        return this.transaccionRepository.findById(id)
                .orElseThrow(() -> new TransaccionNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorEstado(String estado) {
        log.info("Buscando transacciones con estado: {}", estado);
        return this.transaccionRepository.findByEstado(estado);
    }

    @Transactional
    public Transaccion actualizarEstadoTransaccion(String id, String nuevoEstado) {
        log.info("Actualizando estado de transacción {} a {}", id, nuevoEstado);
        
        Transaccion transaccion = this.obtenerTransaccionPorId(id);
        
        if (!List.of("ACT", "INA", "PEN").contains(nuevoEstado)) {
            throw new TransaccionInvalidaException("Estado inválido: " + nuevoEstado);
        }
        
        transaccion.setEstado(nuevoEstado);
        return this.transaccionRepository.save(transaccion);
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Buscando transacciones entre {} y {}", fechaInicio, fechaFin);
        return this.transaccionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto().doubleValue() <= 0) {
            throw new TransaccionInvalidaException("El monto debe ser mayor a 0");
        }
        
        if (transaccion.getFechaCaducidad().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new TransaccionInvalidaException("La tarjeta está caducada");
        }
        
        if (!List.of("USD", "EUR").contains(transaccion.getMoneda())) {
            throw new TransaccionInvalidaException("Moneda no soportada: " + transaccion.getMoneda());
        }
    }
} 