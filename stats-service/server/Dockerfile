FROM amazoncorretto:11-alpine-jdk
COPY target/server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=ci","-jar","/app.jar"]