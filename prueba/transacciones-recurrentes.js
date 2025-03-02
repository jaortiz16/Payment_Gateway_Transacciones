const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const PORT = 3001;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Contador de transacciones recibidas
let contadorTransacciones = 0;

// Ruta para recibir transacciones recurrentes
app.post('/v1/transacciones-recurrentes', (req, res) => {
    const transaccion = req.body;
    contadorTransacciones++;
    
    console.log('\n=== NUEVA TRANSACCIÓN RECURRENTE RECIBIDA ===');
    console.log(`Nº: ${contadorTransacciones}`);
    console.log(`Fecha y Hora: ${new Date().toLocaleString()}`);
    console.log('DATOS RECIBIDOS COMPLETOS:');
    console.log(JSON.stringify(transaccion, null, 2));
    
    // Siempre responder con éxito
    res.status(200).json({
        estado: "RECIBIDA",
        mensaje: "Transacción recurrente registrada correctamente",
        idRecurrencia: `REC-${Date.now()}`
    });
});

// Agregar una ruta de verificación
app.get('/v1/transacciones-recurrentes/ping', (req, res) => {
    console.log('Ping recibido - Servicio funcionando correctamente');
    res.status(200).json({
        estado: "OK",
        mensaje: "Servicio de transacciones recurrentes activo"
    });
});

// Iniciar el servidor
app.listen(PORT, () => {
    console.log(`========================================================`);
    console.log(`=== SERVICIO DE TRANSACCIONES RECURRENTES DE PRUEBA ===`);
    console.log(`========================================================`);
    console.log(`Servidor iniciado en http://localhost:${PORT}`);
    console.log(`Ruta de recepción: http://localhost:${PORT}/v1/transacciones-recurrentes`);
    console.log(`Ping de verificación: http://localhost:${PORT}/v1/transacciones-recurrentes/ping`);
    console.log(`\nEste script simula el servicio de transacciones recurrentes.`);
    console.log(`Esperando transacciones recurrentes...`);
}); 