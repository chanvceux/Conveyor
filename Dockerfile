#./mvnw package && java -jar target/Conveyor-0.0.1-SNAPSHOT.jar

FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=target/conveyor-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]

#docker build -t Conveyor:0.0.1 .