FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY target/project-x.jar project-x.jar
ENTRYPOINT ["java","-jar","/project-x.jar"]