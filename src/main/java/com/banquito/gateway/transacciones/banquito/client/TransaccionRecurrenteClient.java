package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;

@FeignClient(name = "transaccionesRecurrentes", url = "http://localhost:3001")
public interface TransaccionRecurrenteClient {
    
    @PostMapping("/v1/transacciones-recurrentes")
    ResponseEntity<Object> crearTransaccionRecurrente(@RequestBody TransaccionRecurrenteDTO transaccionRecurrente);
} 