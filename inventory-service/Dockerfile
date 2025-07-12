FROM gradle:8.12.0-jdk21 AS builder
WORKDIR /workspace

ARG SERVICE_MODULE=inventory-service

COPY . .
RUN gradle :${SERVICE_MODULE}:bootJar

FROM eclipse-temurin:21-jre
WORKDIR /workspace

ARG SERVICE_MODULE=inventory-service

COPY --from=builder /workspace/${SERVICE_MODULE}/build/libs/${SERVICE_MODULE}-1.0.jar ${SERVICE_MODULE}.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8256

CMD ["sh", "-c", "java $JAVA_OPTS -jar ${SERVICE_MODULE}.jar"]