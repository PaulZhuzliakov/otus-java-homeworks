# Настройка Serial GC

## Цель работы

Запустить тестовое приложение с Serial GC, найти в логах полные сборки мусора, а затем подобрать значения `NewRatio` и `SurvivorRatio`, при которых приложение завершит работу без Full GC.

## Сборка

Из корня репозитория курса выполнить:

```bash
mvn -f kislitsyn/serial-gc-profiling/pom.xml clean package
```

---

## 1. Запуск с исходными параметрами

Команда запуска:

```bash
java -XX:+UseSerialGC \
  -Xms128m \
  -Xmx128m \
  '-Xlog:gc*::time' \
  -cp kislitsyn/serial-gc-profiling/target/classes \
  org.java.expert.Main 2>&1 | grep --line-buffered 'Pause Full'
```

Использовался Serial GC с фиксированным размером heap 128 МБ. Размеры поколений дополнительно не настраивались.

### Лог выполнения

```text
boleque@Vlads-MacBook-Pro ~/src/GC-test % java -XX:+UseSerialGC \
  -Xms128m \
  -Xmx128m \
  '-Xlog:gc*::time' \
  -cp kislitsyn/serial-gc-profiling/target/classes \
  org.java.expert.Main 2>&1 | grep --line-buffered 'Pause Full'
[2026-07-30T11:41:27.961+0300] GC(14) Pause Full (Allocation Failure)
[2026-07-30T11:41:28.000+0300] GC(14) Pause Full (Allocation Failure) 109M->13M(123M) 38.807ms
[2026-07-30T11:41:29.868+0300] GC(25) Pause Full (Allocation Failure)
[2026-07-30T11:41:29.902+0300] GC(25) Pause Full (Allocation Failure) 106M->7M(123M) 33.441ms
[2026-07-30T11:41:32.254+0300] GC(39) Pause Full (Allocation Failure)
[2026-07-30T11:41:32.292+0300] GC(39) Pause Full (Allocation Failure) 113M->9M(123M) 37.591ms
[2026-07-30T11:41:34.322+0300] GC(52) Pause Full (Allocation Failure)
[2026-07-30T11:41:34.353+0300] GC(52) Pause Full (Allocation Failure) 115M->11M(123M) 31.566ms
[2026-07-30T11:41:36.449+0300] GC(65) Pause Full (Allocation Failure)
[2026-07-30T11:41:36.489+0300] GC(65) Pause Full (Allocation Failure) 108M->19M(123M) 39.513ms
[2026-07-30T11:41:38.376+0300] GC(77) Pause Full (Allocation Failure)
[2026-07-30T11:41:38.410+0300] GC(77) Pause Full (Allocation Failure) 109M->5M(123M) 34.357ms
[2026-07-30T11:41:40.437+0300] GC(90) Pause Full (Allocation Failure)
[2026-07-30T11:41:40.470+0300] GC(90) Pause Full (Allocation Failure) 109M->12M(123M) 33.089ms
[2026-07-30T11:41:42.243+0300] GC(101) Pause Full (Allocation Failure)
[2026-07-30T11:41:42.267+0300] GC(101) Pause Full (Allocation Failure) 106M->4M(123M) 23.452ms
[2026-07-30T11:41:44.723+0300] GC(115) Pause Full (Allocation Failure)
[2026-07-30T11:41:44.757+0300] GC(115) Pause Full (Allocation Failure) 110M->4M(123M) 33.515ms
[2026-07-30T11:41:47.091+0300] GC(129) Pause Full (Allocation Failure)
[2026-07-30T11:41:47.129+0300] GC(129) Pause Full (Allocation Failure) 112M->6M(123M) 37.311ms
[2026-07-30T11:41:49.224+0300] GC(142) Pause Full (Allocation Failure)
[2026-07-30T11:41:49.255+0300] GC(142) Pause Full (Allocation Failure) 116M->9M(123M) 30.886ms
[2026-07-30T11:41:51.616+0300] GC(156) Pause Full (Allocation Failure)
[2026-07-30T11:41:51.642+0300] GC(156) Pause Full (Allocation Failure) 119M->4M(123M) 26.425ms
[2026-07-30T11:41:54.095+0300] GC(170) Pause Full (Allocation Failure)
[2026-07-30T11:41:54.131+0300] GC(170) Pause Full (Allocation Failure) 111M->4M(123M) 36.137ms
```

## 2. Первая попытка настройки памяти

Для отмены Full GC были изначально добавлены параметры:

```text
-XX:NewRatio=1
-XX:SurvivorRatio=4
```

Команда запуска:

```bash
java -XX:+UseSerialGC \
  -XX:NewRatio=1 \
  -XX:SurvivorRatio=4 \
  -Xms128m \
  -Xmx128m \
  '-Xlog:gc*::time' \
  -cp kislitsyn/serial-gc-profiling/target/classes \
  org.java.expert.Main 2>&1 | grep --line-buffered 'Pause Full'
```

### Распределение памяти

`NewRatio=1`:
```text
Young Generation ≈ 64 МБ
Old Generation   ≈ 64 МБ
```

`SurvivorRatio=4`:

```text
Eden       ≈ 42,7 МБ
Survivor 0 ≈ 10,7 МБ
Survivor 1 ≈ 10,7 МБ
```

### Лог выполнения

```text
boleque@Vlads-MacBook-Pro ~/src/GC-test % java -XX:+UseSerialGC -XX:NewRatio=1 -XX:SurvivorRatio=4 -Xms128m -Xmx128m '-Xlog:gc*::time' -cp kislitsyn/serial-gc-profiling/target/classes org.java.expert.Main 2>&1 | grep --line-buffered 'Pause Full'
[2026-07-30T11:51:14.951+0300] GC(35) Pause Full (Allocation Failure)
[2026-07-30T11:51:14.992+0300] GC(35) Pause Full (Allocation Failure) 110M->5M(117M) 41.651ms
[2026-07-30T11:51:21.264+0300] GC(64) Pause Full (Allocation Failure)
[2026-07-30T11:51:21.304+0300] GC(64) Pause Full (Allocation Failure) 109M->8M(117M) 39.273ms
[2026-07-30T11:51:27.570+0300] GC(93) Pause Full (Allocation Failure)
[2026-07-30T11:51:27.608+0300] GC(93) Pause Full (Allocation Failure) 111M->10M(117M) 38.284ms
[2026-07-30T11:51:33.911+0300] GC(122) Pause Full (Allocation Failure)
[2026-07-30T11:51:33.948+0300] GC(122) Pause Full (Allocation Failure) 109M->12M(117M) 36.683ms
```

### Результат

После изменения параметров произошло **4 Full GC вместо 13**.

---

## 3. Успешная настройка памяти

В следующем запуске значение `SurvivorRatio` было уменьшено с 4 до 2:

```text
-XX:NewRatio=1
-XX:SurvivorRatio=2
```

Команда запуска:

```bash
java -XX:+UseSerialGC \
  -XX:NewRatio=1 \
  -XX:SurvivorRatio=2 \
  -Xms128m \
  -Xmx128m \
  '-Xlog:gc*::time' \
  -cp kislitsyn/serial-gc-profiling/target/classes \
  org.java.expert.Main 2>&1 | grep --line-buffered 'Pause Full'
```

### Распределение памяти

```text
Eden       ≈ 32 МБ
Survivor 0 ≈ 16 МБ
Survivor 1 ≈ 16 МБ
Old        ≈ 64 МБ
```

### Результат запуска

```text
Количество Full GC: 0
```
