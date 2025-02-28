package com.banquito.payment_gateway.transacciones.banquito.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.banquito.gateway.transacciones.banquito.model.Transaccion;
import com.banquito.gateway.transacciones.banquito.repository.TransaccionRepository;

@DataJpaTest
@ActiveProfiles("test")
public class TransaccionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Test
    void testFindById() {
        // Crear y persistir una transacción
        Transaccion transaccion = crearTransaccion("TX12345678", "PEN");
        entityManager.persist(transaccion);
        entityManager.flush();

        // Buscar por ID
        Optional<Transaccion> encontrada = transaccionRepository.findById("TX12345678");

        // Verificar resultados
        assertTrue(encontrada.isPresent());
        assertEquals("TX12345678", encontrada.get().getCodTransaccion());
        assertEquals("PEN", encontrada.get().getEstado());
    }

    @Test
    void testFindByEstado() {
        // Crear y persistir transacciones con diferentes estados
        Transaccion transaccion1 = crearTransaccion("TX12345678", "PEN");
        Transaccion transaccion2 = crearTransaccion("TX87654321", "ACT");
        Transaccion transaccion3 = crearTransaccion("TX11223344", "PEN");
        
        entityManager.persist(transaccion1);
        entityManager.persist(transaccion2);
        entityManager.persist(transaccion3);
        entityManager.flush();

        // Buscar por estado
        List<Transaccion> transaccionesPendientes = transaccionRepository.findByEstado("PEN");

        // Verificar resultados
        assertEquals(2, transaccionesPendientes.size());
        assertTrue(transaccionesPendientes.stream()
                .allMatch(t -> "PEN".equals(t.getEstado())));
    }

    @Test
    void testFindByFechaBetween() {
        // Crear transacciones con diferentes fechas
        LocalDateTime ahora = LocalDateTime.now();
        
        Transaccion transaccion1 = crearTransaccion("TX12345678", "PEN");
        transaccion1.setFecha(ahora.minusDays(2));
        
        Transaccion transaccion2 = crearTransaccion("TX87654321", "ACT");
        transaccion2.setFecha(ahora);
        
        Transaccion transaccion3 = crearTransaccion("TX11223344", "PEN");
        transaccion3.setFecha(ahora.plusDays(2));
        
        entityManager.persist(transaccion1);
        entityManager.persist(transaccion2);
        entityManager.persist(transaccion3);
        entityManager.flush();

        // Buscar por rango de fechas
        List<Transaccion> transaccionesEnRango = transaccionRepository.findByFechaBetween(
                ahora.minusDays(1), ahora.plusDays(1));

        // Verificar resultados
        assertEquals(1, transaccionesEnRango.size());
        assertEquals("TX87654321", transaccionesEnRango.get(0).getCodTransaccion());
    }

    @Test
    void testFindByMarca() {
        // Crear transacciones con diferentes marcas
        Transaccion transaccion1 = crearTransaccion("TX12345678", "PEN");
        transaccion1.setMarca("VISA");
        
        Transaccion transaccion2 = crearTransaccion("TX87654321", "ACT");
        transaccion2.setMarca("MAST");
        
        Transaccion transaccion3 = crearTransaccion("TX11223344", "PEN");
        transaccion3.setMarca("VISA");
        
        entityManager.persist(transaccion1);
        entityManager.persist(transaccion2);
        entityManager.persist(transaccion3);
        entityManager.flush();

        // Buscar por marca
        List<Transaccion> transaccionesVisa = transaccionRepository.findByMarca("VISA");

        // Verificar resultados
        assertEquals(2, transaccionesVisa.size());
        assertTrue(transaccionesVisa.stream()
                .allMatch(t -> "VISA".equals(t.getMarca())));
    }

    @Test
    void testFindByTarjeta() {
        // Crear transacciones con diferentes tarjetas
        Transaccion transaccion1 = crearTransaccion("TX12345678", "PEN");
        transaccion1.setTarjeta("1234567890123456");
        
        Transaccion transaccion2 = crearTransaccion("TX87654321", "ACT");
        transaccion2.setTarjeta("9876543210987654");
        
        entityManager.persist(transaccion1);
        entityManager.persist(transaccion2);
        entityManager.flush();

        // Buscar por tarjeta
        List<Transaccion> transaccionesTarjeta = transaccionRepository.findByTarjeta("1234567890123456");

        // Verificar resultados
        assertEquals(1, transaccionesTarjeta.size());
        assertEquals("1234567890123456", transaccionesTarjeta.get(0).getTarjeta());
    }

    @Test
    void testFindByCodigoUnicoTransaccion() {
        // Crear transacción con código único
        Transaccion transaccion = crearTransaccion("TX12345678", "PEN");
        transaccion.setCodigoUnicoTransaccion("UUID-12345-67890");
        
        entityManager.persist(transaccion);
        entityManager.flush();

        // Buscar por código único
        Transaccion encontrada = transaccionRepository.findByCodigoUnicoTransaccion("UUID-12345-67890");

        // Verificar resultados
        assertNotNull(encontrada);
        assertEquals("UUID-12345-67890", encontrada.getCodigoUnicoTransaccion());
    }

    private Transaccion crearTransaccion(String codigo, String estado) {
        Transaccion transaccion = new Transaccion();
        transaccion.setCodTransaccion(codigo);
        transaccion.setTipo("PAG");
        transaccion.setMarca("VISA");
        transaccion.setMonto(new BigDecimal("100.00"));
        transaccion.setCodigoUnicoTransaccion("UUID-" + codigo);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setEstado(estado);
        transaccion.setMoneda("USD");
        transaccion.setPais("EC");
        transaccion.setTarjeta("1234567890123456");
        transaccion.setFechaCaducidad(LocalDate.now().plusYears(2));
        transaccion.setDiferido(false);
        return transaccion;
    }
} 