# Java Platform Module System

Заготовка Spring Boot-приложения для задания по Java Platform Module System. Исходный код скопирован из модуля `web-service-with-login`.

## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/pom.xml \
  -pl module-system \
  clean package
```

## Запуск

```bash
java -jar kislitsyn/module-system/target/module-system-1.0-SNAPSHOT.jar
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
