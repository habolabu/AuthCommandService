FROM gradle:jdk17-alpine
WORKDIR /auth-command-service

COPY src /auth-command-service/src/
COPY build.gradle .
COPY lombok.config .

RUN gradle clean build -x test

ENTRYPOINT ["java", "-jar", "./build/libs/auth-command-service-1.0.0.jar"]