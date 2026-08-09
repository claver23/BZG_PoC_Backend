# Guía Rápida - Webchat MOEBIUS

## 🚀 Comandos Esenciales

### Desarrollo Local

```powershell
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar localmente con Spring Boot
mvn spring-boot:run

# Ejecutar localmente con Azure Functions Runtime
mvn clean package azure-functions:run
```

### Despliegue a Azure

```powershell

# Solo empaquetar (sin desplegar)
mvn clean package azure-functions:package
```

### Verificación del Despliegue

```powershell
# Obtener las Function Keys
az functionapp keys list --name webchat-bz --resource-group RG_MOEBIUS

# Probar ping
curl https://webchat-bz.azurewebsites.net/api/ping -H "x-functions-key: TU_KEY"

# Probar healthcheck
curl https://webchat-bz.azurewebsites.net/api/healthcheck -H "x-functions-key: TU_KEY"

# Probar chat
curl -X POST https://webchat-bz.azurewebsites.net/api/chat `
  -H "Content-Type: application/json" `
  -H "x-functions-key: TU_KEY" `
  -d '{\"message\": \"Hola\"}'
```

## 📁 Archivos Generados al Compilar

```
target/
├── webchat-0.0.1-SNAPSHOT.jar          # JAR delgado (thin JAR)
├── webchat-0.0.1-SNAPSHOT-exec.jar     # JAR ejecutable con dependencias
└── azure-functions/
    └── webchat-bz/                      # Directorio de Azure Functions
        ├── chat/function.json
        ├── healthcheck/function.json
        ├── ping/function.json
        ├── lib/                         # Dependencias
        ├── webchat-0.0.1-SNAPSHOT.jar
        ├── host.json
        └── local.settings.json
```

## 🔑 Variables de Entorno Requeridas

```powershell
$env:FOUNDRY_ENDPOINT="https://[foundry-name].services.ai.azure.com/api/projects/[project-name]"
$env:FOUNDRY_AGENT_NAME="Betty"
$env:FOUNDRY_AGENT_VERSION="2"
$env:FOUNDRY_API_KEY="tu-api-key-aqui"
```

## 📝 Configuración en pom.xml

```xml
<azure.functions.app-name>webchat-bz</azure.functions.app-name>
<azure.functions.resource-group>RG_MOEBIUS</azure.functions.resource-group>
<azure.functions.region>eastus2</azure.functions.region>
```

## ⚠️ Troubleshooting

### Error: "No Azure Functions found"
```powershell
# Asegúrate de que las anotaciones @FunctionName están presentes
mvn clean compile
```

### Error: "Authentication failed"
```powershell
# Re-autenticarse en Azure
az login
az account show
```

### Error al desplegar
```powershell
# Verificar que el resource group existe
az group show --name RG_MOEBIUS

# Listar las Function Apps
az functionapp list --resource-group RG_MOEBIUS --output table
```

## 🔍 Logs y Monitoreo

```powershell
# Ver logs en tiempo real
az functionapp log tail --name webchat-bz --resource-group RG_MOEBIUS

# Ver configuración de la app
az functionapp config appsettings list --name webchat-bz --resource-group RG_MOEBIUS

# Ver estado de la Function App
az functionapp show --name webchat-bz --resource-group RG_MOEBIUS --query state
```

## 🏗️ Estructura de Funciones

| Función | Método | Ruta | Autorización | Descripción |
|---------|--------|------|--------------|-------------|
| ping | GET | `/api/ping` | FUNCTION | Verificación básica |
| healthcheck | GET | `/api/healthcheck` | FUNCTION | Estado del servicio |
| chat | POST | `/api/chat` | FUNCTION | Chat con IA |

## 📦 Crear ZIP Manualmente (Opcional - No Recomendado)

```powershell
# Solo si necesitas crear un ZIP manualmente para algún propósito
Compress-Archive -Path "target\azure-functions\webchat-bz\*" `
  -DestinationPath "target\webchat-bz.zip" `
  -Force
```

**Nota:** Esto NO es necesario para el despliegue normal. El comando `mvn azure-functions:deploy` maneja todo automáticamente.

---

Para más información detallada, consulta el [README.md](README.md) completo.

