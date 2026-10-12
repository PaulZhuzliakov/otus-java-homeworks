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

## Шаг 3. Модуль `provider`

Модуль `provider` предоставляет сервисному слою хеш пароля. Он переносит
зависимость от BCrypt из `service` за собственный публичный контракт:

```java
public interface PasswordHashProvider {
    String hash(String password);
}
```

`PasswordHashProviders.bcrypt()` создаёт реализацию на основе исходного
`BCryptPasswordEncoder`. Пакет с реализацией не экспортируется, поэтому другие
модули работают только с `PasswordHashProvider`.

Дескриптор модуля содержит:

```java
module org.java.expert.modulesystem.provider {
    requires org.apache.commons.logging;
    requires spring.core;
    requires spring.security.crypto;

    exports org.java.expert.modulesystem.provider.password;
}
```

`org.apache.commons.logging` и `spring.core` указаны явно, поскольку
`BCryptPasswordEncoder` использует их во время выполнения, но
`spring-security-crypto` не объявляет эти Maven-зависимости транзитивно.

## Шаг 4. Модуль `service`

Модуль `service` координирует регистрацию пользователя:

1. получает хеш пароля у `PasswordHashProvider`;
2. создаёт JPA-сущность `User`;
3. сохраняет её через `UserRepository`;
4. преобразует нарушение уникальности login в `DuplicateLoginException`.

Таким образом, `service` зависит сразу от двух наших модулей:

```java
module org.java.expert.modulesystem.service {
    requires org.java.expert.modulesystem.core;
    requires org.java.expert.modulesystem.provider;
    requires spring.context;
    requires spring.tx;
    requires spring.web;

    exports org.java.expert.modulesystem.service;
}
```

Unit-тест использует mock-реализации `PasswordHashProvider` и `UserRepository`,
поэтому проверяет логику сервиса без запуска Spring и H2. Mockito подключается
к тестовой JVM как Java agent: современные JDK больше не гарантируют возможность
динамического self-attach во время уже запущенного теста.

## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/module-system/pom.xml clean verify
```

## Следующий шаг

Собрать запускаемый Spring Boot-модуль `api`: перенести контроллер, DTO,
конфигурацию и `application.properties`.
