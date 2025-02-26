package com.banquito.gateway.transacciones.banquito.exception;

public class TransaccionRecurrenteException extends RuntimeException {

    public TransaccionRecurrenteException(String mensaje) {
        super(mensaje);
    }

    public TransaccionRecurrenteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 