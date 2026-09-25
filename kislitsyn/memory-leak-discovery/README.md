# Поиск утечки памяти в приложении

## Цель
На примере Spring Boot-приложение с простым REST-сервисом регистрации пользователей воспроизвести утечку памяти.
Проанализировать heap dump приложения и найти причины утечки.
Исправть утечку отдельным коммитом

## Ход работы
Идея для утечки была дана на лекции. 
При использовании библиотеки сбора метрик Micrometer, был создан Counter для посчета 
количеста вызовов метода контроллера. 
При каждом вызове счетчика создается уникальный trace_id

```java
UUID traceToken = UUID.randomUUID();
```
trace_id был помещен в качестве значения тега, что приводило к тому, что каждый новый UUID
создавал новый Counter, а не увеличивал существующий при правильном использовании либы.
Например:
```
demo.memory.leak{reg.attempts.trace.id="uuid-1"}
demo.memory.leak{reg.attempts.trace.id="uuid-2"}
demo.memory.leak{reg.attempts.trace.id="uuid-3"}
```

Чтобы дополнительно ускорить ООM на каждый вызов создавалось 10 уникальных метрик.

MeterRegistry является singleton bean spring я живет все время работы приложения.
Поэтому GC не может осовободить созданные Counter, их Meter, теги и т.д

### Получение heap dump

Приложение было запущено c инструкцией, позволяющей собирать дамп хипа перед падением
Размер heap был выбран 64Mb, чтобы дать приложении создать достаточно объектов в heap

(С 32Mb OOM тоже воспроизводилось, но это причина была в самом spring, на обработку запросов
оставалось примерно 6 Mb, поэтому OutOfMemoryError возникал по причине маленького heap)

```
spring-boot:run -Dspring-boot.run.jvmArguments="-Xms32m -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=target/memory-leak64mb.hprof"
```

При позникновении исключения создался heap dump file memory-leak64mb.hprof:
```
Exception in thread "SpringApplicationShutdownHook" java.lang.OutOfMemoryError: Java heap space
```

### Анализ heap dump c Eclipse Memory Analyzer

Зафиксируем что получили именно OutOfMemoryError по heap

```
java.lang.OutOfMemoryError: Java heap space
```

#### Гистограмма 
Сделал сортировку по Retained heap
На первом месте находится ConcurrentHashMap и массив ConcurrentHashMap$Node[]
MicrometerCollector находится на 4м месте

```
Class Name                                                                        | Objects | Shallow Heap | Retained Heap
---------------------------------------------------------------------------------------------------------------------------
java.util.concurrent.ConcurrentHashMap                                            |     807 |       51,648 | >= 23,800,912
java.util.concurrent.ConcurrentHashMap$Node[]                                     |     372 |    1,424,800 | >= 23,759,952
java.util.concurrent.ConcurrentHashMap$Node                                       | 157,429 |    5,037,728 | >= 21,870,848
io.micrometer.prometheusmetrics.MicrometerCollector                               |      69 |        1,656 | >= 16,539,704
io.micrometer.prometheusmetrics.PrometheusMeterRegistry$$Lambda+0x000003c001a9fd38|  54,003 |    2,160,120 | >= 14,256,376
---------------------------------------------------------------------------------------------------------------------------

```
#### Dominator tree
Сортировка по Retained Heap.
Получили статистику крупнейших доминаторов.
Наибольший размер heap 27.76% удерживает MicrometerCollector, а 11.39% связанный с ним PrometheusMeterRegistry

```
Class Name                                                                        | Shallow Heap | Retained Heap | Percentage
------------------------------------------------------------------------------------------------------------------------------
io.micrometer.prometheusmetrics.MicrometerCollector @ 0x47d5461f8                 |           24 |    16,508,392 |     27.76%
io.micrometer.prometheusmetrics.PrometheusMeterRegistry @ 0x47c7352e8             |          104 |     6,770,848 |     11.39%
org.springframework.beans.factory.support.DefaultListableBeanFactory @ 0x47dea64c0|          288 |     4,159,880 |      7.00%
jdk.internal.loader.ClassLoaders$AppClassLoader @ 0x47f9970a0                     |           96 |     2,063,704 |      3.47%
------------------------------------------------------------------------------------------------------------------------------
```
Если посмотреть какими объектами владеет доминатор MicrometerCollector.
Очевидно что отображаемые на гистограмме ConcurrentHashMap и ConcurrentHashMap$Node связаны с MicrometerCollector
```
Class Name                                                                   | Shallow Heap | Retained Heap | Percentage
-------------------------------------------------------------------------------------------------------------------------
io.micrometer.prometheusmetrics.MicrometerCollector @ 0x47d5461f8            |           24 |    16,508,392 |     27.76%
'- children java.util.concurrent.ConcurrentHashMap @ 0x47db03d88             |           64 |    16,508,368 |     27.76%
'- table java.util.concurrent.ConcurrentHashMap$Node[131072] @ 0x47fe00000|      524,304 |    16,508,304 |     27.76%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47dc11af0           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cd2c6a0           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47e493958           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47e69a9c0           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47e86cb08           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47e9f56d8           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47ee12770           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f44fa10           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f452070           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f46b7f0           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f46e170           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f473d30           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47f477650           |           32 |         1,480 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cecc010           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47dc13050           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cee7fa8           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47dcceab0           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47c9c6680           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47de40550           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47de48028           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47de7abc0           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cddb6f0           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cf2b858           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47d353d20           |           32 |         1,184 |      0.00%
|- java.util.concurrent.ConcurrentHashMap$Node @ 0x47cf42388           |           32 |         1,184 |      0.00%
'- Total: 25 of 44,339 entries; 44,314 more                            |              |               |
-------------------------------------------------------------------------------------------------------------------------
```

#### Leak Suspects
Статистика объектов утечек уверенно указавает на io.micrometer.prometheusmetrics.MicrometerCollector и io.micrometer.prometheusmetrics.PrometheusMeterRegistry
```
Suspect 1
io.micrometer.prometheusmetrics.MicrometerCollector

Suspect 2
io.micrometer.prometheusmetrics.PrometheusMeterRegistry
```

#### Вывод
Причиной утечки является создание новой метрики Micrometer для каждого уникального UUID в теге. 
Эти метрики накапливаются в PrometheusMeterRegistry и остаются достижимыми от GC Roots, поэтому не удаляются сборщиком мусора.
Heap dump подтверждает рост количества Meter$Id, PrometheusCounter и элементов MicrometerCollector.children. 
Для устранения утечки необходимо отказаться от уникальных значений в тегах и использовать один счётчик с постоянным набором тегов.

## Сборка

Из корня репозитория:

```bash
mvn -f kislitsyn/web-service-with-login/pom.xml clean package
```

## Запуск

```bash
java -jar kislitsyn/web-service-with-login/target/web-service-with-login-1.0-SNAPSHOT.jar
```

## Регистрация пользователя

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"login":"foo","password":"secret"}'
```

Успешный ответ имеет статус `201 Created`:

```json
{
  "id": 1,
  "login": "alice"
}
```

Пустые значения возвращают `400 Bad Request`, повторная регистрация того же логина — `409 Conflict`.
