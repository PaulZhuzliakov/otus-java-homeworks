# JDK Tools: keytool, jarsigner и jlink

## Цель работы

Выпустить самоподписанный сертификат, подписать им JAR-файл, проверить целостность подписи.
Cобрать облегчённый Java Runtime и запустить с его помощью приложение.

## Создание сертификата

Самоподписанный сертификат и закрытый ключ были созданы в хранилище PKCS12:

```bash
keytool -genkeypair \
  -alias homework-signer \
  -keyalg RSA \
  -keysize 3072 \
  -sigalg SHA256withRSA \
  -validity 365 \
  -storetype PKCS12 \
  -keystore certificates/private/jar-signing.p12 \
  -dname "CN=Homework JAR Signer, OU=Java Advanced, O=Training, L=Tallinn, C=EE" \
  -ext KeyUsage=digitalSignature \
  -ext ExtendedKeyUsage=codeSigning
```

Сертификат был проверен командой:

```bash
keytool -list -v \
  -storetype PKCS12 \
  -keystore certificates/private/jar-signing.p12 \
  -alias homework-signer
```

Результат:

```
Alias name: homework-signer
Creation date: 14 Oct 2026
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=Homework JAR Signer, OU=Java Advanced, O=Training, L=Tallinn, C=EE
Issuer: CN=Homework JAR Signer, OU=Java Advanced, O=Training, L=Tallinn, C=EE
Serial number: d17f4a6b75320412
Valid from: Wed Oct 14 23:11:15 EEST 2026 until: Thu Oct 14 23:11:15 EEST 2027
Certificate fingerprints:
         SHA1: BB:54:A8:64:E6:C2:88:E8:D3:F8:D0:F9:96:4D:E5:1E:30:33:56:06
         SHA256: 69:45:2B:F6:8B:81:94:99:67:03:D7:3E:9C:14:94:1C:71:E2:DE:C7:BC:3F:38:0F:CC:57:D9:25:41:49:DC:11
Signature algorithm name: SHA256withRSA
Subject Public Key Algorithm: 3072-bit RSA key
Version: 3

Extensions: 

#1: ObjectId: 2.5.29.37 Criticality=false
ExtendedKeyUsages [
  codeSigning
]

#2: ObjectId: 2.5.29.15 Criticality=false
KeyUsage [
  DigitalSignature
]

#3: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 6F DB EC DD 40 96 5A C0   43 5C A6 E2 A9 51 1B 50  o...@.Z.C\...Q.P
0010: 15 BA 4C 38                                        ..L8
]
]
```

## Подписание JAR

JAR-файл был подписан созданным ключом:

```bash
jarsigner \
  -keystore certificates/private/jar-signing.p12 \
  -storetype PKCS12 \
  -signedjar target/web-service-with-login-1.0-SNAPSHOT-signed.jar \
  -digestalg SHA-256 \
  -sigalg SHA256withRSA \
  target/web-service-with-login-1.0-SNAPSHOT.jar \
  homework-signer
```

Результат:

```
jar signed.

Warning: 
The signer's certificate is self-signed.
```

После подписания в `META-INF` появились файлы подписи:

```text
META-INF/MANIFEST.MF
META-INF/HOMEWORK.SF
META-INF/HOMEWORK.RSA
```

## Проверка подписи

Проверка подписанного файла завершилась успешно:

```bash
jarsigner -verify \
  target/web-service-with-login-1.0-SNAPSHOT-signed.jar
```

```text
jar verified.
```

Для проверки целостности была создана копия JAR и изменён файл `BOOT-INF/classes/application.properties`. После изменения верификация завершилась ошибкой:

```bash
jarsigner -verify \
  target/web-service-with-login-1.0-SNAPSHOT-changed.jar
```

```text
jarsigner error: java.lang.SecurityException: SHA-256 digest error for BOOT-INF/classes/application.properties
```

## Работа с jlink

Для эксперимента было создано простое приложение с единственным классом `Main`: 

```bash
jar --list \
  --file target/jdk-tools-1.0-SNAPSHOT.jar \
  | grep '\.class$'
```

```text
org/java/expert/jdktools/Main.class
```

### Сборка Java Runtime

Для нашего простого приложения достаточно базового модуля `java.base`, кастомный runtime был собран следующей командой:

```bash
jlink \
  --add-modules java.base \
  --output target/custom-jre \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress=zip-9
```

### Размер и состав Runtime

Размер созданного Runtime был проверен:

```bash
du -sh target/custom-jre
```

```text
68M    target/custom-jre
```

Список включённых модулей:

```bash
target/custom-jre/bin/java --list-modules
```

```text
java.base@25.0.4.1.1
jdk.internal.vm.ci@25.0.4.1.1
```

### Запуск программы

```bash
target/custom-jre/bin/java \
  -jar target/jdk-tools-1.0-SNAPSHOT.jar
```

```text
this is the main
```

## Вывод

С помощью `keytool` и `jarsigner` были выполнены выпуск сертификата, подписание JAR и проверка его целостности. 
С помощью `jlink` был создан специализированный Runtime размером 68 МБ, содержащий только необходимые модули. 
