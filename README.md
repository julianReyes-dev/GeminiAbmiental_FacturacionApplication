# Microservicio de Facturación - Gemini Ambiental

## Descripción
Microservicio encargado de la gestión completa de facturación de servicios ambientales, incluyendo emisión, validación de pagos y generación de reportes.

## Características Principales
- Emisión automática de facturas
- Gestión de estados (Pendiente, Pagada, Vencida, Anulada)
- Procesamiento automático de facturas vencidas
- Dashboard con estadísticas en tiempo real
- Filtros avanzados de búsqueda
- API REST completa con documentación Swagger
- Manejo de transacciones y excepciones
- Tests unitarios integrados

## Tecnologías Utilizadas
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven 3.9+**
- **Java 17**
- **Swagger/OpenAPI 3**
- **Lombok**

## Endpoints Principales

### Facturación
- `POST /api/facturacion/emitir` - Emitir nueva factura
- `PUT /api/facturacion/{id}/marcar-pagada` - Marcar factura como pagada
- `PUT /api/facturacion/{id}/anular` - Anular factura
- `GET /api/facturacion` - Listar facturas con filtros
- `GET /api/facturacion/estadisticas` - Obtener estadísticas

### Servicios de Apoyo
- `GET /api/facturacion/servicios-para-facturar` - Servicios pendientes de facturar
- `POST /api/facturacion/procesar-vencidas` - Procesar facturas vencidas manualmente

## Configuración

### Base de Datos
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gemini_ambiental_db
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Variables de Entorno
- `DB_USERNAME`: Usuario de base de datos
- `DB_PASSWORD`: Contraseña de base de datos

## Ejecución

### Desarrollo
```bash
mvn spring-boot:run
```

### Producción
```bash
mvn clean package
java -jar target/facturacion-microservice-1.0.0.jar
```

## Documentación API
Una vez ejecutado, la documentación estará disponible en:
- Swagger UI: http://localhost:8083/facturacion-api/swagger-ui.html
- OpenAPI Docs: http://localhost:8083/facturacion-api/api-docs

## Monitoreo
- Health Check: http://localhost:8083/facturacion-api/actuator/health
- Métricas: http://localhost:8083/facturacion-api/actuator/metrics

## Estructura del Proyecto
```
src/
├── main/java/com/geminiambiental/facturacion/
│   ├── controller/          # Controladores REST
│   ├── service/            # Lógica de negocio
│   ├── repository/         # Acceso a datos
│   ├── entity/             # Entidades JPA
│   ├── dto/                # Data Transfer Objects
│   ├── config/             # Configuraciones
│   ├── exception/          # Manejo de excepciones
│   └── scheduler/          # Tareas programadas
└── test/                   # Tests unitarios
```

## Proceso de Facturación
1. **Servicios Completados** → Se marcan como listos para facturar
2. **Emisión de Factura** → Se agrupan servicios y se calcula monto total
3. **Gestión de Estados** → Seguimiento automático de pagos y vencimientos
4. **Reportes y Analytics** → Dashboard con métricas en tiempo real

## Guía de Pruebas

### **Preparación Inicial**

#### 1. Configurar Base de Datos
```sql
-- Ejecutar el script SQL que proporcionaste para crear las tablas
-- Luego insertar datos de prueba:

-- Insertar direcciones
INSERT INTO Direccion (ID_direccion, nombre, descripcion_adicional, depende_de) VALUES
('DIR-001', 'Colombia', 'País', NULL),
('DIR-002', 'Boyacá', 'Departamento', 'DIR-001'),
('DIR-003', 'Tunja', 'Ciudad', 'DIR-002');

-- Insertar categorías
INSERT INTO Categoria_servicio (ID_categoria_servicio, nombre, descripcion) VALUES
('CAT-001', 'Control de Plagas', 'Servicios de control y eliminación de plagas'),
('CAT-002', 'Lavado de Tanques', 'Limpieza y mantenimiento de tanques'),
('CAT-003', 'Certificaciones', 'Certificaciones ambientales');

INSERT INTO Categoria_producto (ID_categoria_producto, nombre, descripcion) VALUES
('PROD-CAT-001', 'Insecticidas', 'Productos para control de insectos'),
('PROD-CAT-002', 'Equipos', 'Equipos y herramientas');

-- Insertar cargos
INSERT INTO Cargo_Especialidad (ID_cargo_especialidad, nombre, descripcion, ID_categoria_servicio) VALUES
('CARGO-001', 'Técnico en Fumigación', 'Especialista en control de plagas', 'CAT-001'),
('CARGO-002', 'Cliente Empresarial', 'Cliente persona jurídica', NULL);

-- Insertar tipos de servicio
INSERT INTO Tipo_servicio (ID_tipo_servicio, nombre_servicio, descripcion, costo, frecuencia, duracion, estado, ID_categoria_servicio) VALUES
('TS-001', 'Fumigación Integral', 'Control total de plagas en instalaciones', 850000.00, 'Mensual', '4 horas', 'Activo', 'CAT-001'),
('TS-002', 'Lavado de Tanques', 'Limpieza profunda de tanques de almacenamiento', 450000.00, 'Trimestral', '6 horas', 'Activo', 'CAT-002'),
('TS-003', 'Certificación Ambiental', 'Evaluación y certificación de procesos', 320000.00, 'Anual', '8 horas', 'Activo', 'CAT-003');

-- Insertar productos
INSERT INTO Producto (ID_producto, nombre, precio_actual, stock, unidad_medida, ID_categoria_producto) VALUES
('PROD-001', 'Insecticida Premium', 45000.00, 50, 'Litro', 'PROD-CAT-001'),
('PROD-002', 'Equipo Fumigador', 25000.00, 10, 'Unidad', 'PROD-CAT-002'),
('PROD-003', 'Detergente Especializado', 15000.00, 100, 'Galón', 'PROD-CAT-002');

-- Insertar personas (clientes y empleados)
INSERT INTO Persona (DNI, tipo_dni, nombre, telefono, correo, rol, ID_direccion, ID_cargo_especialidad) VALUES
('12345678', 'CC', 'Hotel Plaza Real', '3001234567', 'gerencia@plazareal.com', 'Cliente', 'DIR-003', 'CARGO-002'),
('87654321', 'NIT', 'Restaurante El Buen Sabor', '3007654321', 'admin@buensabor.com', 'Cliente', 'DIR-003', 'CARGO-002'),
('11111111', 'CC', 'Colegio San Patricio', '3001111111', 'rector@sanpatricio.edu', 'Cliente', 'DIR-003', 'CARGO-002'),
('22222222', 'CC', 'Panadería Central', '3002222222', 'info@panaderiacentral.com', 'Cliente', 'DIR-003', 'CARGO-002'),
('EMP-001', 'CC', 'Juan Pérez', '3009999999', 'juan.perez@gemini.com', 'Empleado', 'DIR-003', 'CARGO-001');

-- Insertar cotizaciones
INSERT INTO Cotizacion (ID_cotizacion, DNI_cliente, DNI_empleado, estado, fecha_solicitud, fecha_preferida, fecha_respuesta, prioridad, descripcion_problema, costo_total_cotizacion) VALUES
('COT-001', '12345678', 'EMP-001', 'Aprobada', '2025-01-10 09:00:00', '2025-01-15', '2025-01-11 14:30:00', 'Alta', 'Problema de roedores en cocina', 850000.00),
('COT-002', '87654321', 'EMP-001', 'Aprobada', '2025-01-12 10:00:00', '2025-01-18', '2025-01-13 16:00:00', 'Media', 'Limpieza de tanques de agua', 450000.00),
('COT-003', '11111111', 'EMP-001', 'Aprobada', '2025-01-15 11:00:00', '2025-01-20', '2025-01-16 09:00:00', 'Baja', 'Certificación para licencia', 320000.00),
('COT-004', '22222222', 'EMP-001', 'Aprobada', '2025-01-18 08:00:00', '2025-01-22', '2025-01-19 15:00:00', 'Alta', 'Control de cucarachas', 520000.00);

-- Insertar servicios COMPLETADOS (listos para facturar)
INSERT INTO Servicio (ID_servicio, ID_cotizacion, DNI_empleado_asignado, fecha, hora, duracion_estimada, observaciones, prioridad, estado) VALUES
('SRV-001', 'COT-001', 'EMP-001', '2025-01-15', '08:00:00', '4 horas', 'Fumigación completada exitosamente', 'Alta', 'COMPLETADO'),
('SRV-002', 'COT-002', 'EMP-001', '2025-01-18', '09:00:00', '6 horas', 'Tanques limpiados y desinfectados', 'Media', 'COMPLETADO'),
('SRV-003', 'COT-003', 'EMP-001', '2025-01-20', '10:00:00', '8 horas', 'Certificación otorgada', 'Baja', 'COMPLETADO'),
('SRV-004', 'COT-004', 'EMP-001', '2025-01-22', '07:00:00', '4 horas', 'Control de plagas efectivo', 'Alta', 'COMPLETADO');

-- Insertar productos utilizados en servicios
INSERT INTO servicio_producto (ID_servicio, ID_producto, cantidad, precio_actual) VALUES
('SRV-001', 'PROD-001', 2, 45000.00),
('SRV-001', 'PROD-002', 1, 25000.00),
('SRV-002', 'PROD-003', 3, 15000.00),
('SRV-003', 'PROD-002', 1, 25000.00),
('SRV-004', 'PROD-001', 1, 45000.00);
```

Verificar que esté funcionando:
- **Health Check**: http://localhost:8083/facturacion-api/actuator/health
- **Swagger UI**: http://localhost:8083/facturacion-api/swagger-ui.html


### **Casos de Prueba**

#### **CASO 1: Obtener Estadísticas del Dashboard**

**Request:**
```http
GET http://localhost:8083/facturacion-api/api/facturacion/estadisticas
```

**Respuesta Esperada:**
```json
{
  "facturasPendientes": 0,
  "facturasVencidas": 0,
  "facturasPagadas": 0,
  "facturasAnuladas": 0,
  "montoTotalPendiente": 0,
  "montoTotalVencido": 0,
  "montoTotalPagado": 0
}
```

#### **CASO 2: Ver Servicios Listos para Facturar**

**Request:**
```http
GET http://localhost:8083/facturacion-api/api/facturacion/servicios-para-facturar
```

**Respuesta Esperada:**
```json
[
  {
    "idServicio": "SRV-001",
    "fecha": "2025-01-15",
    "estado": "COMPLETADO",
    "observaciones": "Fumigación completada exitosamente",
    // ... más campos
  },
  // ... más servicios
]
```

#### **CASO 3: Emitir Primera Factura** 

**Request:**
```http
POST http://localhost:8083/facturacion-api/api/facturacion/emitir
Content-Type: application/json

{
  "idsServicios": ["SRV-001"],
  "observaciones": "Factura por servicio de fumigación en Hotel Plaza Real"
}
```

**Respuesta Esperada:**
```json
{
  "idFactura": "F-2025-001",
  "dniCliente": "12345678",
  "fechaEmision": "2025-01-23",
  "fechaVencimiento": "2025-02-22",
  "montoTotal": 115000.00,
  "estado": "PENDIENTE",
  "observaciones": "Factura por servicio de fumigación en Hotel Plaza Real"
}
```

#### **CASO 4: Emitir Factura Múltiple**

**Request:**
```http
POST http://localhost:8083/facturacion-api/api/facturacion/emitir
Content-Type: application/json

{
  "idsServicios": ["SRV-002", "SRV-003"],
  "observaciones": "Factura múltiple - Lavado de tanques y certificación"
}
```

#### **CASO 5: Listar Facturas con Filtros**

**Request:**
```http
GET http://localhost:8083/facturacion-api/api/facturacion?estado=PENDIENTE&page=0&size=10
```

**Respuesta Esperada:**
```json
{
  "content": [
    {
      "idFactura": "F-2025-001",
      "estado": "PENDIENTE",
      "montoTotal": 115000.00,
      // ... más campos
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

#### **CASO 6: Marcar Factura como Pagada**

**Request:**
```http
PUT http://localhost:8083/facturacion-api/api/facturacion/F-2025-001/marcar-pagada
```

**Respuesta Esperada:**
```json
{
  "idFactura": "F-2025-001",
  "estado": "PAGADA",
  "fechaPago": "2025-01-23T14:30:00",
  "montoTotal": 115000.00
}
```

#### **CASO 7: Anular Factura**

**Request:**
```http
PUT http://localhost:8083/facturacion-api/api/facturacion/F-2025-002/anular
Content-Type: application/json

{
  "motivo": "Error en el servicio registrado"
}
```

#### **CASO 8: Verificar Estadísticas Actualizadas**

**Request:**
```http
GET http://localhost:8083/facturacion-api/api/facturacion/estadisticas
```

**Respuesta Esperada:**
```json
{
  "facturasPendientes": 1,
  "facturasVencidas": 0,
  "facturasPagadas": 1,
  "facturasAnuladas": 1,
  "montoTotalPendiente": 70000.00,
  "montoTotalPagado": 115000.00
}
```

## 🔧 **Herramientas para Pruebas**

### **Opción 1: Postman**
1. Descargar [Postman](https://www.postman.com/)
2. Crear nueva colección "Facturación Microservice"
3. Agregar los requests de arriba
4. Configurar environment con `baseUrl = http://localhost:8083/facturacion-api`

### **Opción 2: Swagger UI (Recomendado)**
1. Ir a http://localhost:8083/facturacion-api/swagger-ui.html
2. Explorar todos los endpoints disponibles
3. Usar "Try it out" para probar cada endpoint
4. Ver respuestas en tiempo real

##  **Solución de Problemas Comunes**

### **Error de Conexión a BD:**
```
Error: com.mysql.cj.jdbc.exceptions.CommunicationsException
```
**Solución:** Verificar que MySQL esté ejecutándose y los datos de conexión sean correctos.

### **Error de Tablas No Encontradas:**
```
Error: Table 'gemini_ambiental_DB.Factura' doesn't exist
```
**Solución:** Ejecutar el script SQL completo para crear todas las tablas.

### **Error de Servicios No Encontrados:**
```
Error: No se encontraron servicios válidos
```
**Solución:** Verificar que existan servicios con estado 'Completado' en la BD.

### **Error de Puerto en Uso:**
```
Error: Port 8083 was already in use
```
**Solución:** Cambiar el puerto en `application.yml` o detener el proceso que usa el puerto 8083.
