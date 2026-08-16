# File Cache Emulator

An interactive Spring Shell application for loading the contents of text files
into a cache and reading them from the cache afterward.

The emulator uses the `cache-core` module from the same homework project.

## Requirements

- JDK 21
- A terminal with interactive input support
- Text files encoded in UTF-8

You do not need to install Maven separately because the project includes Maven
Wrapper.

## Build

Run the commands from the root of the course repository.

On macOS and Linux:

```bash
./kislitsyn/cache/emulator-for-file-cache/mvnw \
  -f kislitsyn/cache/pom.xml \
  package
```

On Windows:

```bat
kislitsyn\cache\emulator-for-file-cache\mvnw.cmd -f kislitsyn\cache\pom.xml package
```

The command builds both `cache-core` and the emulator.

## Run

After building the project, run the executable Spring Boot JAR:

```bash
java -jar kislitsyn/cache/emulator-for-file-cache/target/emulator-for-file-cache-0.0.1-SNAPSHOT.jar
```

After the application starts, the following prompt appears:

```text
$>
```

Spring Shell requires an interactive terminal. If the application immediately
finishes with `exit code 0` when started with the Run button in IntelliJ IDEA,
start it from the built-in Terminal tool window instead.

## Working with the Cache

The workflow consists of three steps:

1. Configure the directory containing the files.
2. Load a file from disk into the cache.
3. Read the previously loaded file from the cache.

### 1. Configure the Directory

```text
directory --path /absolute/path/to/directory
```

Example:

```text
directory --path /Users/user/files
```

Short form:

```text
directory -d /Users/user/files
```

The directory can only be configured once during an application session. The
path must exist and must point to a directory.

### 2. Load a File into the Cache

```text
load --file Names.txt
```

Short form:

```text
load -f Names.txt
```

The file must be located directly inside the configured directory:

```text
/Users/user/files/Names.txt
```

The command reads the file from disk using UTF-8 and stores its contents in the
cache under the `Names.txt` key.

### 3. Read a File from the Cache

```text
read --file Names.txt
```

Short form:

```text
read -f Names.txt
```

The `read` command does not access the file system. It only returns content
that is currently present in the cache.

## Complete Example

```text
$> directory --path /Users/user/files
Cache directory: /Users/user/files

$> load --file Names.txt
Loaded 'Names.txt' into cache (17 characters)

$> read --file Names.txt
Alice
Bob
Charlie
```

To stop the application:

```text
exit
```

To list all available commands:

```text
help
```

To display help for a specific command:

```text
help directory
help load
help read
```

## Common Errors

If the directory has not been configured:

```text
Cache directory is not configured
```

If the file has not been loaded or its value has been removed by the garbage
collector:

```text
File is not cached: Names.txt
```

Absolute file names, nested paths, and attempts to leave the configured
directory are rejected:

```text
load --file ../secret.txt
load --file subdirectory/Names.txt
```

## Cache Capacity

The maximum number of cache entries is configured in
`src/main/resources/application.properties`:

```properties
file-cache.capacity=50
```

When the capacity is exceeded, the least recently used entry is evicted.

## Run Tests

From the root of the course repository:

```bash
./kislitsyn/cache/emulator-for-file-cache/mvnw \
  -f kislitsyn/cache/pom.xml \
  test
```

This command runs the tests for both `cache-core` and the emulator.
