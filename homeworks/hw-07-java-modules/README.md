# hw-07-java-modules

Мультимодульное приложение на Java Module System: регистрация клиентов.
Сервис по сути из hw-05, но без спринга - заворачивать спринговые jar'ы в модули
не стал, на module path проще пустить голый jdk.httpserver.
4 Maven-модуля, у каждого свой `module-info.java`:

- `api` (`hw07.api`): точка входа, запускает сервер, тут же контроллер
- `registration` (`hw07.registration`): service, бизнес-логика регистрации
- `loyalty` (`hw07.loyalty`): provider, имитация внешнего провайдера данных (выдаёт карту лояльности)
- `core` (`hw07.core`): имитация базы, `InMemoryClientRepository` проставляет id и кладёт в коллекцию

Цепочка: контроллер (api) зовёт `RegistrationService` (registration), тот получает карту
в `LoyaltyService` (loyalty) и сохраняет клиента через `ClientRepository` (core).
Наружу модули отдают только интерфейсы, реализации спрятаны в неэкспортируемых пакетах.

## Сборка и запуск

```bash
mvn clean package
mkdir -p mods && cp */target/*.jar mods/
java -p mods -m hw07.api
```

## Эндпоинты

Зарегистрировать клиента (проходит вся цепочка модулей):

```bash
curl -X POST localhost:8080/api/clients -d '{"name":"Иван","email":"ivan@yandex.ru"}'
```

```json
{"id":1,"name":"Иван","email":"ivan@yandex.ru","cardNumber":"LOY-...","discountPercent":10}
```

Проверить, что сохранилось в "базе":

```bash
curl localhost:8080/api/clients
```
