# Домашнее задание по JMeter

Нагрузка создаётся на сервис регистрации `POST /api/users`. В каталоге есть
два варианта одного теста:

- `jmeter-ui/CreateRequest.jmx` - план для JMeter UI;
- Java-код в `src/main/java` - запуск JMeter из исполняемого JAR.

`web-service-with-login` и `jmeter` подключены к общему `pom.xml` как отдельные Maven-модули.

## JMeter UI

Открыть план:

```bash
jmeter -t jmeter/jmeter-ui/CreateRequest.jmx
```

Параметры можно передать при запуске:

```bash
jmeter -Jrps=5 -Jduration=30 -Jthreads=10 \
  -Jhost=localhost -Jport=8080 \
  -JresultPath=jmeter/jmeter-results/gui-result.jtl \
  -t jmeter/jmeter-ui/CreateRequest.jmx
```

Значения по умолчанию: `5 RPS`, `30` секунд и `10` потоков.

## Запуск из JAR

Перед запуском должна быть задана переменная `JMETER_HOME` с путём к
установленному Apache JMeter.

Сначала собрать проект и запустить сервис:

```bash
mvn -pl :jmeter,:web-service-with-login -am clean package
java -jar jmeter/web-service-with-login/target/web-service-with-login-1.0-SNAPSHOT.jar
```

Затем в другом терминале запустить нагрузку:

```bash
export JMETER_HOME=/path/to/apache-jmeter-5.6.3
java -Dhost=localhost -Dport=8080 \
  -Drps=5 -Dduration=30 -Dthreads=10 \
  -DresultPath=jmeter/jmeter-results/result.jtl \
  -jar jmeter/target/jmeter-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Доступные параметры: `host`, `port`, `endPoint`, `rps`, `duration`, `threads` и
`resultPath`. `Constant Throughput Timer` переводит RPS в запросы в минуту, а
встроенный `CounterConfig` создаёт уникальный логин для каждого запроса.

Пример результата находится в `jmeter-results/result.jtl`.
