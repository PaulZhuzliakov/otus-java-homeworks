# hw-15: запуск spring boot сервиса в docker

сервис регистрации из прошлых дз с новым эндпоинтом GET /hello,
который возвращает строку "Hello world!". собираю jar, образ на основе
eclipse-temurin:21, запускаю контейнер портом 80 наружу.

## как собрать и запустить

сборка jar:

    cd homeworks/hw-15-docker
    mvn package

сборка образа (точка - путь к папке с Dockerfile):

    docker build -t hw-15-docker:1.0 .

запуск контейнера - приложение слушает 8080, наружу 80:

    docker run -d --name hw-15 -p 80:8080 hw-15-docker:1.0

## Dockerfile

    FROM eclipse-temurin:21
    RUN mkdir /opt/app
    COPY ./target/hw-15-docker*.jar /opt/app/app.jar
    EXPOSE 8080
    CMD ["java", "-jar", "/opt/app/app.jar"]

за основу беру образ eclipse-temurin:21 с jdk 21. собранный jar кладется в /opt/app/app.jar
отдельным слоем поверх основы - при пересборке после правок кода перекачивается только он,
основа уже лежит локально. при старте контейнера jar запускается через java -jar.

## проверка

    curl http://localhost/hello
    Hello world!

    curl -X POST 'http://localhost/register?login=paul&email=paul@test.ru&age=30'
    registered, id: 1

    curl http://localhost/users
    ["id: 1, email: paul@test.ru"]

    docker ps --filter name=hw-15
    NAMES   IMAGE              STATUS         PORTS
    hw-15   hw-15-docker:1.0   Up 4 minutes   0.0.0.0:80->8080/tcp, [::]:80->8080/tcp

все запросы идут на порт 80, докер перенаправляет их на 8080 приложения.
