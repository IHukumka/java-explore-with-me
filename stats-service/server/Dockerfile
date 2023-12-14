FROM amazoncorretto:11-alpine-jdk
COPY target/server-0.0.1-SNAPSHOT.jar stats-server.jar
ENTRYPOINT ["java","-jar","/stats-server.jar"]