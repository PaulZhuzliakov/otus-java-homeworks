# Профилирование с помощью visualvm

## Цель

Реализовать тестовый пример, содержащий проблемы, которые потом продемонстрировать в visualvm

## Ход работы

Обычная регистрация сохраняет пользователя одним вызовом `UserRepository.save`.
Spring-профиль `extra-queries` (было реализовано в дз по JFR) дополнительно вызывает `UserRepository.existsByLogin` 100 раз

### Запуск сервиса с `extra-queries` профилем

```bash
java -jar jvisualvm/target/jvisualvm-web-service-with-login-1.0-SNAPSHOT.jar \
  --spring.profiles.active=extra-queries \
  --server.port=18083
```

В журнале должно появиться сообщение:

```text
Intentional profiling problem enabled: 100 redundant database queries per registration
```

### Запуск нагрузки JMeter

```bash
jmeter -n \
  -t jvisualvm/jmeter/registration-load.jmx \
  -Jhost=localhost \
  -Jport=18083 \
  -Jthreads=10 \
  -Jduration=120 \
  -Jrps=10 \
  -JresultPath="$(pwd)/jvisualvm/results/extra-queries.jtl" \
  -j "$(pwd)/jvisualvm/results/extra-queries-jmeter.log"
```

### Flame graph async-profiler

[Открыть flame graph](profiling/flamegraph.html).

## Анализ

- `BCrypt.crypt_raw` занимает ~`52.24%` CPU. Это ожидаемая стоимость хеширования пароля.
- Ветка `RedundantQuerySimulator.executeRedundantDatabaseQueries -> existsByLogin` занимает около `2.52%` всех CPU-сэмплов.
- Необходимый `SimpleJpaRepository.save` занимает около `0.68%`.

Следовательно, заведомо бесполезная ветка чтения потребляет примерно в 3.7 раза больше CPU, чем единственная полезная операция сохранения.
