package com.banquito.gateway.transacciones.banquito.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.gateway.transacciones.banquito.client.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transacciones.banquito.controller.dto.TransaccionDTO;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransaccionRecurrenteMapper {
    
    @Mapping(target = "tarjeta", expression = "java(Long.parseLong(transaccionDTO.getTarjeta()))")
    TransaccionRecurrenteDTO toTransaccionRecurrenteDTO(TransaccionDTO transaccionDTO);
} 