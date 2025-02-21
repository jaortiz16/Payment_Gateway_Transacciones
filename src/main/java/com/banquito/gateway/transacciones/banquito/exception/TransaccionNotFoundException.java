package com.banquito.payment_gateway.transacciones.banquito.exception;

public class TransaccionNotFoundException extends RuntimeException {
    
    private final String identificador;

    public TransaccionNotFoundException(String identificador) {
        super();
        this.identificador = identificador;
    }

    @Override
    public String getMessage() {
        return "No se encontró la transacción con identificador: " + this.identificador;
    }
} 