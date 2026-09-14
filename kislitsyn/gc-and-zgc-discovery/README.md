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
[2026-09-15T00:32:47.966+0300][0.006s][info][gc,init] CardTable entry size: 512
[2026-09-15T00:32:47.966+0300][0.006s][info][gc     ] Using G1
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Version: 21.0.11+10-LTS (release)
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] CPUs: 11 total, 11 available
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Memory: 36864M
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Large Page Support: Disabled
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] NUMA Support: Disabled
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Compressed Oops: Enabled (Zero based)
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Heap Region Size: 1M
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Heap Min Capacity: 2G
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Heap Initial Capacity: 2G
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Heap Max Capacity: 2G
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Pre-touch: Disabled
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Parallel Workers: 9
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Concurrent Workers: 2
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Concurrent Refinement Workers: 9
[2026-09-15T00:32:47.969+0300][0.009s][info][gc,init] Periodic GC: Disabled
[2026-09-15T00:32:47.974+0300][0.013s][info][gc,metaspace] CDS archive(s) mapped at: [0x0000007000000000-0x0000007000c94000-0x0000007000c94000), size 13189120, SharedBaseAddress: 0x0000007000000000, ArchiveRelocationMode: 1.
[2026-09-15T00:32:47.974+0300][0.014s][info][gc,metaspace] Compressed class space mapped at: 0x0000007001000000-0x0000007041000000, reserved size: 1073741824
[2026-09-15T00:32:47.974+0300][0.014s][info][gc,metaspace] Narrow klass base: 0x0000007000000000, Narrow klass shift: 0, Narrow klass range: 0x100000000
[2026-09-15T00:32:48.056+0300][0.096s][info][gc,start    ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.056+0300][0.096s][info][gc,task     ] GC(0) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,phases   ] GC(0)   Pre Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,phases   ] GC(0)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,phases   ] GC(0)   Evacuate Collection Set: 11.16ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,phases   ] GC(0)   Post Evacuate Collection Set: 0.18ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,phases   ] GC(0)   Other: 0.33ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,heap     ] GC(0) Eden regions: 102->0(89)
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,heap     ] GC(0) Survivor regions: 0->13(13)
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,heap     ] GC(0) Old regions: 2->90
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,heap     ] GC(0) Humongous regions: 0->0
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,metaspace] GC(0) Metaspace: 318K(512K)->318K(512K) NonClass: 293K(384K)->293K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.068+0300][0.108s][info][gc          ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 102M->101M(2048M) 12.250ms
[2026-09-15T00:32:48.068+0300][0.108s][info][gc,cpu      ] GC(0) User=0.01s Sys=0.07s Real=0.01s
[2026-09-15T00:32:48.146+0300][0.185s][info][gc,start    ] GC(1) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.146+0300][0.185s][info][gc,task     ] GC(1) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,phases   ] GC(1)   Pre Evacuate Collection Set: 0.13ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,phases   ] GC(1)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,phases   ] GC(1)   Evacuate Collection Set: 11.50ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,phases   ] GC(1)   Post Evacuate Collection Set: 0.18ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,phases   ] GC(1)   Other: 0.05ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,heap     ] GC(1) Eden regions: 89->0(89)
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,heap     ] GC(1) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,heap     ] GC(1) Old regions: 90->179
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,heap     ] GC(1) Humongous regions: 0->0
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,metaspace] GC(1) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.158+0300][0.197s][info][gc          ] GC(1) Pause Young (Normal) (G1 Evacuation Pause) 190M->190M(2048M) 12.154ms
[2026-09-15T00:32:48.158+0300][0.197s][info][gc,cpu      ] GC(1) User=0.01s Sys=0.07s Real=0.01s
[2026-09-15T00:32:48.235+0300][0.275s][info][gc,start    ] GC(2) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.235+0300][0.275s][info][gc,task     ] GC(2) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,phases   ] GC(2)   Pre Evacuate Collection Set: 0.34ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,phases   ] GC(2)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,phases   ] GC(2)   Evacuate Collection Set: 12.04ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,phases   ] GC(2)   Post Evacuate Collection Set: 0.17ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,phases   ] GC(2)   Other: 0.07ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,heap     ] GC(2) Eden regions: 89->0(89)
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,heap     ] GC(2) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,heap     ] GC(2) Old regions: 179->268
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,heap     ] GC(2) Humongous regions: 0->0
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,metaspace] GC(2) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.248+0300][0.288s][info][gc          ] GC(2) Pause Young (Normal) (G1 Evacuation Pause) 279M->279M(2048M) 12.913ms
[2026-09-15T00:32:48.248+0300][0.288s][info][gc,cpu      ] GC(2) User=0.01s Sys=0.08s Real=0.01s
[2026-09-15T00:32:48.299+0300][0.338s][info][gc,start    ] GC(3) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.299+0300][0.339s][info][gc,task     ] GC(3) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,phases   ] GC(3)   Pre Evacuate Collection Set: 0.15ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,phases   ] GC(3)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,phases   ] GC(3)   Evacuate Collection Set: 13.90ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,phases   ] GC(3)   Post Evacuate Collection Set: 0.15ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,phases   ] GC(3)   Other: 0.05ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,heap     ] GC(3) Eden regions: 89->0(89)
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,heap     ] GC(3) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,heap     ] GC(3) Old regions: 268->358
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,heap     ] GC(3) Humongous regions: 0->0
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,metaspace] GC(3) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.313+0300][0.353s][info][gc          ] GC(3) Pause Young (Normal) (G1 Evacuation Pause) 368M->369M(2048M) 14.554ms
[2026-09-15T00:32:48.313+0300][0.353s][info][gc,cpu      ] GC(3) User=0.02s Sys=0.10s Real=0.02s
[2026-09-15T00:32:48.391+0300][0.431s][info][gc,start    ] GC(4) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.391+0300][0.431s][info][gc,task     ] GC(4) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,phases   ] GC(4)   Pre Evacuate Collection Set: 0.26ms
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,phases   ] GC(4)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,phases   ] GC(4)   Evacuate Collection Set: 13.80ms
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,phases   ] GC(4)   Post Evacuate Collection Set: 0.19ms
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,phases   ] GC(4)   Other: 0.08ms
[2026-09-15T00:32:48.406+0300][0.445s][info][gc,heap     ] GC(4) Eden regions: 89->0(89)
[2026-09-15T00:32:48.406+0300][0.446s][info][gc,heap     ] GC(4) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.406+0300][0.446s][info][gc,heap     ] GC(4) Old regions: 358->447
[2026-09-15T00:32:48.406+0300][0.446s][info][gc,heap     ] GC(4) Humongous regions: 0->0
[2026-09-15T00:32:48.406+0300][0.446s][info][gc,metaspace] GC(4) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.406+0300][0.446s][info][gc          ] GC(4) Pause Young (Normal) (G1 Evacuation Pause) 458M->458M(2048M) 14.725ms
[2026-09-15T00:32:48.406+0300][0.446s][info][gc,cpu      ] GC(4) User=0.02s Sys=0.05s Real=0.01s
[2026-09-15T00:32:48.459+0300][0.499s][info][gc,start    ] GC(5) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.459+0300][0.499s][info][gc,task     ] GC(5) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,phases   ] GC(5)   Pre Evacuate Collection Set: 0.37ms
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,phases   ] GC(5)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,phases   ] GC(5)   Evacuate Collection Set: 13.97ms
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,phases   ] GC(5)   Post Evacuate Collection Set: 0.20ms
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,phases   ] GC(5)   Other: 0.08ms
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,heap     ] GC(5) Eden regions: 89->0(89)
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,heap     ] GC(5) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,heap     ] GC(5) Old regions: 447->536
[2026-09-15T00:32:48.474+0300][0.513s][info][gc,heap     ] GC(5) Humongous regions: 0->0
[2026-09-15T00:32:48.474+0300][0.514s][info][gc,metaspace] GC(5) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.474+0300][0.514s][info][gc          ] GC(5) Pause Young (Normal) (G1 Evacuation Pause) 547M->547M(2048M) 14.979ms
[2026-09-15T00:32:48.474+0300][0.514s][info][gc,cpu      ] GC(5) User=0.03s Sys=0.06s Real=0.02s
[2026-09-15T00:32:48.546+0300][0.585s][info][gc,start    ] GC(6) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.546+0300][0.586s][info][gc,task     ] GC(6) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,phases   ] GC(6)   Pre Evacuate Collection Set: 0.48ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,phases   ] GC(6)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,phases   ] GC(6)   Evacuate Collection Set: 18.19ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,phases   ] GC(6)   Post Evacuate Collection Set: 0.21ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,phases   ] GC(6)   Other: 0.10ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,heap     ] GC(6) Eden regions: 89->0(89)
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,heap     ] GC(6) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,heap     ] GC(6) Old regions: 536->625
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,heap     ] GC(6) Humongous regions: 0->0
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,metaspace] GC(6) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.565+0300][0.605s][info][gc          ] GC(6) Pause Young (Normal) (G1 Evacuation Pause) 636M->636M(2048M) 19.332ms
[2026-09-15T00:32:48.565+0300][0.605s][info][gc,cpu      ] GC(6) User=0.02s Sys=0.03s Real=0.02s
[2026-09-15T00:32:48.634+0300][0.674s][info][gc,start    ] GC(7) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.635+0300][0.674s][info][gc,task     ] GC(7) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.648+0300][0.688s][info][gc,phases   ] GC(7)   Pre Evacuate Collection Set: 0.35ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,phases   ] GC(7)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,phases   ] GC(7)   Evacuate Collection Set: 13.20ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,phases   ] GC(7)   Post Evacuate Collection Set: 0.16ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,phases   ] GC(7)   Other: 0.09ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,heap     ] GC(7) Eden regions: 89->0(104)
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,heap     ] GC(7) Survivor regions: 13->13(13)
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,heap     ] GC(7) Old regions: 625->715
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,heap     ] GC(7) Humongous regions: 0->0
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,metaspace] GC(7) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.649+0300][0.688s][info][gc          ] GC(7) Pause Young (Normal) (G1 Evacuation Pause) 725M->726M(2048M) 14.323ms
[2026-09-15T00:32:48.649+0300][0.688s][info][gc,cpu      ] GC(7) User=0.02s Sys=0.03s Real=0.01s
[2026-09-15T00:32:48.727+0300][0.766s][info][gc,start    ] GC(8) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.727+0300][0.766s][info][gc,task     ] GC(8) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.748+0300][0.787s][info][gc,phases   ] GC(8)   Pre Evacuate Collection Set: 0.43ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,phases   ] GC(8)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,phases   ] GC(8)   Evacuate Collection Set: 20.18ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,phases   ] GC(8)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,phases   ] GC(8)   Other: 0.12ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,heap     ] GC(8) Eden regions: 104->0(128)
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,heap     ] GC(8) Survivor regions: 13->15(15)
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,heap     ] GC(8) Old regions: 715->817
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,heap     ] GC(8) Humongous regions: 0->0
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,metaspace] GC(8) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.748+0300][0.788s][info][gc          ] GC(8) Pause Young (Normal) (G1 Evacuation Pause) 830M->830M(2048M) 21.413ms
[2026-09-15T00:32:48.748+0300][0.788s][info][gc,cpu      ] GC(8) User=0.02s Sys=0.06s Real=0.02s
[2026-09-15T00:32:48.831+0300][0.871s][info][gc,start    ] GC(9) Pause Young (Normal) (G1 Evacuation Pause)
[2026-09-15T00:32:48.831+0300][0.871s][info][gc,task     ] GC(9) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,phases   ] GC(9)   Pre Evacuate Collection Set: 0.42ms
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,phases   ] GC(9)   Merge Heap Roots: 0.08ms
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,phases   ] GC(9)   Evacuate Collection Set: 54.66ms
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,phases   ] GC(9)   Post Evacuate Collection Set: 0.39ms
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,phases   ] GC(9)   Other: 0.15ms
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,heap     ] GC(9) Eden regions: 128->0(147)
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,heap     ] GC(9) Survivor regions: 15->18(18)
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,heap     ] GC(9) Old regions: 817->947
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,heap     ] GC(9) Humongous regions: 0->0
[2026-09-15T00:32:48.887+0300][0.927s][info][gc,metaspace] GC(9) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:48.887+0300][0.927s][info][gc          ] GC(9) Pause Young (Normal) (G1 Evacuation Pause) 958M->963M(2048M) 56.354ms
[2026-09-15T00:32:48.888+0300][0.927s][info][gc,cpu      ] GC(9) User=0.03s Sys=0.08s Real=0.05s
[2026-09-15T00:32:49.012+0300][1.052s][info][gc,start    ] GC(10) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:49.013+0300][1.052s][info][gc,task     ] GC(10) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,phases   ] GC(10)   Pre Evacuate Collection Set: 0.53ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,phases   ] GC(10)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,phases   ] GC(10)   Evacuate Collection Set: 42.32ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,phases   ] GC(10)   Post Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,phases   ] GC(10)   Other: 0.16ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,heap     ] GC(10) Eden regions: 147->0(172)
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,heap     ] GC(10) Survivor regions: 18->21(21)
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,heap     ] GC(10) Old regions: 947->1092
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,heap     ] GC(10) Humongous regions: 0->0
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,metaspace] GC(10) Metaspace: 319K(512K)->319K(512K) NonClass: 294K(384K)->294K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.056+0300][1.096s][info][gc          ] GC(10) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1110M->1111M(2048M) 43.679ms
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,cpu      ] GC(10) User=0.03s Sys=0.09s Real=0.04s
[2026-09-15T00:32:49.056+0300][1.096s][info][gc          ] GC(11) Concurrent Mark Cycle
[2026-09-15T00:32:49.056+0300][1.096s][info][gc,marking  ] GC(11) Concurrent Scan Root Regions
[2026-09-15T00:32:49.089+0300][1.128s][info][gc,marking  ] GC(11) Concurrent Scan Root Regions 32.294ms
[2026-09-15T00:32:49.089+0300][1.128s][info][gc,marking  ] GC(11) Concurrent Mark
[2026-09-15T00:32:49.089+0300][1.128s][info][gc,marking  ] GC(11) Concurrent Mark From Roots
[2026-09-15T00:32:49.089+0300][1.128s][info][gc,task     ] GC(11) Using 2 workers of 2 for marking
[2026-09-15T00:32:49.092+0300][1.132s][info][gc,marking  ] GC(11) Concurrent Mark From Roots 3.506ms
[2026-09-15T00:32:49.092+0300][1.132s][info][gc,marking  ] GC(11) Concurrent Preclean
[2026-09-15T00:32:49.092+0300][1.132s][info][gc,marking  ] GC(11) Concurrent Preclean 0.024ms
[2026-09-15T00:32:49.092+0300][1.132s][info][gc,start    ] GC(11) Pause Remark
[2026-09-15T00:32:49.093+0300][1.133s][info][gc          ] GC(11) Pause Remark 1170M->1170M(2048M) 0.670ms
[2026-09-15T00:32:49.093+0300][1.133s][info][gc,cpu      ] GC(11) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:49.093+0300][1.133s][info][gc,marking  ] GC(11) Concurrent Mark 4.336ms
[2026-09-15T00:32:49.093+0300][1.133s][info][gc,marking  ] GC(11) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:49.096+0300][1.135s][info][gc,marking  ] GC(11) Concurrent Rebuild Remembered Sets and Scrub Regions 2.710ms
[2026-09-15T00:32:49.096+0300][1.135s][info][gc,start    ] GC(11) Pause Cleanup
[2026-09-15T00:32:49.096+0300][1.136s][info][gc          ] GC(11) Pause Cleanup 1170M->1170M(2048M) 0.185ms
[2026-09-15T00:32:49.096+0300][1.136s][info][gc,cpu      ] GC(11) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:49.096+0300][1.136s][info][gc,marking  ] GC(11) Concurrent Clear Claimed Marks
[2026-09-15T00:32:49.096+0300][1.136s][info][gc,marking  ] GC(11) Concurrent Clear Claimed Marks 0.006ms
[2026-09-15T00:32:49.096+0300][1.136s][info][gc,marking  ] GC(11) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:49.098+0300][1.138s][info][gc,marking  ] GC(11) Concurrent Cleanup for Next Mark 2.236ms
[2026-09-15T00:32:49.098+0300][1.138s][info][gc          ] GC(11) Concurrent Mark Cycle 42.072ms
[2026-09-15T00:32:49.189+0300][1.228s][info][gc,start    ] GC(12) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:49.189+0300][1.229s][info][gc,task     ] GC(12) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,phases   ] GC(12)   Pre Evacuate Collection Set: 0.25ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,phases   ] GC(12)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,phases   ] GC(12)   Evacuate Collection Set: 52.48ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,phases   ] GC(12)   Post Evacuate Collection Set: 0.48ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,phases   ] GC(12)   Other: 0.08ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,heap     ] GC(12) Eden regions: 172->0(197)
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,heap     ] GC(12) Survivor regions: 21->25(25)
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,heap     ] GC(12) Old regions: 1092->1260
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,heap     ] GC(12) Humongous regions: 0->0
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,metaspace] GC(12) Metaspace: 320K(512K)->320K(512K) NonClass: 295K(384K)->295K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.242+0300][1.282s][info][gc          ] GC(12) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1283M->1283M(2048M) 53.760ms
[2026-09-15T00:32:49.242+0300][1.282s][info][gc,cpu      ] GC(12) User=0.04s Sys=0.09s Real=0.06s
[2026-09-15T00:32:49.398+0300][1.438s][info][gc,start    ] GC(13) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:49.398+0300][1.438s][info][gc,task     ] GC(13) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,phases   ] GC(13)   Pre Evacuate Collection Set: 0.49ms
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,phases   ] GC(13)   Merge Heap Roots: 0.08ms
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,phases   ] GC(13)   Evacuate Collection Set: 64.38ms
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,phases   ] GC(13)   Post Evacuate Collection Set: 0.19ms
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,phases   ] GC(13)   Other: 0.16ms
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,heap     ] GC(13) Eden regions: 197->0(182)
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,heap     ] GC(13) Survivor regions: 25->28(28)
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,heap     ] GC(13) Old regions: 1260->1447
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,heap     ] GC(13) Humongous regions: 0->0
[2026-09-15T00:32:49.463+0300][1.503s][info][gc,metaspace] GC(13) Metaspace: 320K(512K)->320K(512K) NonClass: 295K(384K)->295K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.464+0300][1.503s][info][gc          ] GC(13) Pause Young (Mixed) (G1 Evacuation Pause) 1480M->1474M(2048M) 65.646ms
[2026-09-15T00:32:49.464+0300][1.503s][info][gc,cpu      ] GC(13) User=0.03s Sys=0.11s Real=0.07s
[2026-09-15T00:32:49.590+0300][1.629s][info][gc,start    ] GC(14) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:49.590+0300][1.629s][info][gc,task     ] GC(14) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,phases   ] GC(14)   Pre Evacuate Collection Set: 0.39ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,phases   ] GC(14)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,phases   ] GC(14)   Evacuate Collection Set: 51.52ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,phases   ] GC(14)   Post Evacuate Collection Set: 0.27ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,phases   ] GC(14)   Other: 0.09ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,heap     ] GC(14) Eden regions: 182->0(126)
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,heap     ] GC(14) Survivor regions: 28->27(27)
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,heap     ] GC(14) Old regions: 1447->1630
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,heap     ] GC(14) Humongous regions: 0->0
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,metaspace] GC(14) Metaspace: 320K(512K)->320K(512K) NonClass: 295K(384K)->295K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.642+0300][1.682s][info][gc          ] GC(14) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1656M->1656M(2048M) 52.738ms
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,cpu      ] GC(14) User=0.04s Sys=0.11s Real=0.05s
[2026-09-15T00:32:49.642+0300][1.682s][info][gc          ] GC(15) Concurrent Mark Cycle
[2026-09-15T00:32:49.642+0300][1.682s][info][gc,marking  ] GC(15) Concurrent Scan Root Regions
[2026-09-15T00:32:49.646+0300][1.686s][info][gc,marking  ] GC(15) Concurrent Scan Root Regions 3.819ms
[2026-09-15T00:32:49.646+0300][1.686s][info][gc,marking  ] GC(15) Concurrent Mark
[2026-09-15T00:32:49.646+0300][1.686s][info][gc,marking  ] GC(15) Concurrent Mark From Roots
[2026-09-15T00:32:49.646+0300][1.686s][info][gc,task     ] GC(15) Using 2 workers of 2 for marking
[2026-09-15T00:32:49.660+0300][1.700s][info][gc,marking  ] GC(15) Concurrent Mark From Roots 14.197ms
[2026-09-15T00:32:49.661+0300][1.700s][info][gc,marking  ] GC(15) Concurrent Preclean
[2026-09-15T00:32:49.661+0300][1.700s][info][gc,marking  ] GC(15) Concurrent Preclean 0.020ms
[2026-09-15T00:32:49.661+0300][1.700s][info][gc,start    ] GC(15) Pause Remark
[2026-09-15T00:32:49.662+0300][1.702s][info][gc          ] GC(15) Pause Remark 1676M->1138M(2048M) 1.169ms
[2026-09-15T00:32:49.662+0300][1.702s][info][gc,cpu      ] GC(15) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:49.662+0300][1.702s][info][gc,marking  ] GC(15) Concurrent Mark 15.586ms
[2026-09-15T00:32:49.662+0300][1.702s][info][gc,marking  ] GC(15) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:49.664+0300][1.704s][info][gc,marking  ] GC(15) Concurrent Rebuild Remembered Sets and Scrub Regions 2.205ms
[2026-09-15T00:32:49.664+0300][1.704s][info][gc,start    ] GC(15) Pause Cleanup
[2026-09-15T00:32:49.664+0300][1.704s][info][gc          ] GC(15) Pause Cleanup 1138M->1138M(2048M) 0.354ms
[2026-09-15T00:32:49.665+0300][1.704s][info][gc,cpu      ] GC(15) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:49.665+0300][1.704s][info][gc,marking  ] GC(15) Concurrent Clear Claimed Marks
[2026-09-15T00:32:49.665+0300][1.704s][info][gc,marking  ] GC(15) Concurrent Clear Claimed Marks 0.009ms
[2026-09-15T00:32:49.665+0300][1.704s][info][gc,marking  ] GC(15) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:49.667+0300][1.707s][info][gc,marking  ] GC(15) Concurrent Cleanup for Next Mark 2.326ms
[2026-09-15T00:32:49.667+0300][1.707s][info][gc          ] GC(15) Concurrent Mark Cycle 24.588ms
[2026-09-15T00:32:49.743+0300][1.783s][info][gc,start    ] GC(16) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:49.743+0300][1.783s][info][gc,task     ] GC(16) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,phases   ] GC(16)   Pre Evacuate Collection Set: 0.28ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,phases   ] GC(16)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,phases   ] GC(16)   Evacuate Collection Set: 6.09ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,phases   ] GC(16)   Post Evacuate Collection Set: 0.18ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,phases   ] GC(16)   Other: 0.05ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,heap     ] GC(16) Eden regions: 126->0(256)
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,heap     ] GC(16) Survivor regions: 27->20(20)
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,heap     ] GC(16) Old regions: 1092->1224
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,heap     ] GC(16) Humongous regions: 0->0
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,metaspace] GC(16) Metaspace: 320K(512K)->320K(512K) NonClass: 295K(384K)->295K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.750+0300][1.790s][info][gc          ] GC(16) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1244M->1243M(2048M) 6.868ms
[2026-09-15T00:32:49.750+0300][1.790s][info][gc,cpu      ] GC(16) User=0.03s Sys=0.01s Real=0.01s
[2026-09-15T00:32:49.929+0300][1.969s][info][gc,start    ] GC(17) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:49.929+0300][1.969s][info][gc,task     ] GC(17) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,phases   ] GC(17)   Pre Evacuate Collection Set: 0.33ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,phases   ] GC(17)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,phases   ] GC(17)   Evacuate Collection Set: 24.30ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,phases   ] GC(17)   Post Evacuate Collection Set: 0.21ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,phases   ] GC(17)   Other: 0.10ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,heap     ] GC(17) Eden regions: 256->0(180)
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,heap     ] GC(17) Survivor regions: 20->35(35)
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,heap     ] GC(17) Old regions: 1224->1457
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,heap     ] GC(17) Humongous regions: 0->0
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,metaspace] GC(17) Metaspace: 320K(512K)->320K(512K) NonClass: 295K(384K)->295K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:49.954+0300][1.994s][info][gc          ] GC(17) Pause Young (Mixed) (G1 Evacuation Pause) 1499M->1491M(2048M) 25.265ms
[2026-09-15T00:32:49.954+0300][1.994s][info][gc,cpu      ] GC(17) User=0.06s Sys=0.05s Real=0.03s
[2026-09-15T00:32:50.080+0300][2.120s][info][gc,start    ] GC(18) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:50.081+0300][2.120s][info][gc,task     ] GC(18) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,phases   ] GC(18)   Pre Evacuate Collection Set: 0.63ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,phases   ] GC(18)   Merge Heap Roots: 0.09ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,phases   ] GC(18)   Evacuate Collection Set: 18.38ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,phases   ] GC(18)   Post Evacuate Collection Set: 0.21ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,phases   ] GC(18)   Other: 0.16ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,heap     ] GC(18) Eden regions: 180->0(122)
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,heap     ] GC(18) Survivor regions: 35->27(27)
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,heap     ] GC(18) Old regions: 1457->1645
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,heap     ] GC(18) Humongous regions: 0->0
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,metaspace] GC(18) Metaspace: 321K(512K)->321K(512K) NonClass: 296K(384K)->296K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.100+0300][2.140s][info][gc          ] GC(18) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1671M->1671M(2048M) 19.815ms
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,cpu      ] GC(18) User=0.06s Sys=0.04s Real=0.02s
[2026-09-15T00:32:50.100+0300][2.140s][info][gc          ] GC(19) Concurrent Mark Cycle
[2026-09-15T00:32:50.100+0300][2.140s][info][gc,marking  ] GC(19) Concurrent Scan Root Regions
[2026-09-15T00:32:50.101+0300][2.140s][info][gc,marking  ] GC(19) Concurrent Scan Root Regions 0.377ms
[2026-09-15T00:32:50.101+0300][2.140s][info][gc,marking  ] GC(19) Concurrent Mark
[2026-09-15T00:32:50.101+0300][2.140s][info][gc,marking  ] GC(19) Concurrent Mark From Roots
[2026-09-15T00:32:50.101+0300][2.140s][info][gc,task     ] GC(19) Using 2 workers of 2 for marking
[2026-09-15T00:32:50.104+0300][2.144s][info][gc,marking  ] GC(19) Concurrent Mark From Roots 3.340ms
[2026-09-15T00:32:50.104+0300][2.144s][info][gc,marking  ] GC(19) Concurrent Preclean
[2026-09-15T00:32:50.104+0300][2.144s][info][gc,marking  ] GC(19) Concurrent Preclean 0.015ms
[2026-09-15T00:32:50.104+0300][2.144s][info][gc,start    ] GC(19) Pause Remark
[2026-09-15T00:32:50.105+0300][2.144s][info][gc          ] GC(19) Pause Remark 1676M->1127M(2048M) 0.629ms
[2026-09-15T00:32:50.105+0300][2.144s][info][gc,cpu      ] GC(19) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:50.105+0300][2.144s][info][gc,marking  ] GC(19) Concurrent Mark 4.121ms
[2026-09-15T00:32:50.105+0300][2.144s][info][gc,marking  ] GC(19) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:50.107+0300][2.146s][info][gc,marking  ] GC(19) Concurrent Rebuild Remembered Sets and Scrub Regions 1.877ms
[2026-09-15T00:32:50.107+0300][2.146s][info][gc,start    ] GC(19) Pause Cleanup
[2026-09-15T00:32:50.107+0300][2.147s][info][gc          ] GC(19) Pause Cleanup 1127M->1127M(2048M) 0.278ms
[2026-09-15T00:32:50.107+0300][2.147s][info][gc,cpu      ] GC(19) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:50.107+0300][2.147s][info][gc,marking  ] GC(19) Concurrent Clear Claimed Marks
[2026-09-15T00:32:50.107+0300][2.147s][info][gc,marking  ] GC(19) Concurrent Clear Claimed Marks 0.013ms
[2026-09-15T00:32:50.107+0300][2.147s][info][gc,marking  ] GC(19) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:50.109+0300][2.148s][info][gc,marking  ] GC(19) Concurrent Cleanup for Next Mark 1.658ms
[2026-09-15T00:32:50.109+0300][2.148s][info][gc          ] GC(19) Concurrent Mark Cycle 8.564ms
[2026-09-15T00:32:50.202+0300][2.241s][info][gc,start    ] GC(20) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:50.202+0300][2.242s][info][gc,task     ] GC(20) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,phases   ] GC(20)   Pre Evacuate Collection Set: 0.52ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,phases   ] GC(20)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,phases   ] GC(20)   Evacuate Collection Set: 13.21ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,phases   ] GC(20)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,phases   ] GC(20)   Other: 0.11ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,heap     ] GC(20) Eden regions: 122->0(259)
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,heap     ] GC(20) Survivor regions: 27->19(19)
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,heap     ] GC(20) Old regions: 1096->1226
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,heap     ] GC(20) Humongous regions: 0->0
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,metaspace] GC(20) Metaspace: 321K(512K)->321K(512K) NonClass: 296K(384K)->296K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.216+0300][2.256s][info][gc          ] GC(20) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1244M->1244M(2048M) 14.436ms
[2026-09-15T00:32:50.216+0300][2.256s][info][gc,cpu      ] GC(20) User=0.05s Sys=0.02s Real=0.01s
[2026-09-15T00:32:50.415+0300][2.455s][info][gc,start    ] GC(21) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:50.415+0300][2.455s][info][gc,task     ] GC(21) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,phases   ] GC(21)   Pre Evacuate Collection Set: 0.50ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,phases   ] GC(21)   Merge Heap Roots: 0.09ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,phases   ] GC(21)   Evacuate Collection Set: 21.30ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,phases   ] GC(21)   Post Evacuate Collection Set: 0.28ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,phases   ] GC(21)   Other: 0.17ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,heap     ] GC(21) Eden regions: 259->0(180)
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,heap     ] GC(21) Survivor regions: 19->35(35)
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,heap     ] GC(21) Old regions: 1226->1457
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,heap     ] GC(21) Humongous regions: 0->0
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,metaspace] GC(21) Metaspace: 325K(512K)->325K(512K) NonClass: 300K(384K)->300K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.438+0300][2.477s][info][gc          ] GC(21) Pause Young (Mixed) (G1 Evacuation Pause) 1503M->1491M(2048M) 22.709ms
[2026-09-15T00:32:50.438+0300][2.477s][info][gc,cpu      ] GC(21) User=0.08s Sys=0.03s Real=0.02s
[2026-09-15T00:32:50.561+0300][2.600s][info][gc,start    ] GC(22) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:50.561+0300][2.600s][info][gc,task     ] GC(22) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,phases   ] GC(22)   Pre Evacuate Collection Set: 0.83ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,phases   ] GC(22)   Merge Heap Roots: 0.18ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,phases   ] GC(22)   Evacuate Collection Set: 17.80ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,phases   ] GC(22)   Post Evacuate Collection Set: 0.25ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,phases   ] GC(22)   Other: 0.18ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,heap     ] GC(22) Eden regions: 180->0(122)
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,heap     ] GC(22) Survivor regions: 35->27(27)
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,heap     ] GC(22) Old regions: 1457->1645
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,heap     ] GC(22) Humongous regions: 0->0
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,metaspace] GC(22) Metaspace: 325K(512K)->325K(512K) NonClass: 300K(384K)->300K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.580+0300][2.620s][info][gc          ] GC(22) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1671M->1671M(2048M) 19.627ms
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,cpu      ] GC(22) User=0.07s Sys=0.02s Real=0.02s
[2026-09-15T00:32:50.580+0300][2.620s][info][gc          ] GC(23) Concurrent Mark Cycle
[2026-09-15T00:32:50.580+0300][2.620s][info][gc,marking  ] GC(23) Concurrent Scan Root Regions
[2026-09-15T00:32:50.581+0300][2.620s][info][gc,marking  ] GC(23) Concurrent Scan Root Regions 0.422ms
[2026-09-15T00:32:50.581+0300][2.620s][info][gc,marking  ] GC(23) Concurrent Mark
[2026-09-15T00:32:50.581+0300][2.620s][info][gc,marking  ] GC(23) Concurrent Mark From Roots
[2026-09-15T00:32:50.581+0300][2.620s][info][gc,task     ] GC(23) Using 2 workers of 2 for marking
[2026-09-15T00:32:50.584+0300][2.624s][info][gc,marking  ] GC(23) Concurrent Mark From Roots 3.688ms
[2026-09-15T00:32:50.584+0300][2.624s][info][gc,marking  ] GC(23) Concurrent Preclean
[2026-09-15T00:32:50.584+0300][2.624s][info][gc,marking  ] GC(23) Concurrent Preclean 0.014ms
[2026-09-15T00:32:50.584+0300][2.624s][info][gc,start    ] GC(23) Pause Remark
[2026-09-15T00:32:50.585+0300][2.625s][info][gc          ] GC(23) Pause Remark 1696M->1172M(2048M) 0.847ms
[2026-09-15T00:32:50.585+0300][2.625s][info][gc,cpu      ] GC(23) User=0.00s Sys=0.01s Real=0.00s
[2026-09-15T00:32:50.585+0300][2.625s][info][gc,marking  ] GC(23) Concurrent Mark 4.664ms
[2026-09-15T00:32:50.585+0300][2.625s][info][gc,marking  ] GC(23) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:50.587+0300][2.627s][info][gc,marking  ] GC(23) Concurrent Rebuild Remembered Sets and Scrub Regions 1.908ms
[2026-09-15T00:32:50.587+0300][2.627s][info][gc,start    ] GC(23) Pause Cleanup
[2026-09-15T00:32:50.588+0300][2.627s][info][gc          ] GC(23) Pause Cleanup 1172M->1172M(2048M) 0.257ms
[2026-09-15T00:32:50.588+0300][2.627s][info][gc,cpu      ] GC(23) User=0.01s Sys=0.00s Real=0.00s
[2026-09-15T00:32:50.588+0300][2.627s][info][gc,marking  ] GC(23) Concurrent Clear Claimed Marks
[2026-09-15T00:32:50.588+0300][2.627s][info][gc,marking  ] GC(23) Concurrent Clear Claimed Marks 0.009ms
[2026-09-15T00:32:50.588+0300][2.627s][info][gc,marking  ] GC(23) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:50.589+0300][2.629s][info][gc,marking  ] GC(23) Concurrent Cleanup for Next Mark 1.689ms
[2026-09-15T00:32:50.589+0300][2.629s][info][gc          ] GC(23) Concurrent Mark Cycle 9.169ms
[2026-09-15T00:32:50.660+0300][2.700s][info][gc,start    ] GC(24) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:50.660+0300][2.700s][info][gc,task     ] GC(24) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,phases   ] GC(24)   Pre Evacuate Collection Set: 0.40ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,phases   ] GC(24)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,phases   ] GC(24)   Evacuate Collection Set: 10.07ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,phases   ] GC(24)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,phases   ] GC(24)   Other: 0.10ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,heap     ] GC(24) Eden regions: 122->0(255)
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,heap     ] GC(24) Survivor regions: 27->19(19)
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,heap     ] GC(24) Old regions: 1121->1251
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,heap     ] GC(24) Humongous regions: 0->0
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,metaspace] GC(24) Metaspace: 325K(512K)->325K(512K) NonClass: 300K(384K)->300K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.671+0300][2.711s][info][gc          ] GC(24) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1269M->1269M(2048M) 11.195ms
[2026-09-15T00:32:50.671+0300][2.711s][info][gc,cpu      ] GC(24) User=0.04s Sys=0.01s Real=0.01s
[2026-09-15T00:32:50.878+0300][2.918s][info][gc,start    ] GC(25) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:50.878+0300][2.918s][info][gc,task     ] GC(25) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,phases   ] GC(25)   Pre Evacuate Collection Set: 0.37ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,phases   ] GC(25)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,phases   ] GC(25)   Evacuate Collection Set: 20.11ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,phases   ] GC(25)   Post Evacuate Collection Set: 0.26ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,phases   ] GC(25)   Other: 0.17ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,heap     ] GC(25) Eden regions: 255->0(174)
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,heap     ] GC(25) Survivor regions: 19->35(35)
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,heap     ] GC(25) Old regions: 1251->1476
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,heap     ] GC(25) Humongous regions: 0->0
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,metaspace] GC(25) Metaspace: 325K(512K)->325K(512K) NonClass: 300K(384K)->300K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:50.899+0300][2.939s][info][gc          ] GC(25) Pause Young (Mixed) (G1 Evacuation Pause) 1524M->1510M(2048M) 21.465ms
[2026-09-15T00:32:50.899+0300][2.939s][info][gc,cpu      ] GC(25) User=0.08s Sys=0.03s Real=0.02s
[2026-09-15T00:32:51.032+0300][3.072s][info][gc,start    ] GC(26) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:51.032+0300][3.072s][info][gc,task     ] GC(26) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,phases   ] GC(26)   Pre Evacuate Collection Set: 0.40ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,phases   ] GC(26)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,phases   ] GC(26)   Evacuate Collection Set: 12.85ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,phases   ] GC(26)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,phases   ] GC(26)   Other: 0.10ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,heap     ] GC(26) Eden regions: 174->0(117)
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,heap     ] GC(26) Survivor regions: 35->27(27)
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,heap     ] GC(26) Old regions: 1476->1658
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,heap     ] GC(26) Humongous regions: 0->0
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,metaspace] GC(26) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.046+0300][3.086s][info][gc          ] GC(26) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1684M->1684M(2048M) 13.993ms
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,cpu      ] GC(26) User=0.05s Sys=0.04s Real=0.01s
[2026-09-15T00:32:51.046+0300][3.086s][info][gc          ] GC(27) Concurrent Mark Cycle
[2026-09-15T00:32:51.046+0300][3.086s][info][gc,marking  ] GC(27) Concurrent Scan Root Regions
[2026-09-15T00:32:51.047+0300][3.087s][info][gc,marking  ] GC(27) Concurrent Scan Root Regions 0.801ms
[2026-09-15T00:32:51.047+0300][3.087s][info][gc,marking  ] GC(27) Concurrent Mark
[2026-09-15T00:32:51.047+0300][3.087s][info][gc,marking  ] GC(27) Concurrent Mark From Roots
[2026-09-15T00:32:51.047+0300][3.087s][info][gc,task     ] GC(27) Using 2 workers of 2 for marking
[2026-09-15T00:32:51.050+0300][3.090s][info][gc,marking  ] GC(27) Concurrent Mark From Roots 3.530ms
[2026-09-15T00:32:51.050+0300][3.090s][info][gc,marking  ] GC(27) Concurrent Preclean
[2026-09-15T00:32:51.050+0300][3.090s][info][gc,marking  ] GC(27) Concurrent Preclean 0.013ms
[2026-09-15T00:32:51.050+0300][3.090s][info][gc,start    ] GC(27) Pause Remark
[2026-09-15T00:32:51.051+0300][3.091s][info][gc          ] GC(27) Pause Remark 1704M->1161M(2048M) 0.663ms
[2026-09-15T00:32:51.051+0300][3.091s][info][gc,cpu      ] GC(27) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.051+0300][3.091s][info][gc,marking  ] GC(27) Concurrent Mark 4.329ms
[2026-09-15T00:32:51.051+0300][3.091s][info][gc,marking  ] GC(27) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:51.053+0300][3.093s][info][gc,marking  ] GC(27) Concurrent Rebuild Remembered Sets and Scrub Regions 2.202ms
[2026-09-15T00:32:51.053+0300][3.093s][info][gc,start    ] GC(27) Pause Cleanup
[2026-09-15T00:32:51.054+0300][3.093s][info][gc          ] GC(27) Pause Cleanup 1161M->1161M(2048M) 0.212ms
[2026-09-15T00:32:51.054+0300][3.093s][info][gc,cpu      ] GC(27) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.054+0300][3.093s][info][gc,marking  ] GC(27) Concurrent Clear Claimed Marks
[2026-09-15T00:32:51.054+0300][3.093s][info][gc,marking  ] GC(27) Concurrent Clear Claimed Marks 0.008ms
[2026-09-15T00:32:51.054+0300][3.093s][info][gc,marking  ] GC(27) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:51.055+0300][3.094s][info][gc,marking  ] GC(27) Concurrent Cleanup for Next Mark 1.061ms
[2026-09-15T00:32:51.055+0300][3.094s][info][gc          ] GC(27) Concurrent Mark Cycle 8.800ms
[2026-09-15T00:32:51.121+0300][3.161s][info][gc,start    ] GC(28) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:51.121+0300][3.161s][info][gc,task     ] GC(28) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,phases   ] GC(28)   Pre Evacuate Collection Set: 0.20ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,phases   ] GC(28)   Merge Heap Roots: 0.08ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,phases   ] GC(28)   Evacuate Collection Set: 3.88ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,phases   ] GC(28)   Post Evacuate Collection Set: 0.20ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,phases   ] GC(28)   Other: 0.06ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,heap     ] GC(28) Eden regions: 117->0(259)
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,heap     ] GC(28) Survivor regions: 27->18(18)
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,heap     ] GC(28) Old regions: 1115->1241
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,heap     ] GC(28) Humongous regions: 0->0
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,metaspace] GC(28) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.125+0300][3.165s][info][gc          ] GC(28) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1258M->1258M(2048M) 4.624ms
[2026-09-15T00:32:51.125+0300][3.165s][info][gc,cpu      ] GC(28) User=0.03s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.326+0300][3.365s][info][gc,start    ] GC(29) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:51.326+0300][3.365s][info][gc,task     ] GC(29) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,phases   ] GC(29)   Pre Evacuate Collection Set: 0.38ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,phases   ] GC(29)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,phases   ] GC(29)   Evacuate Collection Set: 18.66ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,phases   ] GC(29)   Post Evacuate Collection Set: 0.25ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,phases   ] GC(29)   Other: 0.09ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,heap     ] GC(29) Eden regions: 259->0(177)
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,heap     ] GC(29) Survivor regions: 18->35(35)
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,heap     ] GC(29) Old regions: 1241->1469
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,heap     ] GC(29) Humongous regions: 0->0
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,metaspace] GC(29) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.345+0300][3.385s][info][gc          ] GC(29) Pause Young (Mixed) (G1 Evacuation Pause) 1517M->1503M(2048M) 19.880ms
[2026-09-15T00:32:51.345+0300][3.385s][info][gc,cpu      ] GC(29) User=0.09s Sys=0.01s Real=0.02s
[2026-09-15T00:32:51.469+0300][3.508s][info][gc,start    ] GC(30) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:51.469+0300][3.509s][info][gc,task     ] GC(30) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.485+0300][3.525s][info][gc,phases   ] GC(30)   Pre Evacuate Collection Set: 0.38ms
[2026-09-15T00:32:51.485+0300][3.525s][info][gc,phases   ] GC(30)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:51.485+0300][3.525s][info][gc,phases   ] GC(30)   Evacuate Collection Set: 15.56ms
[2026-09-15T00:32:51.485+0300][3.525s][info][gc,phases   ] GC(30)   Post Evacuate Collection Set: 0.41ms
[2026-09-15T00:32:51.485+0300][3.525s][info][gc,phases   ] GC(30)   Other: 0.15ms
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,heap     ] GC(30) Eden regions: 177->0(119)
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,heap     ] GC(30) Survivor regions: 35->27(27)
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,heap     ] GC(30) Old regions: 1469->1654
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,heap     ] GC(30) Humongous regions: 0->0
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,metaspace] GC(30) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.486+0300][3.525s][info][gc          ] GC(30) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1680M->1680M(2048M) 16.889ms
[2026-09-15T00:32:51.486+0300][3.525s][info][gc          ] GC(31) Concurrent Mark Cycle
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,cpu      ] GC(30) User=0.08s Sys=0.00s Real=0.02s
[2026-09-15T00:32:51.486+0300][3.525s][info][gc,marking  ] GC(31) Concurrent Scan Root Regions
[2026-09-15T00:32:51.486+0300][3.526s][info][gc,marking  ] GC(31) Concurrent Scan Root Regions 0.666ms
[2026-09-15T00:32:51.486+0300][3.526s][info][gc,marking  ] GC(31) Concurrent Mark
[2026-09-15T00:32:51.486+0300][3.526s][info][gc,marking  ] GC(31) Concurrent Mark From Roots
[2026-09-15T00:32:51.486+0300][3.526s][info][gc,task     ] GC(31) Using 2 workers of 2 for marking
[2026-09-15T00:32:51.490+0300][3.530s][info][gc,marking  ] GC(31) Concurrent Mark From Roots 3.826ms
[2026-09-15T00:32:51.490+0300][3.530s][info][gc,marking  ] GC(31) Concurrent Preclean
[2026-09-15T00:32:51.490+0300][3.530s][info][gc,marking  ] GC(31) Concurrent Preclean 0.013ms
[2026-09-15T00:32:51.490+0300][3.530s][info][gc,start    ] GC(31) Pause Remark
[2026-09-15T00:32:51.491+0300][3.531s][info][gc          ] GC(31) Pause Remark 1693M->1139M(2048M) 0.773ms
[2026-09-15T00:32:51.491+0300][3.531s][info][gc,cpu      ] GC(31) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.491+0300][3.531s][info][gc,marking  ] GC(31) Concurrent Mark 4.751ms
[2026-09-15T00:32:51.491+0300][3.531s][info][gc,marking  ] GC(31) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,marking  ] GC(31) Concurrent Rebuild Remembered Sets and Scrub Regions 1.969ms
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,start    ] GC(31) Pause Cleanup
[2026-09-15T00:32:51.493+0300][3.533s][info][gc          ] GC(31) Pause Cleanup 1139M->1139M(2048M) 0.302ms
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,cpu      ] GC(31) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,marking  ] GC(31) Concurrent Clear Claimed Marks
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,marking  ] GC(31) Concurrent Clear Claimed Marks 0.009ms
[2026-09-15T00:32:51.493+0300][3.533s][info][gc,marking  ] GC(31) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:51.495+0300][3.535s][info][gc,marking  ] GC(31) Concurrent Cleanup for Next Mark 1.703ms
[2026-09-15T00:32:51.495+0300][3.535s][info][gc          ] GC(31) Concurrent Mark Cycle 9.570ms
[2026-09-15T00:32:51.583+0300][3.622s][info][gc,start    ] GC(32) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:51.583+0300][3.622s][info][gc,task     ] GC(32) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,phases   ] GC(32)   Pre Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,phases   ] GC(32)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,phases   ] GC(32)   Evacuate Collection Set: 5.82ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,phases   ] GC(32)   Post Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,phases   ] GC(32)   Other: 0.07ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,heap     ] GC(32) Eden regions: 119->0(264)
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,heap     ] GC(32) Survivor regions: 27->19(19)
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,heap     ] GC(32) Old regions: 1100->1227
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,heap     ] GC(32) Humongous regions: 0->0
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,metaspace] GC(32) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.589+0300][3.629s][info][gc          ] GC(32) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1245M->1245M(2048M) 6.666ms
[2026-09-15T00:32:51.589+0300][3.629s][info][gc,cpu      ] GC(32) User=0.03s Sys=0.01s Real=0.00s
[2026-09-15T00:32:51.762+0300][3.802s][info][gc,start    ] GC(33) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:51.762+0300][3.802s][info][gc,task     ] GC(33) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,phases   ] GC(33)   Pre Evacuate Collection Set: 0.26ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,phases   ] GC(33)   Merge Heap Roots: 0.08ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,phases   ] GC(33)   Evacuate Collection Set: 17.78ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,phases   ] GC(33)   Post Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,phases   ] GC(33)   Other: 0.08ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,heap     ] GC(33) Eden regions: 264->0(180)
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,heap     ] GC(33) Survivor regions: 19->36(36)
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,heap     ] GC(33) Old regions: 1227->1459
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,heap     ] GC(33) Humongous regions: 0->0
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,metaspace] GC(33) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.781+0300][3.821s][info][gc          ] GC(33) Pause Young (Mixed) (G1 Evacuation Pause) 1509M->1494M(2048M) 18.702ms
[2026-09-15T00:32:51.781+0300][3.821s][info][gc,cpu      ] GC(33) User=0.09s Sys=0.00s Real=0.02s
[2026-09-15T00:32:51.935+0300][3.975s][info][gc,start    ] GC(34) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:51.935+0300][3.975s][info][gc,task     ] GC(34) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,phases   ] GC(34)   Pre Evacuate Collection Set: 0.49ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,phases   ] GC(34)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,phases   ] GC(34)   Evacuate Collection Set: 14.65ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,phases   ] GC(34)   Post Evacuate Collection Set: 0.25ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,phases   ] GC(34)   Other: 0.13ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,heap     ] GC(34) Eden regions: 180->0(121)
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,heap     ] GC(34) Survivor regions: 36->27(27)
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,heap     ] GC(34) Old regions: 1459->1648
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,heap     ] GC(34) Humongous regions: 0->0
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,metaspace] GC(34) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:51.951+0300][3.991s][info][gc          ] GC(34) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1674M->1674M(2048M) 15.998ms
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,cpu      ] GC(34) User=0.07s Sys=0.00s Real=0.02s
[2026-09-15T00:32:51.951+0300][3.991s][info][gc          ] GC(35) Concurrent Mark Cycle
[2026-09-15T00:32:51.951+0300][3.991s][info][gc,marking  ] GC(35) Concurrent Scan Root Regions
[2026-09-15T00:32:51.952+0300][3.992s][info][gc,marking  ] GC(35) Concurrent Scan Root Regions 0.455ms
[2026-09-15T00:32:51.952+0300][3.992s][info][gc,marking  ] GC(35) Concurrent Mark
[2026-09-15T00:32:51.952+0300][3.992s][info][gc,marking  ] GC(35) Concurrent Mark From Roots
[2026-09-15T00:32:51.952+0300][3.992s][info][gc,task     ] GC(35) Using 2 workers of 2 for marking
[2026-09-15T00:32:51.956+0300][3.995s][info][gc,marking  ] GC(35) Concurrent Mark From Roots 3.824ms
[2026-09-15T00:32:51.956+0300][3.995s][info][gc,marking  ] GC(35) Concurrent Preclean
[2026-09-15T00:32:51.956+0300][3.995s][info][gc,marking  ] GC(35) Concurrent Preclean 0.018ms
[2026-09-15T00:32:51.956+0300][3.995s][info][gc,start    ] GC(35) Pause Remark
[2026-09-15T00:32:51.956+0300][3.996s][info][gc          ] GC(35) Pause Remark 1704M->1168M(2048M) 0.690ms
[2026-09-15T00:32:51.956+0300][3.996s][info][gc,cpu      ] GC(35) User=0.01s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.956+0300][3.996s][info][gc,marking  ] GC(35) Concurrent Mark 4.681ms
[2026-09-15T00:32:51.957+0300][3.996s][info][gc,marking  ] GC(35) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:51.958+0300][3.998s][info][gc,marking  ] GC(35) Concurrent Rebuild Remembered Sets and Scrub Regions 1.983ms
[2026-09-15T00:32:51.959+0300][3.998s][info][gc,start    ] GC(35) Pause Cleanup
[2026-09-15T00:32:51.959+0300][3.999s][info][gc          ] GC(35) Pause Cleanup 1168M->1168M(2048M) 0.280ms
[2026-09-15T00:32:51.959+0300][3.999s][info][gc,cpu      ] GC(35) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:51.959+0300][3.999s][info][gc,marking  ] GC(35) Concurrent Clear Claimed Marks
[2026-09-15T00:32:51.959+0300][3.999s][info][gc,marking  ] GC(35) Concurrent Clear Claimed Marks 0.009ms
[2026-09-15T00:32:51.959+0300][3.999s][info][gc,marking  ] GC(35) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:51.961+0300][4.000s][info][gc,marking  ] GC(35) Concurrent Cleanup for Next Mark 1.899ms
[2026-09-15T00:32:51.961+0300][4.001s][info][gc          ] GC(35) Concurrent Mark Cycle 9.513ms
[2026-09-15T00:32:52.030+0300][4.070s][info][gc,start    ] GC(36) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:52.030+0300][4.070s][info][gc,task     ] GC(36) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,phases   ] GC(36)   Pre Evacuate Collection Set: 0.33ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,phases   ] GC(36)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,phases   ] GC(36)   Evacuate Collection Set: 11.16ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,phases   ] GC(36)   Post Evacuate Collection Set: 0.24ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,phases   ] GC(36)   Other: 0.11ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,heap     ] GC(36) Eden regions: 121->0(259)
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,heap     ] GC(36) Survivor regions: 27->19(19)
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,heap     ] GC(36) Old regions: 1112->1241
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,heap     ] GC(36) Humongous regions: 0->0
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,metaspace] GC(36) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.042+0300][4.082s][info][gc          ] GC(36) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1259M->1259M(2048M) 12.176ms
[2026-09-15T00:32:52.042+0300][4.082s][info][gc,cpu      ] GC(36) User=0.05s Sys=0.00s Real=0.01s
[2026-09-15T00:32:52.247+0300][4.286s][info][gc,start    ] GC(37) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:52.247+0300][4.287s][info][gc,task     ] GC(37) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.269+0300][4.309s][info][gc,phases   ] GC(37)   Pre Evacuate Collection Set: 0.36ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,phases   ] GC(37)   Merge Heap Roots: 0.08ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,phases   ] GC(37)   Evacuate Collection Set: 21.75ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,phases   ] GC(37)   Post Evacuate Collection Set: 0.19ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,phases   ] GC(37)   Other: 0.10ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,heap     ] GC(37) Eden regions: 259->0(176)
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,heap     ] GC(37) Survivor regions: 19->35(35)
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,heap     ] GC(37) Old regions: 1241->1472
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,heap     ] GC(37) Humongous regions: 0->0
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,metaspace] GC(37) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.270+0300][4.309s][info][gc          ] GC(37) Pause Young (Mixed) (G1 Evacuation Pause) 1518M->1506M(2048M) 22.822ms
[2026-09-15T00:32:52.270+0300][4.309s][info][gc,cpu      ] GC(37) User=0.08s Sys=0.01s Real=0.03s
[2026-09-15T00:32:52.394+0300][4.434s][info][gc,start    ] GC(38) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:52.395+0300][4.434s][info][gc,task     ] GC(38) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,phases   ] GC(38)   Pre Evacuate Collection Set: 0.73ms
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,phases   ] GC(38)   Merge Heap Roots: 0.09ms
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,phases   ] GC(38)   Evacuate Collection Set: 14.59ms
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,phases   ] GC(38)   Post Evacuate Collection Set: 0.20ms
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,phases   ] GC(38)   Other: 0.19ms
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,heap     ] GC(38) Eden regions: 176->0(118)
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,heap     ] GC(38) Survivor regions: 35->27(27)
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,heap     ] GC(38) Old regions: 1472->1655
[2026-09-15T00:32:52.410+0300][4.450s][info][gc,heap     ] GC(38) Humongous regions: 0->0
[2026-09-15T00:32:52.411+0300][4.450s][info][gc,metaspace] GC(38) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.411+0300][4.450s][info][gc          ] GC(38) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1682M->1681M(2048M) 16.162ms
[2026-09-15T00:32:52.411+0300][4.450s][info][gc,cpu      ] GC(38) User=0.06s Sys=0.00s Real=0.02s
[2026-09-15T00:32:52.411+0300][4.450s][info][gc          ] GC(39) Concurrent Mark Cycle
[2026-09-15T00:32:52.411+0300][4.450s][info][gc,marking  ] GC(39) Concurrent Scan Root Regions
[2026-09-15T00:32:52.411+0300][4.451s][info][gc,marking  ] GC(39) Concurrent Scan Root Regions 0.479ms
[2026-09-15T00:32:52.411+0300][4.451s][info][gc,marking  ] GC(39) Concurrent Mark
[2026-09-15T00:32:52.411+0300][4.451s][info][gc,marking  ] GC(39) Concurrent Mark From Roots
[2026-09-15T00:32:52.411+0300][4.451s][info][gc,task     ] GC(39) Using 2 workers of 2 for marking
[2026-09-15T00:32:52.414+0300][4.453s][info][gc,marking  ] GC(39) Concurrent Mark From Roots 2.608ms
[2026-09-15T00:32:52.414+0300][4.453s][info][gc,marking  ] GC(39) Concurrent Preclean
[2026-09-15T00:32:52.414+0300][4.453s][info][gc,marking  ] GC(39) Concurrent Preclean 0.011ms
[2026-09-15T00:32:52.414+0300][4.453s][info][gc,start    ] GC(39) Pause Remark
[2026-09-15T00:32:52.414+0300][4.454s][info][gc          ] GC(39) Pause Remark 1702M->1157M(2048M) 0.640ms
[2026-09-15T00:32:52.414+0300][4.454s][info][gc,cpu      ] GC(39) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:52.414+0300][4.454s][info][gc,marking  ] GC(39) Concurrent Mark 3.359ms
[2026-09-15T00:32:52.414+0300][4.454s][info][gc,marking  ] GC(39) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:52.416+0300][4.456s][info][gc,marking  ] GC(39) Concurrent Rebuild Remembered Sets and Scrub Regions 1.913ms
[2026-09-15T00:32:52.416+0300][4.456s][info][gc,start    ] GC(39) Pause Cleanup
[2026-09-15T00:32:52.417+0300][4.456s][info][gc          ] GC(39) Pause Cleanup 1157M->1157M(2048M) 0.256ms
[2026-09-15T00:32:52.417+0300][4.456s][info][gc,cpu      ] GC(39) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:52.417+0300][4.456s][info][gc,marking  ] GC(39) Concurrent Clear Claimed Marks
[2026-09-15T00:32:52.417+0300][4.456s][info][gc,marking  ] GC(39) Concurrent Clear Claimed Marks 0.006ms
[2026-09-15T00:32:52.417+0300][4.456s][info][gc,marking  ] GC(39) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:52.419+0300][4.458s][info][gc,marking  ] GC(39) Concurrent Cleanup for Next Mark 1.825ms
[2026-09-15T00:32:52.419+0300][4.458s][info][gc          ] GC(39) Concurrent Mark Cycle 8.024ms
[2026-09-15T00:32:52.485+0300][4.525s][info][gc,start    ] GC(40) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:52.485+0300][4.525s][info][gc,task     ] GC(40) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,phases   ] GC(40)   Pre Evacuate Collection Set: 0.37ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,phases   ] GC(40)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,phases   ] GC(40)   Evacuate Collection Set: 11.95ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,phases   ] GC(40)   Post Evacuate Collection Set: 0.20ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,phases   ] GC(40)   Other: 0.07ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,heap     ] GC(40) Eden regions: 118->0(260)
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,heap     ] GC(40) Survivor regions: 27->19(19)
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,heap     ] GC(40) Old regions: 1110->1236
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,heap     ] GC(40) Humongous regions: 0->0
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,metaspace] GC(40) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.498+0300][4.538s][info][gc          ] GC(40) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1254M->1254M(2048M) 12.995ms
[2026-09-15T00:32:52.498+0300][4.538s][info][gc,cpu      ] GC(40) User=0.05s Sys=0.01s Real=0.01s
[2026-09-15T00:32:52.700+0300][4.739s][info][gc,start    ] GC(41) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:52.700+0300][4.740s][info][gc,task     ] GC(41) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,phases   ] GC(41)   Pre Evacuate Collection Set: 0.32ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,phases   ] GC(41)   Merge Heap Roots: 0.07ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,phases   ] GC(41)   Evacuate Collection Set: 18.33ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,phases   ] GC(41)   Post Evacuate Collection Set: 0.27ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,phases   ] GC(41)   Other: 0.10ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,heap     ] GC(41) Eden regions: 260->0(177)
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,heap     ] GC(41) Survivor regions: 19->35(35)
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,heap     ] GC(41) Old regions: 1236->1469
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,heap     ] GC(41) Humongous regions: 0->0
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,metaspace] GC(41) Metaspace: 327K(512K)->327K(512K) NonClass: 302K(384K)->302K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.719+0300][4.759s][info][gc          ] GC(41) Pause Young (Mixed) (G1 Evacuation Pause) 1514M->1503M(2048M) 19.472ms
[2026-09-15T00:32:52.719+0300][4.759s][info][gc,cpu      ] GC(41) User=0.09s Sys=0.00s Real=0.01s
[2026-09-15T00:32:52.844+0300][4.884s][info][gc,start    ] GC(42) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:52.845+0300][4.884s][info][gc,task     ] GC(42) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,phases   ] GC(42)   Pre Evacuate Collection Set: 0.34ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,phases   ] GC(42)   Merge Heap Roots: 0.06ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,phases   ] GC(42)   Evacuate Collection Set: 12.29ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,phases   ] GC(42)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,phases   ] GC(42)   Other: 0.09ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,heap     ] GC(42) Eden regions: 177->0(119)
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,heap     ] GC(42) Survivor regions: 35->27(27)
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,heap     ] GC(42) Old regions: 1469->1654
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,heap     ] GC(42) Humongous regions: 0->0
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,metaspace] GC(42) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.858+0300][4.897s][info][gc          ] GC(42) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1680M->1680M(2048M) 13.390ms
[2026-09-15T00:32:52.858+0300][4.897s][info][gc,cpu      ] GC(42) User=0.06s Sys=0.01s Real=0.01s
[2026-09-15T00:32:52.858+0300][4.898s][info][gc          ] GC(43) Concurrent Mark Cycle
[2026-09-15T00:32:52.858+0300][4.898s][info][gc,marking  ] GC(43) Concurrent Scan Root Regions
[2026-09-15T00:32:52.858+0300][4.898s][info][gc,marking  ] GC(43) Concurrent Scan Root Regions 0.403ms
[2026-09-15T00:32:52.858+0300][4.898s][info][gc,marking  ] GC(43) Concurrent Mark
[2026-09-15T00:32:52.858+0300][4.898s][info][gc,marking  ] GC(43) Concurrent Mark From Roots
[2026-09-15T00:32:52.858+0300][4.898s][info][gc,task     ] GC(43) Using 2 workers of 2 for marking
[2026-09-15T00:32:52.862+0300][4.901s][info][gc,marking  ] GC(43) Concurrent Mark From Roots 3.309ms
[2026-09-15T00:32:52.862+0300][4.901s][info][gc,marking  ] GC(43) Concurrent Preclean
[2026-09-15T00:32:52.862+0300][4.901s][info][gc,marking  ] GC(43) Concurrent Preclean 0.012ms
[2026-09-15T00:32:52.862+0300][4.901s][info][gc,start    ] GC(43) Pause Remark
[2026-09-15T00:32:52.862+0300][4.902s][info][gc          ] GC(43) Pause Remark 1693M->1123M(2048M) 0.635ms
[2026-09-15T00:32:52.862+0300][4.902s][info][gc,cpu      ] GC(43) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:52.862+0300][4.902s][info][gc,marking  ] GC(43) Concurrent Mark 4.102ms
[2026-09-15T00:32:52.862+0300][4.902s][info][gc,marking  ] GC(43) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:52.864+0300][4.904s][info][gc,marking  ] GC(43) Concurrent Rebuild Remembered Sets and Scrub Regions 1.948ms
[2026-09-15T00:32:52.864+0300][4.904s][info][gc,start    ] GC(43) Pause Cleanup
[2026-09-15T00:32:52.865+0300][4.904s][info][gc          ] GC(43) Pause Cleanup 1123M->1123M(2048M) 0.235ms
[2026-09-15T00:32:52.865+0300][4.904s][info][gc,cpu      ] GC(43) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:52.865+0300][4.904s][info][gc,marking  ] GC(43) Concurrent Clear Claimed Marks
[2026-09-15T00:32:52.865+0300][4.904s][info][gc,marking  ] GC(43) Concurrent Clear Claimed Marks 0.008ms
[2026-09-15T00:32:52.865+0300][4.904s][info][gc,marking  ] GC(43) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:52.866+0300][4.906s][info][gc,marking  ] GC(43) Concurrent Cleanup for Next Mark 1.655ms
[2026-09-15T00:32:52.866+0300][4.906s][info][gc          ] GC(43) Concurrent Mark Cycle 8.529ms
[2026-09-15T00:32:52.959+0300][4.999s][info][gc,start    ] GC(44) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:52.959+0300][4.999s][info][gc,task     ] GC(44) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,phases   ] GC(44)   Pre Evacuate Collection Set: 0.53ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,phases   ] GC(44)   Merge Heap Roots: 0.09ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,phases   ] GC(44)   Evacuate Collection Set: 6.26ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,phases   ] GC(44)   Post Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,phases   ] GC(44)   Other: 0.10ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,heap     ] GC(44) Eden regions: 119->0(269)
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,heap     ] GC(44) Survivor regions: 27->19(19)
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,heap     ] GC(44) Old regions: 1084->1211
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,heap     ] GC(44) Humongous regions: 0->0
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,metaspace] GC(44) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:52.966+0300][5.006s][info][gc          ] GC(44) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1229M->1229M(2048M) 7.542ms
[2026-09-15T00:32:52.966+0300][5.006s][info][gc,cpu      ] GC(44) User=0.04s Sys=0.00s Real=0.01s
[2026-09-15T00:32:53.168+0300][5.208s][info][gc,start    ] GC(45) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:53.169+0300][5.208s][info][gc,task     ] GC(45) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,phases   ] GC(45)   Pre Evacuate Collection Set: 0.56ms
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,phases   ] GC(45)   Merge Heap Roots: 0.17ms
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,phases   ] GC(45)   Evacuate Collection Set: 18.62ms
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,phases   ] GC(45)   Post Evacuate Collection Set: 0.26ms
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,phases   ] GC(45)   Other: 0.12ms
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,heap     ] GC(45) Eden regions: 269->0(180)
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,heap     ] GC(45) Survivor regions: 19->36(36)
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,heap     ] GC(45) Old regions: 1211->1458
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,heap     ] GC(45) Humongous regions: 0->0
[2026-09-15T00:32:53.188+0300][5.228s][info][gc,metaspace] GC(45) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:53.189+0300][5.228s][info][gc          ] GC(45) Pause Young (Mixed) (G1 Evacuation Pause) 1498M->1493M(2048M) 20.074ms
[2026-09-15T00:32:53.189+0300][5.228s][info][gc,cpu      ] GC(45) User=0.08s Sys=0.01s Real=0.02s
[2026-09-15T00:32:53.318+0300][5.358s][info][gc,start    ] GC(46) Pause Young (Concurrent Start) (G1 Evacuation Pause)
[2026-09-15T00:32:53.318+0300][5.358s][info][gc,task     ] GC(46) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,phases   ] GC(46)   Pre Evacuate Collection Set: 0.63ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,phases   ] GC(46)   Merge Heap Roots: 0.10ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,phases   ] GC(46)   Evacuate Collection Set: 15.83ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,phases   ] GC(46)   Post Evacuate Collection Set: 0.23ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,phases   ] GC(46)   Other: 0.13ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,heap     ] GC(46) Eden regions: 180->0(121)
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,heap     ] GC(46) Survivor regions: 36->27(27)
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,heap     ] GC(46) Old regions: 1458->1647
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,heap     ] GC(46) Humongous regions: 0->0
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,metaspace] GC(46) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:53.335+0300][5.375s][info][gc          ] GC(46) Pause Young (Concurrent Start) (G1 Evacuation Pause) 1673M->1673M(2048M) 17.263ms
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,cpu      ] GC(46) User=0.07s Sys=0.01s Real=0.02s
[2026-09-15T00:32:53.335+0300][5.375s][info][gc          ] GC(47) Concurrent Mark Cycle
[2026-09-15T00:32:53.335+0300][5.375s][info][gc,marking  ] GC(47) Concurrent Scan Root Regions
[2026-09-15T00:32:53.336+0300][5.375s][info][gc,marking  ] GC(47) Concurrent Scan Root Regions 0.476ms
[2026-09-15T00:32:53.336+0300][5.375s][info][gc,marking  ] GC(47) Concurrent Mark
[2026-09-15T00:32:53.336+0300][5.375s][info][gc,marking  ] GC(47) Concurrent Mark From Roots
[2026-09-15T00:32:53.336+0300][5.376s][info][gc,task     ] GC(47) Using 2 workers of 2 for marking
[2026-09-15T00:32:53.340+0300][5.379s][info][gc,marking  ] GC(47) Concurrent Mark From Roots 3.784ms
[2026-09-15T00:32:53.340+0300][5.379s][info][gc,marking  ] GC(47) Concurrent Preclean
[2026-09-15T00:32:53.340+0300][5.379s][info][gc,marking  ] GC(47) Concurrent Preclean 0.015ms
[2026-09-15T00:32:53.340+0300][5.379s][info][gc,start    ] GC(47) Pause Remark
[2026-09-15T00:32:53.340+0300][5.380s][info][gc          ] GC(47) Pause Remark 1698M->1142M(2048M) 0.711ms
[2026-09-15T00:32:53.340+0300][5.380s][info][gc,cpu      ] GC(47) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:53.340+0300][5.380s][info][gc,marking  ] GC(47) Concurrent Mark 4.653ms
[2026-09-15T00:32:53.340+0300][5.380s][info][gc,marking  ] GC(47) Concurrent Rebuild Remembered Sets and Scrub Regions
[2026-09-15T00:32:53.342+0300][5.382s][info][gc,marking  ] GC(47) Concurrent Rebuild Remembered Sets and Scrub Regions 1.892ms
[2026-09-15T00:32:53.342+0300][5.382s][info][gc,start    ] GC(47) Pause Cleanup
[2026-09-15T00:32:53.343+0300][5.382s][info][gc          ] GC(47) Pause Cleanup 1142M->1142M(2048M) 0.267ms
[2026-09-15T00:32:53.343+0300][5.382s][info][gc,cpu      ] GC(47) User=0.00s Sys=0.00s Real=0.00s
[2026-09-15T00:32:53.343+0300][5.382s][info][gc,marking  ] GC(47) Concurrent Clear Claimed Marks
[2026-09-15T00:32:53.343+0300][5.382s][info][gc,marking  ] GC(47) Concurrent Clear Claimed Marks 0.010ms
[2026-09-15T00:32:53.343+0300][5.382s][info][gc,marking  ] GC(47) Concurrent Cleanup for Next Mark
[2026-09-15T00:32:53.345+0300][5.384s][info][gc,marking  ] GC(47) Concurrent Cleanup for Next Mark 1.825ms
[2026-09-15T00:32:53.345+0300][5.384s][info][gc          ] GC(47) Concurrent Mark Cycle 9.339ms
[2026-09-15T00:32:53.415+0300][5.455s][info][gc,start    ] GC(48) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:53.415+0300][5.455s][info][gc,task     ] GC(48) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,phases   ] GC(48)   Pre Evacuate Collection Set: 0.34ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,phases   ] GC(48)   Merge Heap Roots: 0.05ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,phases   ] GC(48)   Evacuate Collection Set: 9.50ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,phases   ] GC(48)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,phases   ] GC(48)   Other: 0.10ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,heap     ] GC(48) Eden regions: 121->0(266)
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,heap     ] GC(48) Survivor regions: 27->19(19)
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,heap     ] GC(48) Old regions: 1091->1220
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,heap     ] GC(48) Humongous regions: 0->0
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,metaspace] GC(48) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:53.425+0300][5.465s][info][gc          ] GC(48) Pause Young (Prepare Mixed) (G1 Evacuation Pause) 1238M->1238M(2048M) 10.542ms
[2026-09-15T00:32:53.425+0300][5.465s][info][gc,cpu      ] GC(48) User=0.05s Sys=0.00s Real=0.01s
[2026-09-15T00:32:53.625+0300][5.665s][info][gc,start    ] GC(49) Pause Young (Mixed) (G1 Evacuation Pause)
[2026-09-15T00:32:53.625+0300][5.665s][info][gc,task     ] GC(49) Using 9 workers of 9 for evacuation
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,phases   ] GC(49)   Pre Evacuate Collection Set: 0.29ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,phases   ] GC(49)   Merge Heap Roots: 0.16ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,phases   ] GC(49)   Evacuate Collection Set: 14.98ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,phases   ] GC(49)   Post Evacuate Collection Set: 0.22ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,phases   ] GC(49)   Other: 0.10ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,heap     ] GC(49) Eden regions: 266->0(179)
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,heap     ] GC(49) Survivor regions: 19->36(36)
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,heap     ] GC(49) Old regions: 1220->1462
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,heap     ] GC(49) Humongous regions: 0->0
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,metaspace] GC(49) Metaspace: 331K(512K)->331K(512K) NonClass: 306K(384K)->306K(384K) Class: 24K(128K)->24K(128K)
[2026-09-15T00:32:53.641+0300][5.681s][info][gc          ] GC(49) Pause Young (Mixed) (G1 Evacuation Pause) 1504M->1497M(2048M) 16.084ms
[2026-09-15T00:32:53.641+0300][5.681s][info][gc,cpu      ] GC(49) User=0.09s Sys=0.01s Real=0.02s
[2026-09-15T00:32:53.693+0300][5.732s][info][gc,heap,exit] Heap
[2026-09-15T00:32:53.693+0300][5.733s][info][gc,heap,exit]  garbage-first heap   total 2097152K, used 1582016K [0x0000000780000000, 0x0000000800000000)
[2026-09-15T00:32:53.693+0300][5.733s][info][gc,heap,exit]   region size 1024K, 84 young (86016K), 36 survivors (36864K)
[2026-09-15T00:32:53.693+0300][5.733s][info][gc,heap,exit]  Metaspace       used 331K, committed 512K, reserved 1114112K
[2026-09-15T00:32:53.693+0300][5.733s][info][gc,heap,exit]   class space    used 24K, committed 128K, reserved 1048576K

```

За время работы:

## Результат запуска с ZGC

Лог

```text

```

За время работы:

## Сравнение

## Вывод
