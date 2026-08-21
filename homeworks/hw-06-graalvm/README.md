# hw-06-graalvm

Сервис регистрации из hw-05 (поиск утечки памяти), собранный в GraalVM
Native Image. Задание - сравнить время старта на нативе и на обычной джаве

## Приложение

Взял сервис регистрации из hw-05 (rest + h2 + spring data jpa. Сборка на
gradle, как в задании:

```
./gradlew bootJar
java -jar build/libs/hw-06-graalvm-1.0-SNAPSHOT.jar
```

## Шаг 1 - плагин native image

В build.gradle добавил плагин `org.graalvm.buildtools.native` - с
ним появляются задачи nativeCompile и nativeTestCompile. GraalVM поставил
через sdkman (`21.0.12-graal`)

## Шаг 2 - сборка native image

```
JAVA_HOME=~/.sdkman/candidates/java/21.0.12-graal ./gradlew nativeCompile
```

Собралось за 4 минуты (из них сам native-image - 3,5), бинарник 164 мб лежит в
build/native/nativeCompile

## Шаг 3 - запуск и сравнение

Время старта меряю по строке Started Application в логе

На JVM (liberica 21.0.3):
```
java -jar build/libs/hw-06-graalvm-1.0-SNAPSHOT.jar
```
`Started Application in 5.102 seconds`

На нативе:
```
./build/native/nativeCompile/hw-06-graalvm
```
`Started Application in 0.203 seconds`

Натив стартует в 25 раз быстрее.

## Шаг 4 - результаты

Нативу не надо поднимать JVM, грузить классы и греть JIT - всё уже
скомпилировано в бинарник, поэтому старт почти мгновенный

Железо: Intel Xeon E5-2678 v3 (24 потока), 32 гб ОЗУ, ubuntu 22.04

## Шаг 5 - тест в native

Тест простой: регистрирует пользователя и проверяет, что вернулся id > 0
На джаве (`./gradlew test`) зелёный. Прогон в нативе:

```
JAVA_HOME=~/.sdkman/candidates/java/21.0.12-graal ./gradlew nativeTest
```

`BUILD SUCCESSFUL`, тест выполнился внутри нативного бинарника - в логе
`Starting AOT-processed RegistrationServiceTest`. Контекст теста: 0,168 с
в нативе против 3,877 с на джаве

## Docker

```
docker build -t hw-06-graalvm .
docker run -d -p 8080:8080 hw-06-graalvm
```

В образе debian:bookworm-slim - нативному бинарнику джава не нужна, хватает
glibc. Старт в контейнере: `Started Application in 0.144 seconds`,
регистрация отвечает `registered, id: 1`
