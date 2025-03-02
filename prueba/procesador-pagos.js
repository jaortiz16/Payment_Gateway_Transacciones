const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const readlineSync = require('readline-sync');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Cola de transacciones pendientes
const transaccionesPendientes = [];

// Ruta para procesar pagos
app.post('/procesar-pago', (req, res) => {
    const transaccion = req.body;
    console.log('\n\n=== NUEVA TRANSACCIÓN RECIBIDA ===');
    console.log(`ID: ${transaccion.codigoUnicoTransaccion}`);
    console.log(`POS: ${transaccion.codigoPOS} - Comercio: ${transaccion.codigoComercio}`);
    console.log(`Tipo: ${transaccion.tipo} - Marca: ${transaccion.marca} - Modalidad: ${transaccion.modalidad}`);
    console.log(`Monto: ${transaccion.monto} ${transaccion.moneda}`);
    console.log(`Tarjeta: ${transaccion.numeroTarjeta.substring(0, 6)}******${transaccion.numeroTarjeta.substring(12)}`);
    console.log(`Titular: ${transaccion.nombreTitular}`);
    
    // Mostrar el JSON completo
    console.log('\nJSON COMPLETO RECIBIDO:');
    console.log(JSON.stringify(transaccion, null, 2));

    // Agregar a la cola de transacciones pendientes
    transaccionesPendientes.push({
        id: transaccion.codigoUnicoTransaccion,
        data: transaccion,
        timestamp: new Date(),
        response: res
    });
    
    const tiempoEspera = transaccionesPendientes.length * 10; // Segundos estimados de espera
    console.log('\nPROCESANDO TRANSACCIÓN...');
    console.log(`Tiempo estimado de respuesta: ${tiempoEspera} segundos`);
    console.log('La API Spring Boot esperará una respuesta antes de continuar...');
    
    // Mostrar mensajes de proceso
    mostrarProcesoEspera();
});

// Iniciar el servidor
app.listen(PORT, () => {
    console.log(`===================================================`);
    console.log(`=== PROCESADOR DE PAGOS DE PRUEBA (SIMULACIÓN) ===`);
    console.log(`===================================================`);
    console.log(`Servidor iniciado en http://localhost:${PORT}`);
    console.log(`Ruta de procesamiento: http://localhost:${PORT}/procesar-pago`);
    console.log(`\nEste script simula un procesador de pagos real.`);
    console.log(`Las transacciones recibidas permanecerán en espera hasta`);
    console.log(`que usted decida aprobarlas o rechazarlas desde esta terminal.`);
    console.log(`\nLa API de Spring Boot se quedará esperando (cargando) hasta`);
    console.log(`que el procesador (este script) envíe una respuesta.`);
    console.log(`\nEsperando transacciones...`);
    
    // Iniciar el procesador de entrada de terminal
    procesarEntradaUsuario();
});

// Mostrar mensajes simulando procesamiento
function mostrarProcesoEspera() {
    const indice = transaccionesPendientes.length - 1;
    if (indice >= 0) {
        const transaccion = transaccionesPendientes[indice];
        console.log(`\n[${new Date().toLocaleTimeString()}] Esperando decisión para transacción ID: ${transaccion.id}`);
    }
}

// Función para procesar la entrada del usuario en la terminal
function procesarEntradaUsuario() {
    setInterval(() => {
        if (transaccionesPendientes.length > 0) {
            const transaccion = transaccionesPendientes[0];
            const tiempoEspera = Math.floor((new Date() - transaccion.timestamp) / 1000);
            
            // Mostrar mensaje de transacción pendiente
            console.log('\n=== TRANSACCIÓN PENDIENTE DE APROBACIÓN ===');
            console.log(`ID: ${transaccion.id}`);
            console.log(`Monto: ${transaccion.data.monto} ${transaccion.data.moneda}`);
            console.log(`Tarjeta: ${transaccion.data.numeroTarjeta.substring(0, 6)}******${transaccion.data.numeroTarjeta.substring(12)}`);
            console.log(`Tiempo en espera: ${tiempoEspera} segundos`);
            
            // Solicitar decisión al usuario
            const decision = readlineSync.question('\n¿Aprobar transacción? (s/n): ').toLowerCase();
            
            if (decision === 's' || decision === 'si') {
                console.log(`\n✅ Transacción ${transaccion.id} APROBADA`);
                console.log('Notificando a la API de Spring Boot...');
                transaccion.response.status(200).json({
                    estado: "APROBADA",
                    codigo: "00",
                    mensaje: "Transacción procesada exitosamente"
                });
            } else {
                console.log(`\n❌ Transacción ${transaccion.id} RECHAZADA`);
                console.log('Notificando a la API de Spring Boot con código 400...');
                transaccion.response.status(400).json({
                    estado: "RECHAZADA",
                    codigo: "01",
                    mensaje: "Transacción rechazada por el procesador"
                });
            }
            
            // Eliminar la transacción de la cola
            transaccionesPendientes.shift();
            
            console.log('\nEsperando nuevas transacciones...');
        }
    }, 1000); // Verificar cada segundo
} 