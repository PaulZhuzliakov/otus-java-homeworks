# Сравнение G1 и ZGC

## Цель работы

1. Модифицировать [исходный пример OTUS](https://github.com/OtusTeam/Java-Advanced/blob/master/gc-test/src/main/java/com/otus/java/advanced/GcTest.java) так чтобы приложение:
- запускалось с кучей размером 2 ГБ;
- не падало и выделяло 1 ГБ т.e половину кучи;
2. Запустить с GC1 и ZGC;
3. Разобрать вывод и описать различие в работе сборщика мусора;
4. Анализ должен содержать вывод о том, какой сборщик мусора отработал лучше и почему предпочтителен.

### Изменения исходного примера

Исходный пример бесконечно добавляет массивы в статический список. Объекты остаются достижимыми, сборщик мусора не может их освободить, поэтому приложение завершается с `OutOfMemoryError`.

В измененном примере, я буду аллоцировать ровно половину от размера heap.

Размер heap задается через:
- `-Xms2g` — начальный размер кучи 2 ГБ;
- `-Xmx2g` — максимальный размер кучи 2 ГБ;

А непосредственно для аллоцирования буду выполнять:
```java
long maxHeapBytes = Runtime.getRuntime().maxMemory();
long targetAllocationBytes = maxHeapBytes / 2;
```
Код аллоцирования:
```java
private static long allocate(long targetBytes) {
long allocatedBytes = 0;
    while (allocatedBytes < targetBytes) {
        int allocationSize = (int) Math.min(CHUNK_SIZE_BYTES, targetBytes - allocatedBytes);
        ALLOCATIONS.add(new byte[allocationSize]);
        allocatedBytes += allocationSize;
    }
    return allocatedBytes;
    }
```

## Сборка

Из корня репозитория выполнить:

```bash
mvn -f kislitsyn/gc-and-zgc-discovery/pom.xml clean package
```

## Параметры запуска

- `-Xms2g` — начальный размер кучи 2 ГБ;
- `-Xmx2g` — максимальный размер кучи 2 ГБ;
- `-Xlog:gc*` — подробная информация о работе GC;

Различаться будет только флаг выбора GC.

### G1

```bash
java -Xms2g -Xmx2g \
  -XX:+UseG1GC \
  '-Xlog:gc*:file=kislitsyn/gc-and-zgc-discovery/target/g1-gc.log:time,uptime,level,tags' \
  -cp kislitsyn/gc-and-zgc-discovery/target/classes \
  com.otus.java.advanced.GcTest
```

### ZGC

```bash
java -Xms2g -Xmx2g \
  -XX:+UseZGC \
  -XX:+ZGenerational \
  '-Xlog:gc*:file=kislitsyn/gc-and-zgc-discovery/target/generational-zgc.log:time,uptime,level,tags' \
  -cp kislitsyn/gc-and-zgc-discovery/target/classes \
  com.otus.java.advanced.GcTest
```

## Результат запуска с G1

Лог

```text

```

За время работы:

## Результат запуска с ZGC

Лог

```text

```

За время работы:

## Сравнение

## Вывод
