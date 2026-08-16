# Базовое REST приложение для работы с пользователями
## Структура проекта
Проект расположен в модуле aivazian/module-system. 

Каждый модуль использует module-info.java для конфигурации доступов к пакетам

1. user-core. Базовый модуль предоставляет функционал по хранению объектов в in memory хранилище (в текущей реализации ConcurrentHashMap)
У модуля отсутствуют внешние зависимости. Модуль только экспортирует свои пакеты(api, impl)
2. user-provider. Модуль предоставляет функционал по сохранению данных пользователя в InMemoryDataStore.
Зависит от модуля user.core, lombok и экспортирует свои пакеты(api, impl, entity)
3. user-service. Модуль предоставляет бизнес функционал по работе с пользователем. 
Зависит от модуля user.provider, lombok и экспортирует свои пакеты(api, impl, model, exception)
4. user-spring-boot-starter. Модуль предназначен для создания бинов из предыдущих модулей и представляет из себя стандартный стартер.
Зависит от user.core, user.provider, user.service, а также необходимых spring модулей.
Для того чтобы данный модуль можно было подключать и использовать в spring boot приложениях (рефлексия и другие механизмы spring) модуль открвает свои пакеты с помощью команды opens
5. user-api. Модуль представляет из себя spring boot приложение. Зависит от user.service и подлючает user.starter, а также необходимых spring модулей.
   Для того чтобы данный модуль можно было запустить (рефлексия и другие механизмы spring) модуль открывает свои пакеты с помощью команды opens


## Предоставляет функционал CRUD операций (на данный момент реализовано create и read)
1. Создание пользователя
HOST: localhost:8080
endpoint: api/users
method: POST
body: {"name": "name1","lastname": "lastname1"}
2. Получение информации по пользователю
HOST: localhost:8080
endpoint: api/users
method: POST
params: id={id}, например api/users?id=1

## Сборка и запуск
1. сборка из пакета aivazian/module-system mvn clean install
2. запуск aivazian/module-system/user-api/src/main/java/ru/otus/user/api/UserApiApplication.java

