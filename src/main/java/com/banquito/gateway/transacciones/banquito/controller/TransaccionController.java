package com.banquito.gateway.transacciones.banquito.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.gateway.transacciones.banquito.controller.dto.PageResponseDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionPosDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionRecurrenteInboundDTO;
import com.banquito.gateway.transacciones.banquito.controller.mapper.TransaccionMapper;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionInvalidaException;
import com.banquito.gateway.transacciones.banquito.exception.TransaccionNotFoundException;
import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.service.TransaccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Crear una nueva transacción", description = "Crea una nueva transacción en el sistema de pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos")
    })
    public ResponseEntity<TransaccionDTO> crearTransaccion(@Valid @RequestBody TransaccionPosDTO transaccionPosDTO) {
        log.info("Recibiendo petición para crear transacción desde POS: {}", transaccionPosDTO.getCodigoPOS());
        Transaccion transaccion = this.transaccionService.procesarTransaccionPOS(transaccionPosDTO);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una transacción por su ID", description = "Retorna una transacción basada en su ID único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(
            @Parameter(description = "ID de la transacción", required = true)
            @PathVariable("id") String id) {
        log.info("Buscando transacción con ID: {}", id);
        Transaccion transaccion = this.transaccionService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }
    
    @GetMapping("/codigo-unico/{codigoUnico}")
    @Operation(summary = "Obtener una transacción por su código único", description = "Retorna una transacción basada en su código único de transacción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<TransaccionDTO> obtenerTransaccionPorCodigoUnico(
            @Parameter(description = "Código único de la transacción", required = true)
            @PathVariable("codigoUnico") String codigoUnico) {
        log.info("Buscando transacción con código único: {}", codigoUnico);
        Transaccion transaccion = this.transaccionService.obtenerTransaccionPorCodigoUnico(codigoUnico);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }

    @GetMapping
    @Operation(summary = "Buscar transacciones con filtros", description = "Permite buscar transacciones aplicando diferentes filtros y paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    public ResponseEntity<?> buscarTransacciones(
            @Parameter(description = "Estado de la transacción (ACT, INA, PEN, REC)")
            @RequestParam(name = "estado", required = false) String estado,
            
            @Parameter(description = "Fecha inicio para filtrar (ISO DateTime)")
            @RequestParam(name = "fechaInicio", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            
            @Parameter(description = "Fecha fin para filtrar (ISO DateTime)")
            @RequestParam(name = "fechaFin", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            
            @Parameter(description = "Marca de la tarjeta (VISA, MAST, AMEX, etc)")
            @RequestParam(name = "marca", required = false) String marca,
            
            @Parameter(description = "Tipo de transacción (PAG, RET, TRA, DEV)")
            @RequestParam(name = "tipo", required = false) String tipo,
            
            @Parameter(description = "Número de página (comenzando en 0)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página")
            @RequestParam(name = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenar (codTransaccion, fecha, monto, etc)")
            @RequestParam(name = "sort", defaultValue = "fecha") String sort,
            
            @Parameter(description = "Dirección del ordenamiento (ASC, DESC)")
            @RequestParam(name = "direction", defaultValue = "DESC") String direction) {
        
        log.info("Buscando transacciones con filtros");
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        if (estado != null && tipo != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorTipoYEstado(tipo, estado, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        } else if (marca != null && fechaInicio != null && fechaFin != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorMarcaYFecha(marca, fechaInicio, fechaFin, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        } else if (estado != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorEstado(estado, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        } else if (fechaInicio != null && fechaFin != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorFecha(fechaInicio, fechaFin, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        } else if (marca != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorMarca(marca, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        }else if (tipo != null) {
            Page<Transaccion> transaccionesPage = this.transaccionService.obtenerTransaccionesPorTipo(tipo, pageable);
            return ResponseEntity.ok(convertToPageResponseDTO(transaccionesPage));
        } else {
            return ResponseEntity.badRequest().body("Se requiere al menos un parámetro de filtrado");
        }
    }
    
    @GetMapping("/tarjeta/{numeroTarjeta}")
    @Operation(summary = "Buscar transacciones por número de tarjeta", description = "Retorna todas las transacciones asociadas a un número de tarjeta específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorTarjeta(
            @Parameter(description = "Número de tarjeta (16 dígitos)", required = true)
            @PathVariable("numeroTarjeta") String numeroTarjeta) {
        
        log.info("Buscando transacciones para la tarjeta: {}", numeroTarjeta);
        List<Transaccion> transacciones = this.transaccionService.obtenerTransaccionesPorTarjeta(numeroTarjeta);
        
        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @GetMapping("/swift-banco/{swiftBanco}")
    @Operation(summary = "Buscar transacciones por código SWIFT de banco", description = "Retorna todas las transacciones asociadas a un banco específico por su código SWIFT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorSwiftBanco(
            @Parameter(description = "Código SWIFT del banco", required = true)
            @PathVariable("swiftBanco") String swiftBanco) {
        
        log.info("Buscando transacciones para el banco con SWIFT: {}", swiftBanco);
        List<Transaccion> transacciones = this.transaccionService.obtenerTransaccionesPorSwiftBanco(swiftBanco);
        
        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @GetMapping("/cuenta-iban/{cuentaIban}")
    @Operation(summary = "Buscar transacciones por cuenta IBAN", description = "Retorna todas las transacciones asociadas a una cuenta bancaria específica por su IBAN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorCuentaIban(
            @Parameter(description = "Número de cuenta IBAN", required = true)
            @PathVariable("cuentaIban") String cuentaIban) {
        
        log.info("Buscando transacciones para la cuenta IBAN: {}", cuentaIban);
        List<Transaccion> transacciones = this.transaccionService.obtenerTransaccionesPorCuentaIban(cuentaIban);
        
        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @GetMapping("/moneda/{moneda}")
    @Operation(summary = "Buscar transacciones por moneda", description = "Retorna todas las transacciones realizadas en una moneda específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorMoneda(
            @Parameter(description = "Código de moneda (USD, EUR, etc)", required = true)
            @PathVariable("moneda") String moneda) {
        
        log.info("Buscando transacciones en moneda: {}", moneda);
        List<Transaccion> transacciones = this.transaccionService.obtenerTransaccionesPorMoneda(moneda);
        
        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @GetMapping("/pais/{pais}")
    @Operation(summary = "Buscar transacciones por país", description = "Retorna todas las transacciones realizadas en un país específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorPais(
            @Parameter(description = "Código de país ISO (2 caracteres)", required = true)
            @PathVariable("pais") String pais) {
        
        log.info("Buscando transacciones del país: {}", pais);
        List<Transaccion> transacciones = this.transaccionService.obtenerTransaccionesPorPais(pais);
        
        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @GetMapping("/monto")
    @Operation(summary = "Buscar transacciones por rango de monto", description = "Retorna transacciones filtradas por un rango de monto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    public ResponseEntity<List<TransaccionDTO>> buscarTransaccionesPorMonto(
            @Parameter(description = "Monto mínimo")
            @RequestParam(name = "minimo", required = false) BigDecimal montoMinimo,
            
            @Parameter(description = "Monto máximo")
            @RequestParam(name = "maximo", required = false) BigDecimal montoMaximo) {
        
        List<Transaccion> transacciones;
        
        if (montoMinimo != null && montoMaximo != null) {
            return ResponseEntity.badRequest().body(null);
        } else if (montoMinimo != null) {
            log.info("Buscando transacciones con monto mínimo: {}", montoMinimo);
            transacciones = this.transaccionService.obtenerTransaccionesPorMontoMinimo(montoMinimo);
        } else if (montoMaximo != null) {
            log.info("Buscando transacciones con monto máximo: {}", montoMaximo);
            transacciones = this.transaccionService.obtenerTransaccionesPorMontoMaximo(montoMaximo);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        List<TransaccionDTO> transaccionesDTO = new ArrayList<>(transacciones.size());
        for (Transaccion t : transacciones) {
            transaccionesDTO.add(this.mapper.toDTO(t));
        }
        
        return ResponseEntity.ok(transaccionesDTO);
    }
    
    @PostMapping("/recurrentes")
    @Operation(summary = "Procesar una transacción recurrente", description = "Procesa una transacción recurrente proveniente del microservicio de transacciones recurrentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción recurrente procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de transacción recurrente inválidos")
    })
    public ResponseEntity<TransaccionDTO> procesarTransaccionRecurrente(
            @Valid @RequestBody TransaccionRecurrenteInboundDTO transaccionRecurrenteDTO) {
        log.info("Recibiendo petición para procesar transacción recurrente para tarjeta: {}", 
                transaccionRecurrenteDTO.getNumeroTarjeta());
        Transaccion transaccion = this.transaccionService.procesarTransaccionRecurrenteInbound(transaccionRecurrenteDTO);
        return ResponseEntity.ok(this.mapper.toDTO(transaccion));
    }
    
    @ExceptionHandler({TransaccionNotFoundException.class})
    public ResponseEntity<String> handleTransaccionNotFound(TransaccionNotFoundException e) {
        log.error("Transacción no encontrada: {}", e.getMessage());
        return ResponseEntity.status(404).body(e.getMessage());
    }
    
    @ExceptionHandler({TransaccionInvalidaException.class})
    public ResponseEntity<String> handleTransaccionInvalida(TransaccionInvalidaException e) {
        log.error("Transacción inválida: {}", e.getMessage());
        return ResponseEntity.status(400).body(e.getMessage());
    }
    
    private PageResponseDTO<TransaccionDTO> convertToPageResponseDTO(Page<Transaccion> page) {
        List<TransaccionDTO> dtos = new ArrayList<>(page.getSize());
        for (Transaccion transaccion : page.getContent()) {
            dtos.add(mapper.toDTO(transaccion));
        }
        
        return new PageResponseDTO<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
} 