# Базовое REST приложение для работы с пользователями
Предоставляет функционал CRUD операций (на данный момент реализовано create и read)
1. Создание пользователя
HOST: localhost:8080
endpoint: api/users
method: POST
body: {"login": "user1","password": "123456789","algorithm": "SHA-256"}
Варианты algorithm, которые поддерживаются на данный момент:
 - MD5
 - SHA-256
 - SHA-512
2. Получение информации по пользователю
HOST: localhost:8080
endpoint: api/users
method: POST
params: id={id}, например api/users?id=1