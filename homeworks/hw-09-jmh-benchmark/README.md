# hw-09-jmh-benchmark

Бенчмарки на JMH для сервиса регистрации: сравниваю хеширование пароля
тремя алгоритмами и выбираю для сервиса наименее тяжёлый вариант.

## Сервис

Регистрация из прошлых ДЗ. Спринг выкинул. Пароль в базу не пишется:
PasswordHasher хеширует его 1000 раз подряд (хеш от хеша), в таблицу users
уходит login + password_hash.

## Запуск

```
mvn clean package
java -jar target/benchmarks.jar HashBenchmark > jmh-run.log          # хеширование пароля
java -jar target/benchmarks.jar RegistrationBenchmark > jmh-reg.log  # регистрация: хеш + запись в h2
```

## Хеширование

HashBenchmark - хеширование пароля тремя алгоритмами.

| алгоритм | ops/s     |
|----------|-----------|
| MD5      | 6289 ±392 |
| SHA-256  | 3267 ±177 |
| SHA-512  | 2525 ±193 |

Остальные метрики (AverageTime, SampleTime, SingleShotTime) - в jmh-run.log.

## Выбор алгоритма

По заданию нужен наименее тяжёлый - по замерам это MD5, его и выбрал
для RegistrationService.

## Вся регистрация

RegistrationBenchmark - register целиком: хеш + запись в h2.

| что меряем      | ops/s     | на операцию |
|-----------------|-----------|-------------|
| хеш MD5         | 6289 ±392 | 159 мкс     |
| вся регистрация | 5904 ±449 | 169 мкс     |

На запись в h2 остаётся 10 мкс из 169 - почти всё время регистрации
съедает хеширование.

## Семплы JMH

Взял три официальных примера из jmh-samples, код не менял. Гонял так:

```
java -jar target/benchmarks.jar JMHSample_01_HelloWorld -f 1 > jmh-samples-01.log
java -jar target/benchmarks.jar JMHSample_08_DeadCode   -f 1 > jmh-samples-08.log
java -jar target/benchmarks.jar JMHSample_11_Loops      -f 1 > jmh-samples-11.log
```

01 HelloWorld - пустой метод, 1.9 млрд ops/s. Это сколько стоит сам вызов.

08 DeadCode:

| метод        | ns/op  |
|--------------|--------|
| baseline     | 0.531  |
| measureWrong | 0.572  |
| measureRight | 16.738 |

measureWrong вызывает то же вычисление, что measureRight, но без return -
и стоит как пустой baseline. Результат не используется, и компилятор
выбрасывает само вычисление.

11 Loops - сложение в цикле, время делится на число повторов:

| N      | ns/op |
|--------|-------|
| 1      | 0.924 |
| 10     | 0.111 |
| 100000 | 0.012 |

С ростом N цена сложения падает аж до 0.012 нс, при этом measureRight из
того же лога даёт 0.87 нс. x + y всегда одинаковый, и компилятор
упрощает цикл, выбрасывая почти все повторения.
