# Java Platform Module System

Учебное приложение для знакомства с Java Platform Module System (JPMS).

Проект создаётся поэтапно на основе приложения `web-service-with-login`.

## Шаг 1. Каркас модулей

`module-system` является Maven-агрегатором и содержит четыре дочерних модуля:

```text
module-system
├── api
├── service
├── provider
└── core
```

Каждый дочерний проект одновременно является:

- Maven-модулем со своим `pom.xml`;
- JPMS-модулем со своим `module-info.java`.

Текущий граф зависимостей:

```text
api → service → provider
              → core
```

Maven-зависимость предоставляет модулю необходимый JAR во время сборки. Директива `requires` в `module-info.java` разрешает читать соответствующий JPMS-модуль.

Например, модуль `service` содержит Maven-зависимость на `core` и одновременно объявляет:

```java
requires org.java.expert.modulesystem.core;
```

## Шаг 2. Модуль `core`

Модуль `core` содержит слой хранения из исходного приложения:

- `User` — JPA-сущность;
- `UserRepository` — Spring Data JPA-репозиторий;
- H2 — базу данных, подключаемую во время выполнения.

Как и в `web-service-with-login`, реализацию `UserRepository` во время запуска
создаёт Spring Data, а идентификатор пользователя генерирует база данных.

В `module-info.java` модуль объявляет используемые библиотеки и экспортирует
пакеты, которые понадобятся сервисному модулю:

```java
module org.java.expert.modulesystem.core {
    requires jakarta.persistence;
    requires spring.data.jpa;
    requires static lombok;

    exports org.java.expert.modulesystem.core.model;
    exports org.java.expert.modulesystem.core.repository;
}
```

`exports` предоставляет обычный доступ к публичным типам во время компиляции и
выполнения. Необходимость `opens` для доступа Hibernate через reflection
проверим после сборки запускаемого модуля `api`.

## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/module-system/pom.xml clean verify
```

## Следующий шаг

Реализовать модуль `provider`, который будет предоставлять данные модулю `service`.
