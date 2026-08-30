# Базовое REST приложение для работы с пользователями
Предоставляет функционал CRUD операций (на данный момент реализовано create и read)
1. Создание пользователя
HOST: localhost:8080
endpoint: api/users
method: POST
body: {"login": "user1","password": "123456789","algorithm": "SHA-256"}
Варианты algorithm, которые поддерживаются на данный момент:
 - MD5
 - SHA-256
 - SHA-512
2. Получение информации по пользователю
HOST: localhost:8080
endpoint: api/users
method: POST
params: id={id}, например api/users?id=1

## Benchmark тесты
1. Тест на сервис хеширования org.example.memorydump.jmh.HashServiceBenchmark
Запуск: запустить через idea org.example.memorydump.jmh.BenchmarkRunner, предварительно раскоментировать include(HashServiceBenchmark.class.getSimpleName()) 
   Benchmark                        Mode  Cnt     Score    Error  Units
   HashServiceBenchmark.testMD5     avgt  100   430,744 ±  5,263  ns/op
   HashServiceBenchmark.testSHA256  avgt  100   597,159 ±  9,767  ns/op
   HashServiceBenchmark.testSHA512  avgt  100  1149,308 ± 19,156  ns/op

По показателю AverageTime самый лучший результат показал алгоритм хеширования MD5

2. Тесты rest сервиса создания пользователя org.example.memorydump.jmh.UserControllerBenchmark
Запуск: запустить через idea org.example.memorydump.jmh.BenchmarkRunner, предварительно раскоментировать include(UserControllerBenchmark.class.getSimpleName())


Benchmark                                     Mode   Cnt    Score   Error   Units
UserControllerBenchmark.testMD5              thrpt    10    0,885 ± 0,602  ops/ms
UserControllerBenchmark.testSHA256           thrpt    10    1,110 ± 0,561  ops/ms
UserControllerBenchmark.testSHA512           thrpt    10    0,922 ± 0,494  ops/ms
UserControllerBenchmark.testMD5               avgt    10    1,312 ± 0,639   ms/op
UserControllerBenchmark.testSHA256            avgt    10    1,129 ± 0,546   ms/op
UserControllerBenchmark.testSHA512            avgt    10    1,396 ± 1,069   ms/op
UserControllerBenchmark.testMD5             sample  7364    1,357 ± 0,095   ms/op
UserControllerBenchmark.testMD5:p0.00       sample          0,251           ms/op
UserControllerBenchmark.testMD5:p0.50       sample          0,918           ms/op
UserControllerBenchmark.testMD5:p0.90       sample          2,419           ms/op
UserControllerBenchmark.testMD5:p0.95       sample          3,747           ms/op
UserControllerBenchmark.testMD5:p0.99       sample          6,723           ms/op
UserControllerBenchmark.testMD5:p0.999      sample         36,033           ms/op
UserControllerBenchmark.testMD5:p0.9999     sample         91,750           ms/op
UserControllerBenchmark.testMD5:p1.00       sample         91,750           ms/op
UserControllerBenchmark.testSHA256          sample  4922    2,039 ± 0,219   ms/op
UserControllerBenchmark.testSHA256:p0.00    sample          0,368           ms/op
UserControllerBenchmark.testSHA256:p0.50    sample          1,333           ms/op
UserControllerBenchmark.testSHA256:p0.90    sample          3,194           ms/op
UserControllerBenchmark.testSHA256:p0.95    sample          4,772           ms/op
UserControllerBenchmark.testSHA256:p0.99    sample         10,938           ms/op
UserControllerBenchmark.testSHA256:p0.999   sample         75,942           ms/op
UserControllerBenchmark.testSHA256:p0.9999  sample        111,018           ms/op
UserControllerBenchmark.testSHA256:p1.00    sample        111,018           ms/op
UserControllerBenchmark.testSHA512          sample  5198    1,924 ± 0,212   ms/op
UserControllerBenchmark.testSHA512:p0.00    sample          0,388           ms/op
UserControllerBenchmark.testSHA512:p0.50    sample          1,233           ms/op
UserControllerBenchmark.testSHA512:p0.90    sample          3,105           ms/op
UserControllerBenchmark.testSHA512:p0.95    sample          4,252           ms/op
UserControllerBenchmark.testSHA512:p0.99    sample          8,669           ms/op
UserControllerBenchmark.testSHA512:p0.999   sample         96,512           ms/op
UserControllerBenchmark.testSHA512:p0.9999  sample        109,445           ms/op
UserControllerBenchmark.testSHA512:p1.00    sample        109,445           ms/op
UserControllerBenchmark.testMD5                 ss    10    5,183 ± 0,689   ms/op
UserControllerBenchmark.testSHA256              ss    10    4,519 ± 0,887   ms/op
UserControllerBenchmark.testSHA512              ss    10    5,199 ± 1,162   ms/op