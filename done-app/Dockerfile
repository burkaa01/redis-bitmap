FROM openjdk:17-alpine
VOLUME /tmp
COPY build/libs/done-app-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]