FROM eclipse-temurin:21

RUN mkdir /opt/app
COPY ./server/target/hw-19-grpc*.jar /opt/app/app.jar

EXPOSE 50051

CMD ["java", "-jar", "/opt/app/app.jar"]
