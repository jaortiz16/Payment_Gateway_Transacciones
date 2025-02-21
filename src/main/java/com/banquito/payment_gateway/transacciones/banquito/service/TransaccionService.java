package com.banquito.payment_gateway.transacciones.banquito.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.payment_gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.payment_gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.payment_gateway.transacciones.banquito.model.Transaccion;
import com.banquito.payment_gateway.transacciones.banquito.repository.TransaccionRepository;

import lombok.extern.slf4j.Slf4j;
//test
@Service
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
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