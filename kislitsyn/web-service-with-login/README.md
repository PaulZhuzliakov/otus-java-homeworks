# Web Service with Login

Spring Boot-приложение с простым REST-сервисом регистрации пользователей.


## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/web-service-with-login/pom.xml clean package
```

## Запуск

```bash
java -jar kislitsyn/web-service-with-login/target/web-service-with-login-1.0-SNAPSHOT.jar
```

## Регистрация пользователя

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"login":"alice","password":"secret"}'
```

Успешный ответ имеет статус `201 Created`:

```json
{
  "id": 1,
  "login": "alice"
}
```

Пустые значения возвращают `400 Bad Request`, повторная регистрация того же логина — `409 Conflict`.
