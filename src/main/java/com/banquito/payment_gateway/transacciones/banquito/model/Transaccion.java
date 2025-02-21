package com.banquito.payment_gateway.transacciones.banquito.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GTW_TRANSACCION")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transaccion {

    @Id
    @Column(name = "COD_TRANSACCION", length = 10)
    private String codTransaccion;

    @Column(name = "TIPO", length = 3, nullable = false)
    private String tipo;

    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;

    @Column(name = "MONTO", precision = 20, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64, nullable = false)
    private String codigoUnicoTransaccion;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Column(name = "MONEDA", length = 3, nullable = false)
    private String moneda;

    @Column(name = "PAIS", length = 2, nullable = false)
    private String pais;

    @Column(name = "TARJETA", length = 16, nullable = false)
    private String tarjeta;

    @Column(name = "FECHA_CADUCIDAD", nullable = false)
    private LocalDate fechaCaducidad;

    @Column(name = "TRANSACCION_ENCRIPTADA", length = 1000)
    private String transaccionEncriptada;

    @Column(name = "SWIFT_BANCO", length = 11)
    private String swiftBanco;

    @Column(name = "CUENTA_IBAN", length = 28)
    private String cuentaIban;

    @Column(name = "DIFERIDO")
    private Boolean diferido;

    public Transaccion(String codTransaccion) {
        this.codTransaccion = codTransaccion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codTransaccion == null) ? 0 : codTransaccion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Transaccion other = (Transaccion) obj;
        if (codTransaccion == null) {
            if (other.codTransaccion != null)
                return false;
        } else if (!codTransaccion.equals(other.codTransaccion))
            return false;
        return true;
    }
} 