FROM gradle:8.12.0-jdk21 AS builder

ARG SERVICE_MODULE=discovery-server

WORKDIR /${SERVICE_MODULE}
COPY . .
RUN gradle bootJar --no-daemon

RUN echo ls

FROM eclipse-temurin:21-jre

ARG SERVICE_MODULE=discovery-server

WORKDIR /${SERVICE_MODULE}

ENV SERVICE_MODULE=${SERVICE_MODULE}
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

COPY --from=builder /${SERVICE_MODULE}/build/libs/${SERVICE_MODULE}-1.0.jar ${SERVICE_MODULE}.jar

EXPOSE 8761

CMD ["sh", "-c", "java $JAVA_OPTS -jar ${SERVICE_MODULE}.jar"]