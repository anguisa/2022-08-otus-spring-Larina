FROM maven:3.8.6-openjdk-11-slim as build

ENV PROJECT_DIR=/opt/project

RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR

ADD ./pom.xml $PROJECT_DIR
RUN mvn dependency:resolve

ADD ./src/ $PROJECT_DIR/src
RUN mvn -B clean install

FROM bellsoft/liberica-openjdk-alpine:11

ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=build $PROJECT_DIR/target/hw17-spring-docker.jar $PROJECT_DIR/

EXPOSE 8080

CMD ["java", "-jar", "/opt/project/hw17-spring-docker.jar"]
