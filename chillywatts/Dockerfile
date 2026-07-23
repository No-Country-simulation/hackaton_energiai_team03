# Estágio 1: Compilação do código
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace/app

# Copia os arquivos do projeto para o estágio de build
COPY pom.xml .
COPY src src

# Compila o projeto ignorando os testes locais
RUN mvn clean package -DskipTests

# Estágio 2: Execução limpa
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

# Sequestra apenas o arquivo .jar compilado do estágio temporário
COPY --from=build /workspace/app/target/*.jar app.jar

# Abre a porta 8080 para o tráfego da internet
EXPOSE 8080

# Inicia o servidor Tomcat embutido do Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]