# API de Transacciones - Pasarela de Pagos Banquito

Esta API permite gestionar todas las transacciones de la pasarela de pagos de Banquito, ofreciendo diferentes endpoints para crear, consultar y buscar transacciones aplicando diversos filtros.

## URL Base

```
http://localhost:8080/api/v1/transacciones
```

## Endpoints Disponibles

### 1. Crear una Transacción desde un POS

**Método**: POST  
**URI**: `/`  
**Descripción**: Crea una nueva transacción en el sistema de pago.

**Cuerpo de la Petición**:
```json
{
  "codigoPOS": "POS123456",
  "codigoComercio": "COM789012",
  "tipo": "PAG",
  "marca": "VISA",
  "modalidad": "SIM",
  "monto": 150.50,
  "moneda": "USD",
  "pais": "EC",
  "plazo": null,
  "numeroTarjeta": "1234567812345678",
  "nombreTitular": "Juan Pérez",
  "codigoSeguridad": 123,
  "fechaExpiracion": "12/25",
  "codigoUnicoTransaccion": "TRX123456789",
  "referencia": "REF001",
  "recurrente": false,
  "frecuenciaDias": null
}
```

**Respuesta**: Devuelve un objeto `TransaccionDTO` con la información de la transacción creada.

### 2. Obtener una Transacción por ID

**Método**: GET  
**URI**: `/{id}`  
**Descripción**: Retorna una transacción basada en su ID único.

**Parámetros**:
- `id` (path): ID de la transacción

**Respuesta**: Objeto `TransaccionDTO` con los detalles de la transacción.

### 3. Obtener una Transacción por Código Único

**Método**: GET  
**URI**: `/codigo-unico/{codigoUnico}`  
**Descripción**: Retorna una transacción basada en su código único de transacción.

**Parámetros**:
- `codigoUnico` (path): Código único de la transacción

**Respuesta**: Objeto `TransaccionDTO` con los detalles de la transacción.

### 4. Buscar Transacciones con Filtros y Paginación

**Método**: GET  
**URI**: `/`  
**Descripción**: Permite buscar transacciones aplicando diferentes filtros y paginación.

**Parámetros**:
- `estado` (query, opcional): Estado de la transacción (ACT, INA, PEN, REC)
- `fechaInicio` (query, opcional): Fecha inicio para filtrar (ISO DateTime)
- `fechaFin` (query, opcional): Fecha fin para filtrar (ISO DateTime)
- `marca` (query, opcional): Marca de la tarjeta (VISA, MAST, AMEX, etc)
- `tipo` (query, opcional): Tipo de transacción (PAG, RET, TRA, DEV)
- `page` (query, opcional, default: 0): Número de página (comenzando en 0)
- `size` (query, opcional, default: 10): Tamaño de página
- `sort` (query, opcional, default: "fecha"): Campo para ordenar
- `direction` (query, opcional, default: "DESC"): Dirección del ordenamiento (ASC, DESC)

**Respuesta**: Objeto `PageResponseDTO<TransaccionDTO>` con la siguiente estructura:
```json
{
  "content": [
    {
      "codTransaccion": "TRX001",
      "tipo": "PAG",
      "marca": "VISA",
      "monto": 150.50,
      "codigoUnicoTransaccion": "TRX123456789",
      "fecha": "2023-05-01T10:30:00",
      "estado": "ACT",
      "moneda": "USD",
      "pais": "EC",
      "tarjeta": "1234567812345678",
      "fechaCaducidad": "2025-12-31",
      "transaccionEncriptada": "...",
      "swiftBanco": "BANKEC12",
      "cuentaIban": "EC12345678901234567890",
      "diferido": false,
      "esDiferido": false,
      "diaMesPago": null,
      "fechaInicio": null,
      "fechaFin": null,
      "codigoSeguridad": 123,
      "frecuenciaDias": null
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 50,
  "totalPages": 5,
  "firstPage": true,
  "lastPage": false
}
```

### 5. Buscar Transacciones por Número de Tarjeta

**Método**: GET  
**URI**: `/tarjeta/{numeroTarjeta}`  
**Descripción**: Retorna todas las transacciones asociadas a un número de tarjeta específico.

**Parámetros**:
- `numeroTarjeta` (path): Número de tarjeta (16 dígitos)

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 6. Buscar Transacciones por Código SWIFT de Banco

**Método**: GET  
**URI**: `/swift-banco/{swiftBanco}`  
**Descripción**: Retorna todas las transacciones asociadas a un banco específico por su código SWIFT.

**Parámetros**:
- `swiftBanco` (path): Código SWIFT del banco

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 7. Buscar Transacciones por Cuenta IBAN

**Método**: GET  
**URI**: `/cuenta-iban/{cuentaIban}`  
**Descripción**: Retorna todas las transacciones asociadas a una cuenta bancaria específica por su IBAN.

**Parámetros**:
- `cuentaIban` (path): Número de cuenta IBAN

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 8. Buscar Transacciones por Moneda

**Método**: GET  
**URI**: `/moneda/{moneda}`  
**Descripción**: Retorna todas las transacciones realizadas en una moneda específica.

**Parámetros**:
- `moneda` (path): Código de moneda (USD, EUR, etc)

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 9. Buscar Transacciones por País

**Método**: GET  
**URI**: `/pais/{pais}`  
**Descripción**: Retorna todas las transacciones realizadas en un país específico.

**Parámetros**:
- `pais` (path): Código de país ISO (2 caracteres)

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 10. Buscar Transacciones por Rango de Monto

**Método**: GET  
**URI**: `/monto`  
**Descripción**: Retorna transacciones filtradas por un rango de monto.

**Parámetros** (al menos uno debe estar presente):
- `minimo` (query, opcional): Monto mínimo
- `maximo` (query, opcional): Monto máximo

**Respuesta**: Lista de objetos `TransaccionDTO`.

### 11. Procesar una Transacción Recurrente

**Método**: POST  
**URI**: `/recurrentes`  
**Descripción**: Procesa una transacción recurrente proveniente del microservicio de transacciones recurrentes.

**Cuerpo de la Petición**:
```json
{
  "marca": "VISA",
  "monto": 100.00,
  "moneda": "USD",
  "pais": "EC",
  "numeroTarjeta": "1234567812345678",
  "fechaExpiracion": "12/25",
  "swift_banco": "BANKEC12",
  "cuenta_iban": "EC12345678901234567890",
  "cvv": 123,
  "frecuenciaDias": 30
}
```

**Respuesta**: Objeto `TransaccionDTO` con los detalles de la transacción recurrente procesada.

## Modelos de Datos

### TransaccionDTO

```json
{
  "codTransaccion": "TRX001",
  "tipo": "PAG",
  "marca": "VISA",
  "monto": 150.50,
  "codigoUnicoTransaccion": "TRX123456789",
  "fecha": "2023-05-01T10:30:00",
  "estado": "ACT",
  "moneda": "USD",
  "pais": "EC",
  "tarjeta": "1234567812345678",
  "fechaCaducidad": "2025-12-31",
  "transaccionEncriptada": "...",
  "swiftBanco": "BANKEC12",
  "cuentaIban": "EC12345678901234567890",
  "diferido": false,
  "esDiferido": false,
  "diaMesPago": null,
  "fechaInicio": null,
  "fechaFin": null,
  "codigoSeguridad": 123,
  "frecuenciaDias": null
}
```

### TransaccionPosDTO

```json
{
  "codigoPOS": "POS123456",
  "codigoComercio": "COM789012",
  "tipo": "PAG",
  "marca": "VISA",
  "modalidad": "SIM",
  "monto": 150.50,
  "moneda": "USD",
  "pais": "EC",
  "plazo": null,
  "numeroTarjeta": "1234567812345678",
  "nombreTitular": "Juan Pérez",
  "codigoSeguridad": 123,
  "fechaExpiracion": "12/25",
  "codigoUnicoTransaccion": "TRX123456789",
  "referencia": "REF001",
  "recurrente": false,
  "frecuenciaDias": null
}
```

### TransaccionRecurrenteInboundDTO

```json
{
  "marca": "VISA",
  "monto": 100.00,
  "moneda": "USD",
  "pais": "EC",
  "numeroTarjeta": "1234567812345678",
  "fechaExpiracion": "12/25",
  "swift_banco": "BANKEC12",
  "cuenta_iban": "EC12345678901234567890",
  "cvv": 123,
  "frecuenciaDias": 30
}
```

## Códigos de Error

- **404**: Transacción no encontrada
- **400**: Datos de transacción inválidos o parámetros de búsqueda inválidos

## Valores Válidos

### Tipo de Transacción
- PAG: Pago
- RET: Retiro
- TRA: Transferencia
- DEV: Devolución

### Estado de Transacción
- ACT: Activa
- INA: Inactiva
- PEN: Pendiente
- REC: Rechazada

### Modalidad
- SIM: Simple (pago único)
- DIF: Diferida
- REC: Recurrente 