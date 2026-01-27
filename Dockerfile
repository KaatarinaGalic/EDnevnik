FROM maven:3.8.7-openjdk-18 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests


FROM eclipse-temurin:18-jre-alpine
COPY --from=build /home/app/target/studenti-ocjene-0.0.1-SNAPSHOT.jar /app/studenti-ocjene.jar
RUN mkdir /app/uploads
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/studenti-ocjene.jar"]
