package com.banquito.gateway.transacciones.banquito.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.gateway.transacciones.banquito.model.Transaccion;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, String> {
    
    List<Transaccion> findByEstado(String estado);
    
    Page<Transaccion> findByEstado(String estado, Pageable pageable);
    
    List<Transaccion> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Page<Transaccion> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    
    List<Transaccion> findByMarca(String marca);
    
    Page<Transaccion> findByMarca(String marca, Pageable pageable);
    
    List<Transaccion> findByTarjeta(String tarjeta);
    
    List<Transaccion> findByMoneda(String moneda);
    
    List<Transaccion> findByPais(String pais);
    
    List<Transaccion> findByMontoGreaterThanEqual(BigDecimal monto);
    
    List<Transaccion> findByMontoLessThanEqual(BigDecimal monto);
    
    Transaccion findByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
    
    List<Transaccion> findByTipo(String tipo);
    
    Page<Transaccion> findByTipo(String tipo, Pageable pageable);
    
    List<Transaccion> findBySwiftBanco(String swiftBanco);
    
    List<Transaccion> findByCuentaIban(String cuentaIban);
    
    List<Transaccion> findByTipoAndEstado(String tipo, String estado);
    
    Page<Transaccion> findByTipoAndEstado(String tipo, String estado, Pageable pageable);
    
    List<Transaccion> findByMarcaAndFechaBetween(String marca, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Page<Transaccion> findByMarcaAndFechaBetween(String marca, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
} 