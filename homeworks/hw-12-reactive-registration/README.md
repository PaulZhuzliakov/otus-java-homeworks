# hw-12-reactive-registration

Сервис регистрации, переписанный на spring webflux: spring-data-r2dbc
(r2dbc-h2) вместо jpa, ddl переехал в schema.sql.

## Запуск

```
mvn clean package
java -jar target/hw-12-reactive-registration-1.0-SNAPSHOT.jar
```

в логе `Netty started on port 8080` - поднялся netty, а не tomcat.

## Проверка

Регистрация (email необязательный - чтобы в /users/emails было что фильтровать):

```
curl -X POST "localhost:8080/register?login=ivan&password=123&email=ivan@mail.ru"  -> registered, id: 1
curl -X POST "localhost:8080/register?login=olga&password=123"                     -> registered, id: 2

curl localhost:8080/users         -> [{"id":1,"login":"ivan","email":"ivan@mail.ru"},{"id":2,"login":"olga","email":null}]
curl localhost:8080/users/names   -> ["ivan","olga"]
curl localhost:8080/users/emails  -> ["ivan@mail.ru"]
```

пароля в /users нет - у User для него нет геттера.
