FROM krmp-d2hub-idock.9rum.cc/goorm/gradle:7.3.1-jdk17

WORKDIR /app

COPY . .

ENTRYPOINT ["java", "-jar", "app.jar"]
