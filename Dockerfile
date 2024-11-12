FROM openjdk:17-oracle
WORKDIR /app
COPY ./build/libs/Employee-Management-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]