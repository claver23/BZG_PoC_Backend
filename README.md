# Webchat MOEBIUS - PoC Notaría Beatriz Zevallos

> 📘 **Guía Rápida**: Para comandos esenciales y resolución rápida de problemas, consulta [QUICK_START.md](QUICK_START.md)
> 
> ✅ **Checklist de Despliegue**: Guía paso a paso con verificaciones en [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)

## 📋 Resumen del Proyecto

Este proyecto es una Prueba de Concepto (PoC) de un asistente virtual basado en IA para la Notaría Beatriz Zevallos. El sistema proporciona un servicio de chat inteligente que puede responder consultas sobre los servicios notariales disponibles, utilizando Azure AI Foundry (Agente Betty) como motor de inteligencia artificial.

### Características Principales

- **Chat Inteligente**: Servicio de chat conversacional basado en IA para consultas sobre servicios notariales
- **Integración con Azure AI Foundry**: Utiliza el agente "Betty" para procesamiento de lenguaje natural
- **API RESTful**: Endpoints HTTP seguros desplegados como Azure Functions
- **Arquitectura Serverless**: Basado en Azure Functions para escalabilidad automática
- **Spring Cloud Functions**: Integración de Spring Boot con Azure Functions

## 🎯 Alcance Actual

### Servicios Implementados

El sistema actualmente proporciona información sobre los siguientes servicios notariales:

1. **Escrituras Públicas**
   - Anticipo de Herencia

2. **Asuntos No Contenciosos**
   - Adopción de Persona Capaz

3. **Transferencias Vehiculares**
   - Compra/venta de vehículos
   - Transferencia de propiedad vehicular

4. **Cartas Notariales**
   - Diligenciamiento de cartas notariales

5. **Trámites Extra Protocolares**
   - Autorizaciones de viaje al interior

6. **Legalizaciones y Certificaciones**
   - Apertura de libros

### Endpoints Disponibles

- `POST /api/chat` - Servicio principal de chat conversacional
- `GET /api/healthcheck` - Verificación del estado del servicio
- `GET /api/ping` - Endpoint simple de disponibilidad

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 4.1.0**
- **Spring Cloud Function**
- **Azure Functions 3.1.0**
- **Azure AI Foundry**
- **Maven 3.x**
- **Lombok**

## 📦 Requisitos Previos

- Java 21 JDK
- Maven 3.x
- Azure CLI (para despliegue en Azure)
- Cuenta de Azure con acceso a Azure Functions
- API Key de Azure AI Foundry

## 🚀 Comandos de Despliegue

### 1. Configuración Inicial

```powershell
# Verificar versión de Java
java -version

# Verificar versión de Maven
mvn -version

# Clonar o ubicarse en el directorio del proyecto
cd C:\Users\USUARIO\Documents\ETLIONS\MOEBIUS\BZG_PoC\webchat
```

### 2. Configuración de Variables de Entorno

Antes de ejecutar, configure las siguientes variables de entorno o actualice el archivo `local.settings.json`:

```powershell
# Variables de entorno requeridas
$env:FOUNDRY_ENDPOINT="https://[foundry-name].services.ai.azure.com/api/projects/[project-name]"
$env:FOUNDRY_AGENT_NAME="Betty"
$env:FOUNDRY_AGENT_VERSION="2"
$env:FOUNDRY_API_KEY="tu-api-key-aqui"
```

### 3. Compilación del Proyecto

```powershell
# Limpiar compilaciones anteriores
mvn clean

# Compilar el proyecto
mvn compile

# Compilar y empaquetar (crea los JARs)
mvn package
```

### 4. Ejecución Local

```powershell
# Ejecutar en modo local con Spring Boot
mvn spring-boot:run

# O ejecutar con perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

La aplicación estará disponible en `http://localhost:8080/api`

### 5. Ejecución Local con Azure Functions Runtime

```powershell
# Empaquetar para Azure Functions
mvn package azure-functions:package

# Ejecutar localmente con Azure Functions Core Tools
cd target/azure-functions/webchat-bz
func start

# O desde la raíz del proyecto
mvn azure-functions:run
```

### 6. Despliegue a Azure

**Nota:** Azure Functions se puede desplegar de dos maneras principales:

1. **Maven Plugin (Opción A)**: El plugin de Azure Functions para Maven maneja todo automáticamente
2. **Azure CLI con ZIP (Opción B)**: Control manual del proceso creando un archivo ZIP del directorio generado

#### Opción A: Despliegue Automatizado con Maven (Recomendado)

```powershell
# 1. Login en Azure
az login

# 2. Compilar y desplegar en un solo comando
mvn clean package azure-functions:deploy
```

Este comando realizará:
- Compilación del proyecto
- Empaquetado de las Azure Functions
- Creación de la Function App (si no existe)
- Despliegue del código a Azure

#### Opción B: Despliegue Manual con Azure CLI

```powershell
# 1. Compilar y empaquetar
mvn clean package azure-functions:package

# 2. Login en Azure
az login

# 3. Crear resource group (si no existe)
az group create --name RG_MOEBIUS --location eastus2

# 4. Crear storage account (si no existe)
az storage account create `
  --name webchatbzstorage `
  --resource-group RG_MOEBIUS `
  --location eastus2 `
  --sku Standard_LRS

# 5. Crear Azure Function App (si no existe)
az functionapp create `
  --resource-group RG_MOEBIUS `
  --name webchat-bz `
  --consumption-plan-location eastus2 `
  --runtime java `
  --runtime-version 21 `
  --functions-version 4 `
  --os-type Linux `
  --storage-account webchatbzstorage

# 6. Configurar variables de aplicación
az functionapp config appsettings set `
  --name webchat-bz `
  --resource-group RG_MOEBIUS `
  --settings "FOUNDRY_ENDPOINT=https://[foundry-name].services.ai.azure.com/api/projects/[project-name]" `
             "FOUNDRY_AGENT_NAME=Betty" `
             "FOUNDRY_AGENT_VERSION=2" `
             "FOUNDRY_API_KEY=tu-api-key-aqui"

# 7. Crear archivo ZIP del directorio de Azure Functions
Compress-Archive -Path "target\azure-functions\webchat-bz\*" -DestinationPath "target\webchat-bz.zip" -Force

# 8. Desplegar el código usando ZIP
az functionapp deployment source config-zip `
  --resource-group RG_MOEBIUS `
  --name webchat-bz `
  --src target\webchat-bz.zip
```

### 7. Verificación del Despliegue

```powershell
# Verificar estado de la Function App
az functionapp show --name webchat-bz --resource-group RG_MOEBIUS

# Obtener las Function Keys
az functionapp keys list --name webchat-bz --resource-group RG_MOEBIUS

# Probar el endpoint de ping (requiere Function Key)
curl https://webchat-bz.azurewebsites.net/api/ping `
  -H "x-functions-key: tu-function-key"

# Probar el healthcheck (requiere Function Key)
curl https://webchat-bz.azurewebsites.net/api/healthcheck `
  -H "x-functions-key: tu-function-key"

# Probar el chat (requiere Function Key)
curl -X POST https://webchat-bz.azurewebsites.net/api/chat `
  -H "Content-Type: application/json" `
  -H "x-functions-key: tu-function-key" `
  -d '{\"message\": \"Hola, necesito información sobre transferencias vehiculares\"}'
```

## 🧪 Pruebas

```powershell
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con reporte de cobertura
mvn test jacoco:report
```

## 📝 Configuración

### Archivo `application.yaml`

Configuración principal de la aplicación:

```yaml
spring:
  application:
    name: webchat
  cloud:
    function:
      definition: healthcheck;chat

foundry:
  endpoint: ${FOUNDRY_ENDPOINT}
  agent-name: ${FOUNDRY_AGENT_NAME}
  agent-version: ${FOUNDRY_AGENT_VERSION}
  api-key: ${FOUNDRY_API_KEY}
```

### Archivo `local.settings.json`

Configuración para ejecución local con Azure Functions:

```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "FOUNDRY_ENDPOINT": "https://[foundry-name].services.ai.azure.com/api/projects/[project-name]",
    "FOUNDRY_AGENT_NAME": "Betty",
    "FOUNDRY_AGENT_VERSION": "2",
    "FOUNDRY_API_KEY": "tu-api-key-aqui"
  }
}
```

## 🔒 Seguridad

- **Todos los endpoints** requieren autenticación a nivel de función (Function Level Authorization)
- Cada función (`chat`, `ping`, `healthcheck`) tiene su propio archivo y configuración de seguridad
- Las API Keys de Azure AI Foundry se configuran como variables de entorno
- CORS está habilitado para integración con frontends
- Se requiere Function Key en los headers de las peticiones: `x-functions-key`

## 📊 Estructura del Proyecto

```
webchat/
├── src/
│   ├── main/
│   │   ├── java/com/etlions/webchat/
│   │   │   ├── config/          # Configuraciones
│   │   │   ├── dto/             # Objetos de transferencia de datos
│   │   │   ├── function/        # Azure Functions
│   │   │   │   ├── ChatFunction.java
│   │   │   │   ├── HealthCheckFunction.java
│   │   │   │   └── PingFunction.java
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── support/         # Utilidades
│   │   │   └── WebchatApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-local.yaml
│   │       └── data-ingest/     # Datos de servicios notariales
│   └── test/                    # Pruebas unitarias
├── pom.xml                      # Configuración Maven
├── host.json                    # Configuración Azure Functions
└── local.settings.json          # Configuración local
```

## 🔄 Próximos Pasos / Roadmap

- [ ] Ampliar el catálogo de servicios notariales
- [ ] Implementar sistema de feedback de usuarios
- [ ] Agregar soporte multiidioma
- [ ] Implementar caché de respuestas frecuentes
- [ ] Agregar analytics y métricas de uso
- [ ] Crear interfaz de usuario web
- [ ] Implementar autenticación de usuarios
- [ ] Agregar integración con sistemas de agendamiento

## 📞 Soporte

Para consultas sobre el proyecto, contactar al equipo de ETLIONS MOEBIUS.

## 📄 Licencia

Proyecto propietario - ETLIONS © 2026

---

**Versión:** 0.0.1-SNAPSHOT  
**Última actualización:** Agosto 2026








