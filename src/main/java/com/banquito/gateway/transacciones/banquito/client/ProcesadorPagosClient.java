package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transacciones.banquito.client.dto.ProcesadorPagosDTO;

@FeignClient(name = "procesadorPagos", url = "http://localhost:3000")
public interface ProcesadorPagosClient {
    
    @PostMapping("/procesar-pago")
    ResponseEntity<Object> procesarPago(@RequestBody ProcesadorPagosDTO transaccion);
} 