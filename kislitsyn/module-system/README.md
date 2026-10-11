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

## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/module-system/pom.xml clean verify
```

На первом шаге модули содержат только дескрипторы `module-info.java`. Исполняемое Spring Boot-приложение и бизнес-код будут добавлены на следующих шагах.

## Следующий шаг

Реализовать модуль `core`: модель пользователя и in-memory-хранилище, которое присваивает идентификатор и сохраняет данные в коллекцию.
