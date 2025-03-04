package com.banquito.gateway.transacciones.banquito.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.gateway.transacciones.banquito.client.ComercioClient;
import com.banquito.gateway.transacciones.banquito.client.ProcesadorPagosClient;
import com.banquito.gateway.transacciones.banquito.client.dto.ComercioDTO;
import com.banquito.gateway.transacciones.banquito.client.dto.ProcesadorPagosDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionPosDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionRecurrenteInboundDTO;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.repository.TransaccionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionRecurrenteService transaccionRecurrenteService;
    private final ProcesadorPagosClient procesadorPagosClient;
    private final ComercioClient comercioClient;

    public TransaccionService(TransaccionRepository transaccionRepository, 
                             TransaccionRecurrenteService transaccionRecurrenteService,
                             ProcesadorPagosClient procesadorPagosClient,
                             ComercioClient comercioClient) {
        this.transaccionRepository = transaccionRepository;
        this.transaccionRecurrenteService = transaccionRecurrenteService;
        this.procesadorPagosClient = procesadorPagosClient;
        this.comercioClient = comercioClient;
    }

    @Transactional
    public Transaccion crearTransaccion(Transaccion transaccion) {
        log.info("Iniciando creación de transacción");
        
        validarTransaccion(transaccion);
        
        transaccion.setCodTransaccion(UUID.randomUUID().toString().substring(0, 10));
        if (transaccion.getCodigoUnicoTransaccion() == null) {
            transaccion.setCodigoUnicoTransaccion(UUID.randomUUID().toString());
        }
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
        
        if (Boolean.TRUE.equals(transaccionDTO.getEsDiferido())) {
            log.info("Transacción marcada como recurrente, se enviará al servicio de transacciones recurrentes");
            try {
                validarCamposTransaccionRecurrente(transaccionDTO);
                
                transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
                log.info("Transacción recurrente enviada exitosamente al servicio correspondiente");
            } catch (Exception e) {
                log.error("Error al enviar transacción recurrente: {}", e.getMessage());
            }
        }
        
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

    private void validarCamposTransaccionRecurrente(TransaccionDTO transaccionDTO) {
        if (transaccionDTO.getDiaMesPago() == null) {
            throw new TransaccionInvalidaException("El día del mes para el pago es requerido para transacciones recurrentes");
        }
        
        if (transaccionDTO.getFechaInicio() == null) {
            throw new TransaccionInvalidaException("La fecha de inicio es requerida para transacciones recurrentes");
        }
        
        if (transaccionDTO.getFechaFin() == null) {
            throw new TransaccionInvalidaException("La fecha de fin es requerida para transacciones recurrentes");
        }
        
        if (transaccionDTO.getDiaMesPago() < 1 || transaccionDTO.getDiaMesPago() > 31) {
            throw new TransaccionInvalidaException("El día del mes para el pago debe estar entre 1 y 31");
        }
        
        if (transaccionDTO.getFechaInicio().isAfter(transaccionDTO.getFechaFin())) {
            throw new TransaccionInvalidaException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    @Transactional
    public Transaccion procesarTransaccionPOS(TransaccionPosDTO posDTO) {
        log.info("Procesando transacción desde POS {}, comercio {}", posDTO.getCodigoPOS(), posDTO.getCodigoComercio());
        
        // Obtener datos bancarios del comercio
        ComercioDTO comercioDTO;
        try {
            log.info("Consultando datos bancarios del comercio con POS: {}", posDTO.getCodigoPOS());
            comercioDTO = comercioClient.obtenerDatosComercio(posDTO.getCodigoPOS());
            log.info("Datos bancarios obtenidos - Swift: {}, IBAN: {}", 
                    comercioDTO.getSwift_banco(), comercioDTO.getCuenta_iban());
        } catch (Exception e) {
            log.error("Error al consultar datos bancarios: {}", e.getMessage());
            throw new TransaccionInvalidaException("No se pudieron obtener los datos bancarios del comercio");
        }
        
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(posDTO.getTipo());
        transaccion.setMarca(posDTO.getMarca());
        transaccion.setMonto(posDTO.getMonto());
        transaccion.setMoneda(posDTO.getMoneda());
        transaccion.setPais(posDTO.getPais());
        transaccion.setTarjeta(posDTO.getNumeroTarjeta());
        transaccion.setCodigoUnicoTransaccion(posDTO.getCodigoUnicoTransaccion());
        transaccion.setSwiftBanco(comercioDTO.getSwift_banco());
        transaccion.setCuentaIban(comercioDTO.getCuenta_iban());
        
        String fechaExp = posDTO.getFechaExpiracion();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(fechaExp, formatter);
        transaccion.setFechaCaducidad(yearMonth.atEndOfMonth());
        
        String datosAdicionales = String.format(
            "POS: %s, Comercio: %s, Titular: %s, CVV: %s", 
            posDTO.getCodigoPOS(),
            posDTO.getCodigoComercio(),
            posDTO.getNombreTitular(),
            posDTO.getCodigoSeguridad()
        );
        transaccion.setTransaccionEncriptada(datosAdicionales);
        
        boolean esDiferida = "DIF".equals(posDTO.getModalidad()) && posDTO.getPlazo() != null && posDTO.getPlazo() > 1;
        transaccion.setDiferido(esDiferida);
        
        Transaccion transaccionGuardada = this.crearTransaccion(transaccion);
        log.info("Transacción guardada con ID: {} en estado pendiente", transaccionGuardada.getCodTransaccion());
        
        try {
            log.info("Enviando transacción al procesador de pagos y esperando respuesta: {}", 
                    transaccionGuardada.getCodigoUnicoTransaccion());
            
            // Crear DTO específico para el procesador de pagos
            ProcesadorPagosDTO procesadorDTO = new ProcesadorPagosDTO(posDTO, comercioDTO);
            ResponseEntity<Object> respuesta = procesadorPagosClient.procesarPago(procesadorDTO);
            
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                transaccionGuardada.setEstado("ACT");
                transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
                log.info("Procesador aceptó la transacción: {}. Estado actualizado a: ACT", 
                        transaccionGuardada.getCodigoUnicoTransaccion());
            } else {
                transaccionGuardada.setEstado("REJ");
                transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
                log.error("Procesador rechazó la transacción: {}. Estado actualizado a: REJ", 
                        transaccionGuardada.getCodigoUnicoTransaccion());
                throw new TransaccionInvalidaException("Transacción rechazada por el procesador de pagos");
            }
        } catch (Exception e) {
            if (e instanceof TransaccionInvalidaException) {
                throw e;
            }
            
            log.error("Error al procesar la transacción con el procesador: {}", e.getMessage());
            transaccionGuardada.setEstado("ERR");
            transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
            log.error("Transacción marcada con estado de error");
            throw new TransaccionInvalidaException("Error al procesar la transacción: " + e.getMessage());
        }
        
        boolean esRecurrente = "REC".equals(posDTO.getModalidad()) || Boolean.TRUE.equals(posDTO.getRecurrente());
        if (esRecurrente && posDTO.getFrecuenciaDias() != null) {
            try {
                log.info("Enviando transacción recurrente al servicio correspondiente");
                enviarTransaccionRecurrente(posDTO, comercioDTO);
                log.info("Transacción recurrente enviada exitosamente");
            } catch (Exception e) {
                log.error("Error al enviar transacción recurrente: {}", e.getMessage());
            }
        }
        
        return transaccionGuardada;
    }
    
    @Transactional
    public void actualizarEstadoTransaccion(String codTransaccion, String nuevoEstado) {
        log.info("Actualizando estado de transacción {} a {}", codTransaccion, nuevoEstado);
        Transaccion transaccion = this.transaccionRepository.findById(codTransaccion)
                .orElseThrow(() -> new TransaccionNotFoundException(codTransaccion));
        
        transaccion.setEstado(nuevoEstado);
        this.transaccionRepository.save(transaccion);
        log.info("Estado de transacción actualizado exitosamente");
    }
    
    private void enviarTransaccionRecurrente(TransaccionPosDTO posDTO, ComercioDTO comercioDTO) {
        log.info("Preparando envío de transacción recurrente para la tarjeta: {}", posDTO.getNumeroTarjeta());
        
        String fechaExp = posDTO.getFechaExpiracion();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(fechaExp, formatter);
        LocalDate fechaCaducidad = yearMonth.atEndOfMonth();
        
        // Crear un DTO específico para transacciones recurrentes
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        transaccionDTO.setTipo(posDTO.getTipo());
        transaccionDTO.setMarca(posDTO.getMarca());
        transaccionDTO.setMonto(posDTO.getMonto());
        transaccionDTO.setMoneda(posDTO.getMoneda());
        transaccionDTO.setPais(posDTO.getPais());
        transaccionDTO.setTarjeta(posDTO.getNumeroTarjeta());
        transaccionDTO.setFechaCaducidad(fechaCaducidad);
        transaccionDTO.setCodigoUnicoTransaccion(posDTO.getCodigoUnicoTransaccion());
        transaccionDTO.setSwiftBanco(comercioDTO.getSwift_banco());
        transaccionDTO.setCuentaIban(comercioDTO.getCuenta_iban());
        transaccionDTO.setCodigoSeguridad(posDTO.getCodigoSeguridad());
        
        // Configurar campos específicos para transacciones recurrentes
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin;
        if (posDTO.getFrecuenciaDias() != null && posDTO.getFrecuenciaDias() > 0) {
            fechaFin = fechaInicio.plusDays(posDTO.getFrecuenciaDias() * 12);
            transaccionDTO.setFrecuenciaDias(posDTO.getFrecuenciaDias());
        } else {
            fechaFin = fechaInicio.plusMonths(12);
            transaccionDTO.setFrecuenciaDias(30); // Valor por defecto: mensual
        }
        
        int diaPago = fechaInicio.getDayOfMonth();
        
        transaccionDTO.setFechaInicio(fechaInicio);
        transaccionDTO.setFechaFin(fechaFin);
        transaccionDTO.setDiaMesPago(diaPago);
        
        // Enviar al servicio de transacciones recurrentes
        try {
            transaccionRecurrenteService.enviarTransaccionRecurrente(transaccionDTO);
            log.info("Transacción recurrente enviada exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar transacción recurrente: {}", e.getMessage());
        }
    }

    @Transactional
    public Transaccion procesarTransaccionRecurrenteInbound(TransaccionRecurrenteInboundDTO recurrenteDTO) {
        log.info("Procesando transacción recurrente entrante desde el microservicio recurrente");
        
        // Generar un código único para esta transacción
        String codigoUnico = UUID.randomUUID().toString();
        
        // Crear la transacción con los datos recibidos
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo("PAG"); // Por defecto es un pago
        transaccion.setMarca(recurrenteDTO.getMarca());
        transaccion.setMonto(recurrenteDTO.getMonto());
        transaccion.setMoneda(recurrenteDTO.getMoneda());
        transaccion.setPais(recurrenteDTO.getPais());
        transaccion.setTarjeta(recurrenteDTO.getNumeroTarjeta());
        transaccion.setCodigoUnicoTransaccion(codigoUnico);
        transaccion.setSwiftBanco(recurrenteDTO.getSwift_banco());
        transaccion.setCuentaIban(recurrenteDTO.getCuenta_iban());
        
        // Convertir la fecha de expiración
        String fechaExp = recurrenteDTO.getFechaExpiracion();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(fechaExp, formatter);
        transaccion.setFechaCaducidad(yearMonth.atEndOfMonth());
        
        // Guardar información adicional encriptada
        String datosAdicionales = String.format(
            "Transacción recurrente, CVV: %s, Frecuencia: %d días", 
            recurrenteDTO.getCvv(),
            recurrenteDTO.getFrecuenciaDias()
        );
        transaccion.setTransaccionEncriptada(datosAdicionales);
        
        // La transacción es recurrente por definición
        transaccion.setDiferido(false);
        
        // Guardar la transacción en estado pendiente
        Transaccion transaccionGuardada = this.crearTransaccion(transaccion);
        log.info("Transacción recurrente guardada con ID: {} en estado pendiente", 
                transaccionGuardada.getCodTransaccion());
        
        // Crear el DTO para enviar al procesador de pagos
        ProcesadorPagosDTO procesadorDTO = new ProcesadorPagosDTO();
        procesadorDTO.setTipo("PAG");
        procesadorDTO.setMarca(recurrenteDTO.getMarca());
        procesadorDTO.setModalidad("REC");
        procesadorDTO.setMonto(recurrenteDTO.getMonto());
        procesadorDTO.setMoneda(recurrenteDTO.getMoneda());
        procesadorDTO.setSwift_banco(recurrenteDTO.getSwift_banco());
        procesadorDTO.setCuenta_iban(recurrenteDTO.getCuenta_iban());
        procesadorDTO.setNumeroTarjeta(recurrenteDTO.getNumeroTarjeta());
        procesadorDTO.setNombreTitular("Titular Recurrente"); // Valor por defecto
        procesadorDTO.setCodigoSeguridad(recurrenteDTO.getCvv());
        procesadorDTO.setFechaExpiracion(recurrenteDTO.getFechaExpiracion());
        procesadorDTO.setCodigoUnicoTransaccion(codigoUnico);
        
        try {
            log.info("Enviando transacción recurrente al procesador de pagos y esperando respuesta: {}", 
                    transaccionGuardada.getCodigoUnicoTransaccion());
            
            ResponseEntity<Object> respuesta = procesadorPagosClient.procesarPago(procesadorDTO);
            
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                transaccionGuardada.setEstado("ACT");
                transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
                log.info("Procesador aceptó la transacción recurrente: {}. Estado actualizado a: ACT", 
                        transaccionGuardada.getCodigoUnicoTransaccion());
            } else {
                transaccionGuardada.setEstado("REJ");
                transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
                log.error("Procesador rechazó la transacción recurrente: {}. Estado actualizado a: REJ", 
                        transaccionGuardada.getCodigoUnicoTransaccion());
                throw new TransaccionInvalidaException("Transacción recurrente rechazada por el procesador de pagos");
            }
        } catch (Exception e) {
            if (e instanceof TransaccionInvalidaException) {
                throw e;
            }
            
            log.error("Error al procesar la transacción recurrente con el procesador: {}", e.getMessage());
            transaccionGuardada.setEstado("ERR");
            transaccionGuardada = this.transaccionRepository.save(transaccionGuardada);
            log.error("Transacción recurrente marcada con estado de error");
            throw new TransaccionInvalidaException("Error al procesar la transacción recurrente: " + e.getMessage());
        }
        
        return transaccionGuardada;
    }
} 