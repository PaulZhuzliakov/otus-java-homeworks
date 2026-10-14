# Домашнее задание по теме docker
1. Добавлен эндпоинт api/hello, который возвращает строку "Hello World"
2. Добавлен Dockerfile

## Запуск docker контейнера
1. Необходимо запустить mvn clean install. В результате будет создан fat jar target/memory-dump-0.0.1-SNAPSHOT.jar
2. docker build -t myapp .
Убедиться, что image создан, команда docker images. Пример вывода
   IMAGE          ID             DISK USAGE   CONTENT SIZE   EXTRA
   myapp:latest   3ef0cf367ad4        514MB          156MB   
3. Для запуска контейнера
   docker run -d --name myapp -p 80:8080 myapp
Контейнер стартует в режиме detach. Внешний порт 80 мапится на внутренний порт контейнера 8080

Чтобы достучаться до нового эндпоинта, теперь нужно сделать запрос
http://localhost/api/hello

