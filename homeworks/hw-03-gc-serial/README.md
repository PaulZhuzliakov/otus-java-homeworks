# hw-03-gc-serial

Запустил Practice с SerialGC на куче 128m, подобирал параметры так, чтобы не
было Full GC.

## Шаг 1 - дефолтные параметры (gc-step1.log)

По дефолту ловит Full GC - в логе их 12 за 30 секунд (например,
`GC(15): 111M->9M(123M) 57,364ms`). После minor GC выживает 20 МБ объектов
(`51M->20M`), а survivor при дефолте всего 4 МБ (`from space 4352K`). Лишнее
уходит в old, тот переполняется - и случается Full GC.

## Шаг 2 - `-XX:NewRatio=1` (gc-step2.log)

Сначала пробовал `-XX:NewRatio=1` (увеличить young) - думал, раз survivor
часть young, он заодно тоже подрастёт. Но почти не помогло: Full GC всё равно
есть (9 штук). Survivor хоть и подрос до 6 МБ (`from space 6528K`), но
выживает-то по-прежнему ~20 МБ - 20 в 6 не влезают так же, как раньше в 4.

## Шаг 3 - `-XX:SurvivorRatio=1` (gc-step3.log)

Помогло `-XX:SurvivorRatio=1` - он и задаёт размер survivor: отношение eden к
survivor (по дефолту 8, т.е. eden в 8 раз больше survivor). При 1 eden и
survivor равны, а так как survivor'ов всегда два (from и to), young делится на
три равные части - survivor вырастает до ~21 МБ (`from space 21824K`). Теперь
20 МБ выживших в него влезают, в old почти ничего не уходит, и Full GC
пропадают совсем.

## Ответ

```
-XX:+UseSerialGC -Xms128m -Xmx128m -XX:NewRatio=1 -XX:SurvivorRatio=1
```
