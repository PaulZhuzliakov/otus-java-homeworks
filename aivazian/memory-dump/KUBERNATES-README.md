# Домашнее задание: Запуск SpringBoot приложения в Kubernetes с помощью Helm

## Подготовка docker образа
1. Собрать проект
> mvn clean install
2. Собрать docker образ
> docker build -t myapp:v1 .
3. запушить docker образ в registry
> docker push arshgu/myapp:v1

## Деплой приложения в kubernetes с помощью Helm
1. Установить кластер
> kind create cluster

Для проверки вывести список доступных кластеров
![img.png](img.png)

2. Генерируем базовый chart и удаляем ненужные template
> helm create my-app-chart
![img_1.png](img_1.png)

3. Редактируем values.yaml, deployment.yaml, service.yaml
4. Проверка генирации артефактов с помощью helm
   ![img_2.png](img_2.png)
   ![img_3.png](img_3.png)

5. Установка приложения
   ![img_4.png](img_4.png)
6. Проверим что приложение поднялось
   ![img_5.png](img_5.png)> kubectl logs deployment/my-app-chart
7. Для локального доступа пробрасываем порт 80
   ![img_6.png](img_6.png)
8. Чтобы достучаться до нового эндпоинта, теперь нужно сделать запрос
   ![img_7.png](img_7.png)http://localhost:80/hello

