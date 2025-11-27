# Flow Install Application para ONOS 2.7.0

Aplicación ONOS para instalar flujos OpenFlow 1.3.

## Compilar

```bash
mvn clean install
```

## Generar OAR

```bash
powershell -ExecutionPolicy Bypass -File crear-oar.ps1
```

El archivo OAR se generará en: `target/flowinstall-1.0.0.oar`

## Instalar en ONOS

1. Copiar OAR a ONOS:
   ```bash
   copy target\flowinstall-1.0.0.oar %ONOS_ROOT%\apps\
   ```

2. En CLI de ONOS:
   ```bash
   app install org.example.flowinstall
   app activate org.example.flowinstall
   ```

## Requisitos

- Java 11 (JDK)
- Apache Maven 3.6+
- ONOS 2.7.0
