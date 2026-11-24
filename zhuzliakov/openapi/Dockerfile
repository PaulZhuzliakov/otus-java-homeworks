FROM eclipse-temurin:21

RUN mkdir /opt/app
COPY ./target/hw-18-openapi*.jar /opt/app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/app/app.jar"]
