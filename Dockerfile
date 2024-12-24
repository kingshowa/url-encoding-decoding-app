FROM openjdk:8
ADD target/URL-encoding-decoding-app.jar url-encoding-decoding-app.jar
ENTRYPOINT ["java", "-jar","url-encoding-decoding-app.jar"]
EXPOSE 8080