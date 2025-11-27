# Script PowerShell para crear el archivo OAR de ONOS manualmente
# El OAR es básicamente un archivo ZIP con estructura específica

Write-Host "Creando archivo OAR para ONOS..." -ForegroundColor Green

# Verificar que existe el JAR compilado
$jarPath = "target\flowinstall-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "ERROR: No se encuentra el archivo JAR en $jarPath" -ForegroundColor Red
    Write-Host "Ejecuta primero: mvn clean install" -ForegroundColor Yellow
    exit 1
}

# Verificar que existe app.xml
$appXmlPath = "src\main\resources\app.xml"
if (-not (Test-Path $appXmlPath)) {
    Write-Host "ERROR: No se encuentra app.xml en $appXmlPath" -ForegroundColor Red
    exit 1
}

# Crear directorio temporal
$tempDir = "target\oar-temp"
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Path $tempDir | Out-Null

Write-Host "Copiando archivos al directorio temporal..." -ForegroundColor Yellow

# Copiar el JAR (ONOS espera el nombre sin versión en algunos casos)
Copy-Item $jarPath "$tempDir\flowinstall-1.0.0.jar"

# Copiar app.xml a la raíz del OAR
Copy-Item $appXmlPath "$tempDir\app.xml"

# Verificar que los archivos se copiaron correctamente
if (-not (Test-Path "$tempDir\flowinstall-1.0.0.jar")) {
    Write-Host "ERROR: No se pudo copiar el JAR" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path "$tempDir\app.xml")) {
    Write-Host "ERROR: No se pudo copiar app.xml" -ForegroundColor Red
    exit 1
}

# Crear el archivo OAR (es un ZIP)
$oarPath = "target\flowinstall-1.0.0.oar"
if (Test-Path $oarPath) {
    Remove-Item -Force $oarPath
}

Write-Host "Creando archivo OAR..." -ForegroundColor Yellow

# Comprimir en ZIP primero (PowerShell solo acepta .zip)
$zipPath = "target\flowinstall-1.0.0.zip"
if (Test-Path $zipPath) {
    Remove-Item -Force $zipPath
}
Compress-Archive -Path "$tempDir\*" -DestinationPath $zipPath -Force

# Copiar y renombrar a .oar (un OAR es básicamente un ZIP)
Copy-Item $zipPath $oarPath -Force
Remove-Item $zipPath -Force

# Limpiar directorio temporal
Remove-Item -Recurse -Force $tempDir

Write-Host "`n¡Archivo OAR creado exitosamente!" -ForegroundColor Green
Write-Host "Ubicación: $oarPath" -ForegroundColor Cyan
Write-Host "`nPróximos pasos:" -ForegroundColor Yellow
Write-Host "1. Copia el archivo OAR a ONOS: copy $oarPath `$ONOS_ROOT\apps\" -ForegroundColor White
Write-Host "2. En la CLI de ONOS: app install org.example.flowinstall" -ForegroundColor White
Write-Host "3. Activa la aplicación: app activate org.example.flowinstall" -ForegroundColor White

