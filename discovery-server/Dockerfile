FROM gradle:8.12.0-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/discovery-server-1.0.jar discovery-server.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8761

CMD ["sh", "-c", "java $JAVA_OPTS -jar discovery-server.jar"]