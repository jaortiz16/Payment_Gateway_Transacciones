package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transacciones.banquito.client.dto.ProcesadorPagosDTO;

@FeignClient(name = "procesadorPagos", url = "https://0271-2800-bf0-29c-1b03-9bd-d9d3-2ef9-70db.ngrok-free.app")
public interface ProcesadorPagosClient {
    
    @PostMapping("/api/v1/transacciones")
    ResponseEntity<Object> procesarPago(@RequestBody ProcesadorPagosDTO transaccion);
} 