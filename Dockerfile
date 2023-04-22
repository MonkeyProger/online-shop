FROM openjdk:8-alpine

ARG JAR_FILE=target/online-shop-0.0.1-SNAPSHOT.jar

WORKDIR /app

ADD $JAR_FILE online-shop-backend.jar

ENTRYPOINT ["java","-jar","online-shop-backend.jar"]