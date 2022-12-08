FROM openjdk:18-jdk-alpine
MAINTAINER aiothwacom
COPY target/modbussim-0.0.1-SNAPSHOT.jar modbussim-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/modbussim-0.0.1-SNAPSHOT.jar"]
