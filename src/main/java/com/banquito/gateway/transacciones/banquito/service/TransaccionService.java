package com.banquito.gateway.transacciones.banquito.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.repository.TransaccionRepository;

import lombok.extern.slf4j.Slf4j;

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
        transaccion.setTransaccionEncriptada(transaccionDTO.getTransaccionEncriptada());
        
        return this.crearTransaccion(transaccion);
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
    
    @Transactional(readOnly = true)
    public Page<Transaccion> obtenerTransaccionesPorEstado(String estado, Pageable pageable) {
        log.info("Buscando transacciones con estado: {} (paginado)", estado);
        return this.transaccionRepository.findByEstado(estado, pageable);
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Buscando transacciones entre {} y {}", fechaInicio, fechaFin);
        return this.transaccionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
    
    @Transactional(readOnly = true)
    public Page<Transaccion> obtenerTransaccionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        log.info("Buscando transacciones entre {} y {} (paginado)", fechaInicio, fechaFin);
        return this.transaccionRepository.findByFechaBetween(fechaInicio, fechaFin, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorMarca(String marca) {
        log.info("Buscando transacciones para la marca: {}", marca);
        return this.transaccionRepository.findByMarca(marca);
    }
    
    @Transactional(readOnly = true)
    public Page<Transaccion> obtenerTransaccionesPorMarca(String marca, Pageable pageable) {
        log.info("Buscando transacciones para la marca: {} (paginado)", marca);
        return this.transaccionRepository.findByMarca(marca, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorTarjeta(String tarjeta) {
        log.info("Buscando transacciones para la tarjeta: {}", tarjeta);
        return this.transaccionRepository.findByTarjeta(tarjeta);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorMoneda(String moneda) {
        log.info("Buscando transacciones en moneda: {}", moneda);
        return this.transaccionRepository.findByMoneda(moneda);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorPais(String pais) {
        log.info("Buscando transacciones del país: {}", pais);
        return this.transaccionRepository.findByPais(pais);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorMontoMinimo(BigDecimal monto) {
        log.info("Buscando transacciones con monto mínimo: {}", monto);
        return this.transaccionRepository.findByMontoGreaterThanEqual(monto);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorMontoMaximo(BigDecimal monto) {
        log.info("Buscando transacciones con monto máximo: {}", monto);
        return this.transaccionRepository.findByMontoLessThanEqual(monto);
    }
    
    @Transactional(readOnly = true)
    public Transaccion obtenerTransaccionPorCodigoUnico(String codigoUnico) {
        log.info("Buscando transacción con código único: {}", codigoUnico);
        Transaccion transaccion = this.transaccionRepository.findByCodigoUnicoTransaccion(codigoUnico);
        if (transaccion == null) {
            throw new TransaccionNotFoundException(codigoUnico);
        }
        return transaccion;
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorTipo(String tipo) {
        log.info("Buscando transacciones de tipo: {}", tipo);
        return this.transaccionRepository.findByTipo(tipo);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorSwiftBanco(String swiftBanco) {
        log.info("Buscando transacciones para el banco con SWIFT: {}", swiftBanco);
        return this.transaccionRepository.findBySwiftBanco(swiftBanco);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorCuentaIban(String cuentaIban) {
        log.info("Buscando transacciones para la cuenta IBAN: {}", cuentaIban);
        return this.transaccionRepository.findByCuentaIban(cuentaIban);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorTipoYEstado(String tipo, String estado) {
        log.info("Buscando transacciones de tipo: {} con estado: {}", tipo, estado);
        return this.transaccionRepository.findByTipoAndEstado(tipo, estado);
    }
    
    @Transactional(readOnly = true)
    public Page<Transaccion> obtenerTransaccionesPorTipoYEstado(String tipo, String estado, Pageable pageable) {
        log.info("Buscando transacciones de tipo: {} con estado: {} (paginado)", tipo, estado);
        return this.transaccionRepository.findByTipoAndEstado(tipo, estado, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorMarcaYFecha(String marca, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Buscando transacciones para la marca: {} entre {} y {}", marca, fechaInicio, fechaFin);
        return this.transaccionRepository.findByMarcaAndFechaBetween(marca, fechaInicio, fechaFin);
    }
    
    @Transactional(readOnly = true)
    public Page<Transaccion> obtenerTransaccionesPorMarcaYFecha(String marca, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        log.info("Buscando transacciones para la marca: {} entre {} y {} (paginado)", marca, fechaInicio, fechaFin);
        return this.transaccionRepository.findByMarcaAndFechaBetween(marca, fechaInicio, fechaFin, pageable);
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().doubleValue() <= 0) {
            throw new TransaccionInvalidaException("El monto debe ser mayor a 0");
        }
        
        if (transaccion.getFechaCaducidad() == null || transaccion.getFechaCaducidad().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new TransaccionInvalidaException("La tarjeta está caducada");
        }
        
        if (transaccion.getMoneda() == null || !List.of("USD", "EUR").contains(transaccion.getMoneda())) {
            throw new TransaccionInvalidaException("Moneda no soportada: " + transaccion.getMoneda());
        }
        
        if (transaccion.getTarjeta() != null && (transaccion.getTarjeta().length() < 15 || transaccion.getTarjeta().length() > 19)) {
            throw new TransaccionInvalidaException("Número de tarjeta inválido, debe tener entre 15 y 19 dígitos dependiendo de la marca");
        }
        
        if (transaccion.getTipo() == null || !List.of("PAG", "RET", "TRA", "DEV").contains(transaccion.getTipo())) {
            throw new TransaccionInvalidaException("Tipo de transacción no soportado: " + transaccion.getTipo());
        }
        
        if ("TRA".equals(transaccion.getTipo()) && 
            (transaccion.getSwiftBanco() == null || transaccion.getCuentaIban() == null)) {
            throw new TransaccionInvalidaException("Para transferencias (TRA) se requiere SWIFT del banco y cuenta IBAN");
        }
        
        if ("PAG".equals(transaccion.getTipo()) && transaccion.getTarjeta() == null) {
            throw new TransaccionInvalidaException("Para pagos (PAG) se requiere número de tarjeta");
        }
        
        if (transaccion.getPais() == null || transaccion.getPais().length() != 2) {
            throw new TransaccionInvalidaException("El código de país debe tener 2 caracteres (formato ISO)");
        }
    }

    @Transactional
    public Transaccion crearTransaccionRespuesta(String codigoUnicoOriginal, String estado, 
                                               String codigoRespuesta, String mensajeRespuesta) {
        log.info("Creando transacción de respuesta para la transacción con código único: {}", codigoUnicoOriginal);
        
        Transaccion transaccionOriginal = this.obtenerTransaccionPorCodigoUnico(codigoUnicoOriginal);
        
        if (!"PEN".equals(transaccionOriginal.getEstado())) {
            throw new TransaccionInvalidaException("La transacción original debe estar en estado pendiente para crear una respuesta");
        }

        Transaccion nuevaTransaccion = new Transaccion();
        nuevaTransaccion.setCodTransaccion(UUID.randomUUID().toString().substring(0, 10));
        nuevaTransaccion.setCodigoUnicoTransaccion(UUID.randomUUID().toString());
        nuevaTransaccion.setTipo(transaccionOriginal.getTipo());
        nuevaTransaccion.setMarca(transaccionOriginal.getMarca());
        nuevaTransaccion.setMonto(transaccionOriginal.getMonto());
        nuevaTransaccion.setFecha(LocalDateTime.now());
        nuevaTransaccion.setEstado(estado);
        nuevaTransaccion.setMoneda(transaccionOriginal.getMoneda());
        nuevaTransaccion.setPais(transaccionOriginal.getPais());
        nuevaTransaccion.setTarjeta(transaccionOriginal.getTarjeta());
        nuevaTransaccion.setFechaCaducidad(transaccionOriginal.getFechaCaducidad());
        nuevaTransaccion.setSwiftBanco(transaccionOriginal.getSwiftBanco());
        nuevaTransaccion.setCuentaIban(transaccionOriginal.getCuentaIban());
        
        String respuestaInfo = "Respuesta a transacción: " + codigoUnicoOriginal + 
                              ", Código: " + codigoRespuesta + 
                              (mensajeRespuesta != null ? ", Mensaje: " + mensajeRespuesta : "");
        nuevaTransaccion.setTransaccionEncriptada(respuestaInfo);
        
        log.info("Guardando transacción de respuesta con código: {} y estado: {}", 
                nuevaTransaccion.getCodTransaccion(), estado);
        
        return this.transaccionRepository.save(nuevaTransaccion);
    }
} 