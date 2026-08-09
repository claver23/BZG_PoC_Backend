# ✅ Checklist de Despliegue - Webchat MOEBIUS

## Pre-despliegue

### 1. Verificar Requisitos

- [ ] Java 21 JDK instalado
  ```powershell
  java -version
  # Debe mostrar: java version "21.x.x"
  ```

- [ ] Maven instalado
  ```powershell
  mvn -version
  # Debe mostrar: Apache Maven 3.x
  ```

- [ ] Azure CLI instalado
  ```powershell
  az --version
  # Debe mostrar: azure-cli 2.x.x
  ```

### 2. Configurar Credenciales

- [ ] Variables de entorno configuradas
  ```powershell
  # Verificar
  echo $env:FOUNDRY_ENDPOINT
  echo $env:FOUNDRY_AGENT_NAME
  echo $env:FOUNDRY_AGENT_VERSION
  echo $env:FOUNDRY_API_KEY
  ```

- [ ] Archivo `local.settings.json` configurado con valores correctos

### 3. Verificar Compilación Local

- [ ] Compilación exitosa
  ```powershell
  mvn clean compile
  # BUILD SUCCESS
  ```

- [ ] Tests ejecutados correctamente
  ```powershell
  mvn test
  # Tests run: X, Failures: 0, Errors: 0, Skipped: 0
  ```

- [ ] Empaquetado de Azure Functions exitoso
  ```powershell
  mvn clean package azure-functions:package
  # 3 Azure Functions entry point(s) found
  ```

### 4. Verificar Archivos Generados

- [ ] Directorio de funciones creado
  ```powershell
  Test-Path "target\azure-functions\webchat-bz"
  # True
  ```

- [ ] Archivos function.json creados
  ```powershell
  Test-Path "target\azure-functions\webchat-bz\ping\function.json"
  Test-Path "target\azure-functions\webchat-bz\healthcheck\function.json"
  Test-Path "target\azure-functions\webchat-bz\chat\function.json"
  # All True
  ```

- [ ] Nivel de autorización correcto en function.json
  ```powershell
  Get-Content "target\azure-functions\webchat-bz\ping\function.json" | Select-String "authLevel"
  # "authLevel" : "FUNCTION"
  ```

## Despliegue

### 5. Autenticación en Azure

- [ ] Login en Azure CLI
  ```powershell
  az login
  ```

- [ ] Verificar suscripción correcta
  ```powershell
  az account show
  az account list --output table
  ```

- [ ] Seleccionar suscripción si es necesario
  ```powershell
  az account set --subscription "NOMBRE_O_ID_SUSCRIPCION"
  ```

### 6. Verificar Recursos de Azure

- [ ] Resource Group existe
  ```powershell
  az group show --name RG_MOEBIUS
  # Si no existe, crear:
  # az group create --name RG_MOEBIUS --location eastus2
  ```

- [ ] Storage Account existe (si es primera vez)
  ```powershell
  az storage account list --resource-group RG_MOEBIUS --output table
  ```

### 7. Desplegar

- [ ] **Opción 1: Despliegue con Maven (RECOMENDADO)**
  ```powershell
  mvn clean package azure-functions:deploy
  ```

- [ ] **Opción 2: Despliegue manual con ZIP**
  ```powershell
  # 1. Crear ZIP
  Compress-Archive -Path "target\azure-functions\webchat-bz\*" `
    -DestinationPath "target\webchat-bz.zip" -Force
  
  # 2. Verificar ZIP creado
  Test-Path "target\webchat-bz.zip"  # True
  
  # 3. Desplegar
  az functionapp deployment source config-zip `
    --resource-group RG_MOEBIUS `
    --name webchat-bz `
    --src target\webchat-bz.zip
  ```

### 8. Configurar Variables en Azure

- [ ] Configurar settings en Azure Function App
  ```powershell
  az functionapp config appsettings set `
    --name webchat-bz `
    --resource-group RG_MOEBIUS `
    --settings `
      "FOUNDRY_ENDPOINT=https://[foundry-name].services.ai.azure.com/api/projects/[project-name]" `
      "FOUNDRY_AGENT_NAME=Betty" `
      "FOUNDRY_AGENT_VERSION=2" `
      "FOUNDRY_API_KEY=tu-api-key-real"
  ```

## Post-despliegue

### 9. Obtener Function Keys

- [ ] Obtener las keys
  ```powershell
  az functionapp keys list --name webchat-bz --resource-group RG_MOEBIUS
  ```

- [ ] Guardar la key en un lugar seguro

### 10. Verificar Funciones

- [ ] Ping funciona
  ```powershell
  curl https://webchat-bz.azurewebsites.net/api/ping `
    -H "x-functions-key: TU_KEY_AQUI"
  # Respuesta: pong
  ```

- [ ] Healthcheck funciona
  ```powershell
  curl https://webchat-bz.azurewebsites.net/api/healthcheck `
    -H "x-functions-key: TU_KEY_AQUI"
  # Respuesta: JSON con status
  ```

- [ ] Chat funciona
  ```powershell
  curl -X POST https://webchat-bz.azurewebsites.net/api/chat `
    -H "Content-Type: application/json" `
    -H "x-functions-key: TU_KEY_AQUI" `
    -d '{\"message\": \"Hola\"}'
  # Respuesta: JSON con respuesta del agente
  ```

### 11. Monitoreo

- [ ] Ver logs en tiempo real
  ```powershell
  az functionapp log tail --name webchat-bz --resource-group RG_MOEBIUS
  ```

- [ ] Verificar métricas en Azure Portal
  - Ir a: https://portal.azure.com
  - Buscar: webchat-bz
  - Ver: Monitor → Metrics

### 12. Documentar

- [ ] URL de producción documentada
- [ ] Function Keys almacenadas de forma segura
- [ ] Variables de entorno documentadas
- [ ] Contact points actualizados

## Troubleshooting Común

### Error al compilar
```powershell
# Limpiar cache de Maven
mvn clean
rm -r target
mvn clean install -U
```

### Error de autenticación
```powershell
# Re-login
az logout
az login
az account show
```

### Funciones no encontradas
```powershell
# Verificar anotaciones
Get-Content src/main/java/com/etlions/webchat/function/*.java | Select-String "@FunctionName"
```

### Despliegue falla
```powershell
# Ver detalles del error
mvn clean package azure-functions:deploy -X

# O verificar logs
az functionapp log tail --name webchat-bz --resource-group RG_MOEBIUS
```

---

## 📊 Estado del Checklist

- Total de pasos: 12 categorías principales
- Tiempo estimado: 15-30 minutos (primera vez)
- Tiempo estimado: 5-10 minutos (actualizaciones)

---

**Última actualización:** Agosto 8, 2026

