package com.banquito.gateway.transacciones.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.banquito.gateway.transacciones.banquito.client.dto.ComercioDTO;

@FeignClient(name = "comercios", url = "http://gestioncomercios-alb-945169585.us-east-2.elb.amazonaws.com")
public interface ComercioClient {
    
    @GetMapping("/v1/comercios/pos/{codigoPOS}")
    ComercioDTO obtenerDatosComercio(@PathVariable("codigoPOS") String codigoPOS);
} 