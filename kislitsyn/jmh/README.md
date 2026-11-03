# Исследование производительности с помощью JMH

## Цель

Реализовать и выполнить тесты по замеру производительности с помощью Java Microbenchmark Harness (JMH), получить сравнительные результаты и проанализировать особенности различных режимов измерения.

## План работы

1. benchmark для сравнения алгоритмов хеширования MD5, SHA-256 и SHA-512 с различным количеством последовательных раундов хеширования.
2. benchmark общего сервиса регистрации клиента без учёта времени инициализации Spring и HTTP-транспорта.
3. Запустить и проанализировать три benchmark из примеров JMH (https://hg.openjdk.org/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/):
   - `JMHSample_10_ConstantFold`;
   - `JMHSample_21_ConsumeCPU`;
   - `JMHSample_15_Asymmetric`.

## Конфигурация стенда

В проекте используется JMH 1.37. Benchmark приложения запускаются из fat JAR.

### Запуск

Запуск отдельных benchmark-ов:

```bash
java -jar target/jmh-1.0-SNAPSHOT-benchmarks.jar PasswordHashBenchmark
java -jar target/jmh-1.0-SNAPSHOT-benchmarks.jar RegistrationServiceBenchmark
java -jar target/jmh-1.0-SNAPSHOT-benchmarks.jar JMHSample_10_ConstantFold
java -jar target/jmh-1.0-SNAPSHOT-benchmarks.jar JMHSample_21_ConsumeCPU
java -jar target/jmh-1.0-SNAPSHOT-benchmarks.jar JMHSample_15_Asymmetric
```

## 1. Сравнение алгоритмов хеширования

Исходный код: [`PasswordHashBenchmark`](src/main/java/org/java/expert/webservicewithlogin/benchmark/PasswordHashBenchmark.java).

В тесте сравниваются MD5, SHA-256 и SHA-512. 

Алгоритм выбирается с помощью `@Param`. 

Второй параметр задаёт количество последовательных раундов хеширования внутри одного вызова benchmark:

```java
@Param({"MD5", "SHA-256", "SHA-512"})
public String algorithm;

@Param({"1", "1000", "10000"})
public int hashIterations;
```

### Результаты

```text
Benchmark                                   (algorithm)  (iterations)    Mode     Cnt      Score      Error   Units
PasswordHashBenchmark.hashPassword                  MD5             1   thrpt       5      8.504 ±    0.642  ops/us
PasswordHashBenchmark.hashPassword                  MD5          1000   thrpt       5      0.008 ±    0.001  ops/us
PasswordHashBenchmark.hashPassword                  MD5         10000   thrpt       5      0.001 ±    0.001  ops/us
PasswordHashBenchmark.hashPassword              SHA-256             1   thrpt       5     19.486 ±    1.937  ops/us
PasswordHashBenchmark.hashPassword              SHA-256          1000   thrpt       5      0.019 ±    0.002  ops/us
PasswordHashBenchmark.hashPassword              SHA-256         10000   thrpt       5      0.002 ±    0.001  ops/us
PasswordHashBenchmark.hashPassword              SHA-512             1   thrpt       5      7.418 ±    0.444  ops/us
PasswordHashBenchmark.hashPassword              SHA-512          1000   thrpt       5      0.007 ±    0.001  ops/us
PasswordHashBenchmark.hashPassword              SHA-512         10000   thrpt       5      0.001 ±    0.001  ops/us
PasswordHashBenchmark.hashPassword                  MD5             1    avgt       5      0.122 ±    0.022   us/op
PasswordHashBenchmark.hashPassword                  MD5          1000    avgt       5    124.804 ±   31.373   us/op
PasswordHashBenchmark.hashPassword                  MD5         10000    avgt       5   1231.781 ±  176.927   us/op
PasswordHashBenchmark.hashPassword              SHA-256             1    avgt       5      0.050 ±    0.002   us/op
PasswordHashBenchmark.hashPassword              SHA-256          1000    avgt       5     48.774 ±    0.694   us/op
PasswordHashBenchmark.hashPassword              SHA-256         10000    avgt       5    488.839 ±   11.509   us/op
PasswordHashBenchmark.hashPassword              SHA-512             1    avgt       5      0.134 ±    0.001   us/op
PasswordHashBenchmark.hashPassword              SHA-512          1000    avgt       5    134.800 ±    3.256   us/op
PasswordHashBenchmark.hashPassword              SHA-512         10000    avgt       5   1324.942 ±   38.362   us/op
PasswordHashBenchmark.hashPassword                  MD5             1  sample  168804      0.117 ±    0.002   us/op
PasswordHashBenchmark.hashPassword:p0.00            MD5             1  sample              0.041              us/op
PasswordHashBenchmark.hashPassword:p0.50            MD5             1  sample              0.125              us/op
PasswordHashBenchmark.hashPassword:p0.90            MD5             1  sample              0.125              us/op
PasswordHashBenchmark.hashPassword:p0.95            MD5             1  sample              0.125              us/op
PasswordHashBenchmark.hashPassword:p0.99            MD5             1  sample              0.125              us/op
PasswordHashBenchmark.hashPassword:p0.999           MD5             1  sample              0.292              us/op
PasswordHashBenchmark.hashPassword:p0.9999          MD5             1  sample              6.846              us/op
PasswordHashBenchmark.hashPassword:p1.00            MD5             1  sample             54.144              us/op
PasswordHashBenchmark.hashPassword                  MD5          1000  sample   41993    119.514 ±    0.228   us/op
PasswordHashBenchmark.hashPassword:p0.00            MD5          1000  sample            118.272              us/op
PasswordHashBenchmark.hashPassword:p0.50            MD5          1000  sample            118.528              us/op
PasswordHashBenchmark.hashPassword:p0.90            MD5          1000  sample            121.344              us/op
PasswordHashBenchmark.hashPassword:p0.95            MD5          1000  sample            122.624              us/op
PasswordHashBenchmark.hashPassword:p0.99            MD5          1000  sample            130.696              us/op
PasswordHashBenchmark.hashPassword:p0.999           MD5          1000  sample            173.321              us/op
PasswordHashBenchmark.hashPassword:p0.9999          MD5          1000  sample           1183.291              us/op
PasswordHashBenchmark.hashPassword:p1.00            MD5          1000  sample           1671.168              us/op
PasswordHashBenchmark.hashPassword                  MD5         10000  sample    4100   1223.771 ±    8.450   us/op
PasswordHashBenchmark.hashPassword:p0.00            MD5         10000  sample           1179.648              us/op
PasswordHashBenchmark.hashPassword:p0.50            MD5         10000  sample           1187.840              us/op
PasswordHashBenchmark.hashPassword:p0.90            MD5         10000  sample           1296.384              us/op
PasswordHashBenchmark.hashPassword:p0.95            MD5         10000  sample           1368.064              us/op
PasswordHashBenchmark.hashPassword:p0.99            MD5         10000  sample           1587.180              us/op
PasswordHashBenchmark.hashPassword:p0.999           MD5         10000  sample           3274.195              us/op
PasswordHashBenchmark.hashPassword:p0.9999          MD5         10000  sample           8585.216              us/op
PasswordHashBenchmark.hashPassword:p1.00            MD5         10000  sample           8585.216              us/op
PasswordHashBenchmark.hashPassword              SHA-256             1  sample  161698      0.066 ±    0.001   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-256             1  sample                ≈ 0              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-256             1  sample              0.083              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-256             1  sample              0.084              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-256             1  sample              0.084              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-256             1  sample              0.084              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-256             1  sample              0.416              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-256             1  sample              8.869              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-256             1  sample             23.936              us/op
PasswordHashBenchmark.hashPassword              SHA-256          1000  sample   96894     51.754 ±    0.226   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-256          1000  sample             48.832              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-256          1000  sample             50.944              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-256          1000  sample             52.992              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-256          1000  sample             54.208              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-256          1000  sample             67.072              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-256          1000  sample             93.238              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-256          1000  sample           1356.272              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-256          1000  sample           4071.424              us/op
PasswordHashBenchmark.hashPassword              SHA-256         10000  sample   10113    495.936 ±    2.197   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-256         10000  sample            481.280              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-256         10000  sample            488.448              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-256         10000  sample            502.784              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-256         10000  sample            509.952              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-256         10000  sample            615.137              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-256         10000  sample           1806.463              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-256         10000  sample           3030.840              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-256         10000  sample           3035.136              us/op
PasswordHashBenchmark.hashPassword              SHA-512             1  sample  139064      0.161 ±    0.002   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-512             1  sample              0.041              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-512             1  sample              0.166              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-512             1  sample              0.167              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-512             1  sample              0.167              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-512             1  sample              0.209              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-512             1  sample              1.329              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-512             1  sample             12.003              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-512             1  sample             27.520              us/op
PasswordHashBenchmark.hashPassword              SHA-512          1000  sample   36616    137.045 ±    1.294   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-512          1000  sample            129.280              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-512          1000  sample            132.352              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-512          1000  sample            139.776              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-512          1000  sample            149.030              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-512          1000  sample            210.688              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-512          1000  sample            537.818              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-512          1000  sample           2303.239              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-512          1000  sample          11108.352              us/op
PasswordHashBenchmark.hashPassword              SHA-512         10000  sample    3691   1358.470 ±   15.417   us/op
PasswordHashBenchmark.hashPassword:p0.00        SHA-512         10000  sample           1300.480              us/op
PasswordHashBenchmark.hashPassword:p0.50        SHA-512         10000  sample           1339.392              us/op
PasswordHashBenchmark.hashPassword:p0.90        SHA-512         10000  sample           1372.160              us/op
PasswordHashBenchmark.hashPassword:p0.95        SHA-512         10000  sample           1415.987              us/op
PasswordHashBenchmark.hashPassword:p0.99        SHA-512         10000  sample           1632.420              us/op
PasswordHashBenchmark.hashPassword:p0.999       SHA-512         10000  sample           3895.689              us/op
PasswordHashBenchmark.hashPassword:p0.9999      SHA-512         10000  sample          17137.664              us/op
PasswordHashBenchmark.hashPassword:p1.00        SHA-512         10000  sample          17137.664              us/op
PasswordHashBenchmark.hashPassword                  MD5             1      ss       5     21.492 ±   35.921   us/op
PasswordHashBenchmark.hashPassword                  MD5          1000      ss       5    776.458 ±  330.286   us/op
PasswordHashBenchmark.hashPassword                  MD5         10000      ss       5   1729.942 ± 2744.526   us/op
PasswordHashBenchmark.hashPassword              SHA-256             1      ss       5     32.208 ±   12.259   us/op
PasswordHashBenchmark.hashPassword              SHA-256          1000      ss       5   1575.233 ±  559.471   us/op
PasswordHashBenchmark.hashPassword              SHA-256         10000      ss       5   2393.184 ± 8544.615   us/op
PasswordHashBenchmark.hashPassword              SHA-512             1      ss       5     72.034 ±  196.304   us/op
PasswordHashBenchmark.hashPassword              SHA-512          1000      ss       5   1892.325 ±  453.163   us/op
PasswordHashBenchmark.hashPassword              SHA-512         10000      ss       5   1813.016 ±  450.127   us/op
```

### Анализ

Время выполнения растёт практически линейно с количеством раундов.

| Алгоритм | Время одного раунда |
|---|---:|
| MD5 | ≈ 123 ns |
| SHA-256 | ≈ 49 ns |
| SHA-512 | ≈ 133 ns |

На исследуемом стенде SHA-256 оказался приблизительно в 2.5 раза быстрее MD5 и в 2.7 раза быстрее SHA-512. 

## 2. Benchmark сервиса регистрации

Подготовка Spring, Hibernate и схемы БД выполняется до начала измерения и не входит в результат.
HTTP-транспорт, контроллер, JSON-сериализация и валидация запроса в измерение не входят. 

### Результаты

```text
Benchmark                                            Mode  Cnt    Score   Error   Units
RegistrationServiceBenchmark.registerUser           thrpt    5    0.010 ± 0.001  ops/ms
RegistrationServiceBenchmark.registerUser            avgt    5   94.919 ± 4.965   ms/op
RegistrationServiceBenchmark.registerUser          sample   55   93.357 ± 1.544   ms/op
RegistrationServiceBenchmark.registerUser:p0.00    sample        91.095           ms/op
RegistrationServiceBenchmark.registerUser:p0.50    sample        92.537           ms/op
RegistrationServiceBenchmark.registerUser:p0.90    sample        95.892           ms/op
RegistrationServiceBenchmark.registerUser:p0.95    sample       101.424           ms/op
RegistrationServiceBenchmark.registerUser:p0.99    sample       109.707           ms/op
RegistrationServiceBenchmark.registerUser:p0.999   sample       109.707           ms/op
RegistrationServiceBenchmark.registerUser:p0.9999  sample       109.707           ms/op
RegistrationServiceBenchmark.registerUser:p1.00    sample       109.707           ms/op
RegistrationServiceBenchmark.registerUser              ss    5   92.930 ± 0.633   ms/op
```

### Анализ

Средняя регистрация пользователя занимает около 95 ms. 
Throughput `0.010 ops/ms` приблизительно 10 регистрациям в секунду на 1 потоке.

Медиана составляет 92.537 ms. 

Результаты четырёх режимов согласуются между собой. 
Основной вклад в задержку вносит BCrypt, который намеренно выполняется медленно для повышения стоимости перебора паролей. 

## 3. Анализ примеров JMH

Примеры взяты из официального набора [`jmh-samples`](https://github.com/openjdk/jmh/tree/master/jmh-samples/src/main/java/org/openjdk/jmh/samples).

### 3.1. JMHSample_10_ConstantFold

Тест демонстрирует оптимизацию constant folding, при которой JIT-компилятор заранее вычисляет выражение, составленное из известных констант.

```text
Benchmark                                 Mode  Cnt  Score    Error  Units
JMHSample_10_ConstantFold.baseline        avgt   25  0.281 ±  0.004  ns/op
JMHSample_10_ConstantFold.measureRight    avgt   25  4.119 ±  0.008  ns/op
JMHSample_10_ConstantFold.measureWrong_1  avgt   25  0.277 ±  0.001  ns/op
JMHSample_10_ConstantFold.measureWrong_2  avgt   25  0.287 ±  0.024  ns/op
```

`measureWrong_1` вычисляет `Math.log(Math.PI)`, а `measureWrong_2` использует `final`-поле. В обоих случаях JVM заранее вычисляет результат, поэтому время практически совпадает с пустым `baseline`. 
В `measureRight` используется изменяемое поле, и `Math.log()` действительно выполняется во время benchmark.

Реальное вычисление занимает 4.119 ns/op — приблизительно в 14.7 раза больше baseline.

### 3.2. JMHSample_21_ConsumeCPU

Тест демонстрирует `Blackhole.consumeCPU(tokens)`, который создаёт синтетическую CPU-нагрузку без `sleep`, I/O и полезного результата, удаляемого компилятором.

```text
Benchmark                             Mode  Cnt     Score     Error  Units
JMHSample_21_ConsumeCPU.consume_0000  avgt   25     1.860 ±   0.029  ns/op
JMHSample_21_ConsumeCPU.consume_0001  avgt   25     2.326 ±   0.020  ns/op
JMHSample_21_ConsumeCPU.consume_0002  avgt   25     3.114 ±   0.043  ns/op
JMHSample_21_ConsumeCPU.consume_0004  avgt   25     4.256 ±   0.035  ns/op
JMHSample_21_ConsumeCPU.consume_0008  avgt   25     6.638 ±   0.048  ns/op
JMHSample_21_ConsumeCPU.consume_0016  avgt   25    16.407 ±   0.155  ns/op
JMHSample_21_ConsumeCPU.consume_0032  avgt   25    42.316 ±   0.348  ns/op
JMHSample_21_ConsumeCPU.consume_0064  avgt   25   110.760 ±   0.447  ns/op
JMHSample_21_ConsumeCPU.consume_0128  avgt   25   262.041 ±   1.037  ns/op
JMHSample_21_ConsumeCPU.consume_0256  avgt   25   556.556 ±   1.565  ns/op
JMHSample_21_ConsumeCPU.consume_0512  avgt   25  1166.085 ±  23.261  ns/op
JMHSample_21_ConsumeCPU.consume_1024  avgt   25  1612.627 ± 115.885  ns/op
```

`consume_0000` показывает базовую стоимость вызова — 1.860 ns. 

При увеличении количества tokens среднее время операции возрастает. 

В диапазоне 128–512 удвоение tokens приводит примерно к удвоению времени:

```text
128 tokens 262.041 ns
256 tokens 556.556 ns
512 tokens 1166.085 ns
```

### 3.3. JMHSample_15_Asymmetric

Тест демонстрирует групповую асимметричную нагрузку. Четыре потока одновременно работают с одним `AtomicInteger`: 3 потока выполняют `incrementAndGet()`, 1 поток — `get()`.

```text
Benchmark                      Mode  Cnt   Score   Error  Units
JMHSample_15_Asymmetric.g      avgt   25  19.986 ± 0.860  ns/op
JMHSample_15_Asymmetric.g:get  avgt   25   2.232 ± 0.104  ns/op
JMHSample_15_Asymmetric.g:inc  avgt   25  25.904 ± 1.131  ns/op
```

Чтение общего `AtomicInteger` занимает 2.232 ns/op.
Атомарное увеличение занимает 25.904 ns/op — приблизительно x11.6 раза больше из-за read-modify-write операции и конкуренции потоков.

## Вывод

В ходе работы были реализованы benchmark алгоритмов хеширования и полного сервиса регистрации, а также исследованы три официальных примера JMH.

Основные результаты:

- SHA-256 показал наименьшую вычислительную стоимость среди MD5, SHA-256 и SHA-512 — около 49 ns на один раунд хеширования.
- Полная регистрация пользователя с BCrypt и сохранением в H2 занимает в среднем около 95 ms, что соответствует приблизительно 10 операциям в секунду на одном потоке.
- `JMHSample_10_ConstantFold` показал, что при использовании констант JIT может заранее вычислить результат, и benchmark будет измерять возврат готового значения.
- `JMHSample_21_ConsumeCPU` показал создание контролируемой синтетической CPU-нагрузки.
- `JMHSample_15_Asymmetric` показал групповое измерение чтения и конкурентного изменения общего состояния.
