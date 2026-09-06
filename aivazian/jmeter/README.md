# Домашнее задание по теме JMeter

## Тесты через UI интерфейс JMeter
В директории jmeter-ui расположен тестовый план CreateRequest.jmx, импортированный из UI
Также результаты прогона jmeter-ui/img.png

## Сделать тесты через отдельный подмодуль с библиотекой Jmeter
Аналогичные реализованы в модуле aivazian/jmeter

### Предусловие: 
установлена переменная среды JMETER_HOME

### Запуск
cd aivazian/jmeter
mvn clean package
java -Dthreads=100 -Dramp=10 -Dloop=5 -jar target\jmeter-1.0-SNAPSHOT-jar-with-dependencies.jar

Возможные параметры:
- host,
- port,
- endPoint,
- threads,
- ramp,
- loop,
- duration,
- resultPath

По умолчанию результат выгружается в [jmeter-results](jmeter-results)
Пример добавлен jmeter-results/result.jtl