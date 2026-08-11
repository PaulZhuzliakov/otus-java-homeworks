# hw-01-java-agent

Java-агент, считающий количество входов в методы. Методы и длительность сбора задаются в `agent-config.xml`, результаты пишутся в `profiler-result.txt`.

Метод в конфиге задаётся как `класс.метод` или `класс.метод(типы,аргументов)` - сигнатура нужна, чтобы различать перегрузки (например, `demo.App.overloadedMethod(int)` и `demo.App.overloadedMethod(java.lang.String)` считаются отдельно). Методы из конфига попадают в результат, даже если ни разу не вызывались - с нулём.

```bash
mvn package
java -javaagent:target/hw-01-java-agent-1.0-SNAPSHOT.jar=agent-config.xml -cp target/classes demo.App
```

Тесты: `mvn verify`. Есть и интеграционный (`ProfilerAgentIT`) - поднимает демо в отдельной JVM с `-javaagent` и проверяет итоговый файл.
