# hw-01-java-agent

Java-агент, считающий количество входов в методы. Методы и длительность сбора задаются в `agent-config.xml`, результаты пишутся в `profiler-result.txt`.

```bash
mvn package
java -javaagent:target/hw-01-java-agent-1.0-SNAPSHOT.jar=agent-config.xml -cp target/classes demo.App
```
