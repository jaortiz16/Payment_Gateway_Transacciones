package com.banquito.payment_gateway.transacciones.banquito.exception;

public class TransaccionInvalidaException extends RuntimeException {
    
    private final String mensaje;

    public TransaccionInvalidaException(String mensaje) {
        super();
        this.mensaje = mensaje;
    }

    @Override
    public String getMessage() {
        return "Error en la transacción: " + this.mensaje;
    }
} 