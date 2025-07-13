FROM gradle:8.12.0-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN gradle bootJar

FROM eclipse-temurin:21-jre
WORKDIR /workspace

ARG SERVICE_MODULE=discovery-server
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

COPY --from=builder /workspace/build/libs/${SERVICE_MODULE}-1.0.jar ${SERVICE_MODULE}.jar

EXPOSE 8761

CMD ["sh", "-c", "java $JAVA_OPTS -jar ${SERVICE_MODULE}.jar"]