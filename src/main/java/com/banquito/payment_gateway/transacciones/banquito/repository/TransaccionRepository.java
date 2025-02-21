package com.banquito.payment_gateway.transacciones.banquito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.payment_gateway.transacciones.banquito.model.Transaccion;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, String> {
    
    List<Transaccion> findByEstado(String estado);
    List<Transaccion> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Transaccion> findByMarca(String marca);
    List<Transaccion> findByTarjeta(String tarjeta);
    Transaccion findByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
} 