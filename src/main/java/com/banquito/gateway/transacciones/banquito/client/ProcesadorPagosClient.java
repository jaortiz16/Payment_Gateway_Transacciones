package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transacciones.banquito.client.dto.ProcesadorPagosDTO;
//cometario de prueba para devops
@FeignClient(name = "procesadorPagos", url = "http://procesatransaccion-alb-785318717.us-east-2.elb.amazonaws.com")
public interface ProcesadorPagosClient {
    
    @PostMapping("/api/v1/transacciones")
    ResponseEntity<Object> procesarPago(@RequestBody ProcesadorPagosDTO transaccion);
} 