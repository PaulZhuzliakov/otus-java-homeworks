# hw-11-nio-offheap

Хранение данных в off-heap. Две реализации одного интерфейса. Первая
выделяет память процесса вне java-кучи (ByteBuffer.allocateDirect) и
копирует файл туда. Вторая файл не копирует, а отображает его в память
процесса (mmap) - чтение буфера идёт из самого файла. Пользователь вводит
размер хранилища и путь к файлу. Если файла нет - исключение.

## Запуск

```
mvn clean package
java -cp target/classes hw11.Main
```

Демо-файл - `demo/1000bytes.txt`, ровно 1000 байт. Ввожу размер буфера
тоже 1000 - файл влезает впритык.

создание хранилищ:

```
размер off-heap хранилища (байт): 1000

пулы до создания хранилищ:
  pool mapped: count=0, used=0 байт
  pool direct: count=0, used=0 байт
  pool mapped - 'non-volatile memory': count=0, used=0 байт

пулы после создания ByteBufferStorage:
  pool mapped: count=0, used=0 байт
  pool direct: count=1, used=1000 байт
  pool mapped - 'non-volatile memory': count=0, used=0 байт

пулы после создания MappedByteBufferStorage:
  pool mapped: count=0, used=0 байт
  pool direct: count=1, used=1000 байт
  pool mapped - 'non-volatile memory': count=0, used=0 байт
```

загрузка файла:

```
файл (пустая строка - выход): demo/1000bytes.txt
загружено 1000 байт

пулы после загрузки файла:
  pool mapped: count=1, used=1000 байт
  pool direct: count=1, used=1000 байт
  pool mapped - 'non-volatile memory': count=0, used=0 байт
```

файла нет:

```
файл (пустая строка - выход): demo/нет-такого.txt
файл не найден: demo/нет-такого.txt

файл (пустая строка - выход):
```

Ввёл 1000 - direct pool вырос ровно на 1000: allocateDirect берёт память
сразу, при создании хранилища. После создания MappedByteBufferStorage ничего
не изменилось - он память берёт только при загрузке. Загрузил файл -
появился mapped pool на 1000, direct не тронут: файл читался внутрь уже
выделенного буфера. На несуществующий файл программа не падает - говорит
"файл не найден" и спрашивает следующий.

## Как я проверял, что данные вне кучи

Точный учёт off-heap буферов ведут пулы - снимаю их у JVM через
ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class): direct вырос
ровно на размер буфера, mapped - на размер файла, и ровно столько же данных
загружено. Значит, данные лежат вне кучи.

## Лимит direct-памяти

Лимит задаётся флагом -XX:MaxDirectMemorySize. Если он меньше буфера, allocateDirect кидает OutOfMemoryError
