FROM openjdk:25-jdk-alpine

WORKDIR /app

COPY target/chefkoch-scraper-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]