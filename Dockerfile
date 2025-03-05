# Etapa 1: Construir el proyecto
FROM gradle:jdk17 AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Configura JAVA_HOME explícitamente
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copia todos los archivos del proyecto al contenedor
COPY . .

# Ejecuta Gradle para construir el proyecto
RUN ./gradlew clean build

# Etapa 2: Crear la imagen de ejecución
FROM openjdk:17-jdk

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Configura JAVA_HOME explícitamente
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copia el archivo JAR generado en la etapa de construcción
COPY --from=build /app/build/libs/aplicacion-1.0.jar app.jar

# Expone el puerto que usa la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
