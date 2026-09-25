# Profiling Agent

This Java agent instruments configured methods and counts how many times they
are called during a specified collection period. The results are written to a
CSV file when the monitored application exits.

## Requirements

- JDK 21 or later
- Maven 3

## Build

Run the following command from the root of the course repository:

```shell
mvn -f kislitsyn/pom.xml -pl profiling-agent -am clean package
```

The packaged agent, including its runtime dependencies, will be created at:

```text
kislitsyn/profiling-agent/target/profiling-agent-1.0-SNAPSHOT.jar
```

## Configuration

Create an XML configuration file, for example `profiling-config.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<profiling durationSeconds="60" outputFile="profiling-result.csv">
    <method
            className="org.example.Service"
            methodName="process"/>
    <method
            className="org.example.Repository"
            methodName="find"/>
</profiling>
```

Configuration attributes:

- `durationSeconds` — the period during which method calls are counted. The
  period starts when the first configured method is called.
- `outputFile` — the path of the CSV file to create. Missing parent
  directories are created automatically.
- `className` — the fully qualified name of the class to instrument.
- `methodName` — the name of the method to instrument.

More than one `<method>` element can be added to the configuration.

## Run

Pass the agent JAR and the path to the XML configuration to the JVM:

```shell
java \
  -javaagent:kislitsyn/profiling-agent/target/profiling-agent-1.0-SNAPSHOT.jar=profiling-config.xml \
  -jar application.jar
```

The general syntax is:

```text
-javaagent:<path-to-agent-jar>=<path-to-configuration>
```

Use absolute paths when the working directory of the monitored application is
different from the `java-expert` directory:

```shell
java \
  -javaagent:/path/to/java-expert/profiling-agent/target/profiling-agent-1.0-SNAPSHOT.jar=/path/to/profiling-config.xml \
  -jar /path/to/application.jar
```

## IntelliJ IDEA

1. Open **Run | Edit Configurations**.
2. Select the run configuration of the application to profile.
3. Choose **Modify options | Add VM options**.
4. Add the following VM option using absolute paths:

```text
-javaagent:/path/to/profiling-agent-1.0-SNAPSHOT.jar=/path/to/profiling-config.xml
```

The `-javaagent` option must be added to **VM options**, not to **Program
arguments**.

## Result

The result is written when the monitored JVM shuts down normally:

```csv
method,calls
org.example.Service#process,5
org.example.Repository#find,2
```

Every configured method is included in the file. A method that was not called
has a count of `0`.
