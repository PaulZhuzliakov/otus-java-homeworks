# GraalVM Native Image

Spring Boot-приложение для знакомства с AOT компиляцией GraalVM, сборкой native executable.

## Цель работы

- собрать приложение в Native Image;
- собрать и запустить тест в native executable;

## Окружение

| Параметр | Значение |
|---|---|
| Компьютер | MacBook Pro (`Mac15,6`) |
| Процессор | Apple M3 Pro |
| Оперативная память | 36 GB |
| Spring Boot | 4.1.0 |
| Native Build Tools | 1.1.14 |
| GraalVM для native-тестов | GraalVM Community 25.4.4.1.1 (JDK 25.0.4.1.1) |
| Builder | `paketobuildpacks/builder-noble-java-tiny:latest` |

## Выбор инструмента

Для сборки контейнера используется [Cloud Native Buildpacks](https://docs.spring.io/spring-boot/how-to/native-image/developing-your-first-application.html#howto.native-image.developing-your-first-application.buildpacks).
Интеграция с Buildpacks уже входит в `spring-boot-maven-plugin`
В POM настроены два Maven-профиля:

- `native` — AOT-обработка приложения и создание Docker image через Buildpacks;
- `nativeTest` — AOT-обработка тестов и их локальная компиляция через GraalVM.

Используемые версии заданы в свойствах POM:

```xml
<properties>
    <spring-boot.version>4.1.0</spring-boot.version>
    <native-maven-plugin.version>1.1.14</native-maven-plugin.version>
</properties>
```

## Сборка Native Image через Buildpacks

```bash
mvn -f kislitsyn/pom.xml \
  -pl native-image \
  -Pnative \
  spring-boot:build-image
```

Сборка завершилась успешно и заняла 3 минуты 2 секунды:

```text
[INFO] Successfully built image 'docker.io/library/native-image:1.0-SNAPSHOT'
[INFO] BUILD SUCCESS
[INFO] Total time:  03:02 min
```

Результатом команды является Linux OCI image в локальном хранилище Docker.

## Запуск в Docker

```bash
docker run --rm \
  --name native-image-app \
  -p 8080:8080 \
  native-image:1.0-SNAPSHOT
```

Приложение успешно запустилось:

```text
Started WebServiceWithLoginApplication in 0.137 seconds (process running for 0.148)
```

Проверить регистрацию пользователя можно запросом:

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"login":"alice","password":"secret"}'
```

## Сравнение с JVM

JVM-версия была собрана без native-профиля:

```bash
mvn -f kislitsyn/pom.xml \
  -pl native-image \
  clean package
```

Запуск JAR-файла:

```bash
java -jar kislitsyn/native-image/target/native-image-1.0-SNAPSHOT.jar
```

Результаты одного запуска на одном компьютере:

| Режим | Время из сообщения `Started` | Относительное время |
|---|---:|---:|
| Native Image | 0.137 с | 1× |
| JVM | 2.302 с | 16,8× |

Расчёт:

```text
2.302 / 0.137 ≈ 16.8
```

В данном запуске JVM потребовалось примерно в 16,8 раза больше времени.

## Native-тесты

Для native-тестов GraalVM установлена локально, поскольку Buildpacks собирает приложение в контейнер, но не запускает `native-maven-plugin:test`.

Для проверки был выбран тест Spring-контекста:

```bash
mvn -f kislitsyn/pom.xml \
  -pl native-image \
  -PnativeTest \
  -Dtest=WebServiceWithLoginApplicationTest \
  clean test
```

Результат:

```text
Test run finished after 142 ms
[         1 tests found           ]
[         1 tests started         ]
[         1 tests successful      ]
[         0 tests failed          ]

[INFO] BUILD SUCCESS
[INFO] Total time:  02:21 min
```

## Выводы

- Native Image запустился значительно быстрее JVM: 0.137 секунды против 2.302 секунды в выполненном измерении.
- Ускорение запуска достигается ценой более продолжительной сборки: image собирался 3 минуты 2 секунды, native-тест — 2 минуты 21 секунду.
