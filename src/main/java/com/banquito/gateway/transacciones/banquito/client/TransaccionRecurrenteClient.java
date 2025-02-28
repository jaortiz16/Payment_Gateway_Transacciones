package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;

@FeignClient(name = "transaccionesRecurrentes", url = "${feign.client.transaccionesrecurrentes.url}")
public interface TransaccionRecurrenteClient {
    
    @PostMapping("/v1/transaccionesrecurrentes")
    ResponseEntity<Object> crearTransaccionRecurrente(@RequestBody TransaccionRecurrenteDTO transaccionRecurrente);
} 