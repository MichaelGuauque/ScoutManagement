# Usa una imagen base ligera de Java
FROM eclipse-temurin:21-jdk-alpine

# Crea un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo .jar al contenedor
COPY ScoutManagement-0.0.1-SNAPSHOT.jar app.jar

# Expón el puerto que tu app usa
EXPOSE 8080

# Comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar", "server-address=0.0.0.0"]