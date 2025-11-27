#!/bin/bash
# Script bash para crear el archivo OAR de ONOS manualmente
# El OAR es básicamente un archivo ZIP con estructura específica

echo "Creando archivo OAR para ONOS..."

# Verificar que existe el JAR compilado
JAR_PATH="target/flowinstall-1.0.0.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "ERROR: No se encuentra el archivo JAR en $JAR_PATH"
    echo "Ejecuta primero: mvn clean install"
    exit 1
fi

# Verificar que existe app.xml
APP_XML_PATH="src/main/resources/app.xml"
if [ ! -f "$APP_XML_PATH" ]; then
    echo "ERROR: No se encuentra app.xml en $APP_XML_PATH"
    exit 1
fi

# Crear directorio temporal
TEMP_DIR="target/oar-temp"
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"

echo "Copiando archivos al directorio temporal..."

# Copiar el JAR
cp "$JAR_PATH" "$TEMP_DIR/flowinstall-1.0.0.jar"

# Copiar app.xml
cp "$APP_XML_PATH" "$TEMP_DIR/app.xml"

# Verificar que los archivos se copiaron correctamente
if [ ! -f "$TEMP_DIR/flowinstall-1.0.0.jar" ]; then
    echo "ERROR: No se pudo copiar el JAR"
    exit 1
fi
if [ ! -f "$TEMP_DIR/app.xml" ]; then
    echo "ERROR: No se pudo copiar app.xml"
    exit 1
fi

# Crear el archivo OAR (es un ZIP)
OAR_PATH="target/flowinstall-1.0.0.oar"
rm -f "$OAR_PATH"

echo "Creando archivo OAR..."

# Comprimir en ZIP (OAR es básicamente un ZIP)
cd "$TEMP_DIR"
zip -r "../flowinstall-1.0.0.oar" *
cd - > /dev/null

# Limpiar directorio temporal
rm -rf "$TEMP_DIR"

echo ""
echo "¡Archivo OAR creado exitosamente!"
echo "Ubicación: $OAR_PATH"
echo ""
echo "Próximos pasos:"
echo "1. Copia el archivo OAR a ONOS: cp $OAR_PATH \$ONOS_ROOT/apps/"
echo "2. En la CLI de ONOS: app install org.example.flowinstall"
echo "3. Activa la aplicación: app activate org.example.flowinstall"

