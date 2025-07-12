FROM gradle:8.12.0-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN gradle bootJar

FROM eclipse-temurin:21-jre
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/gateway-service-1.0.jar gateway-service.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8255

CMD ["sh", "-c", "java $JAVA_OPTS -jar gateway-service.jar"]