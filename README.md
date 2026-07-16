# Habit Tracker

## Описание проекта

Habit Tracker — это REST API приложение для отслеживания пользовательских привычек.

Пользователь может:

- зарегистрироваться и авторизоваться;
- создавать привычки;
- получать список своих привычек;
- изменять и удалять привычки;
- отмечать выполнение привычек;
- просматривать статистику выполнения.

Основные возможности:

- JWT авторизация;
- разделение данных пользователей;
- хранение данных в PostgreSQL;
- миграции базы данных через Liquibase;
- документация API через Swagger;
- покрытие кода тестами.


## Технологии

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Liquibase
- Maven
- JUnit 5
- Mockito
- Swagger/OpenAPI


# Запуск проекта

## Требования

Для запуска необходимы:

- Java 17 или выше;
- Maven;
- Docker.


## Запуск PostgreSQL через Docker

База данных запускается отдельно через Docker.

Создание контейнера PostgreSQL:

```bash
docker run --name habit-postgres \
-e POSTGRES_DB=habit_tracker \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=secret \
-p 5432:5432 \
-d postgres
```

Проверить запущенные контейнеры:

```bash
docker ps
```


## Запуск приложения

Через Maven:

```bash
./mvnw spring-boot:run
```

или:

```bash
mvn spring-boot:run
```

После запуска приложение доступно:

```
http://localhost:8080
```


## Настройки базы данных

Конфигурация подключения:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/habit_tracker
    username: postgres
    password: secret
```

Миграции таблиц выполняются автоматически с помощью Liquibase.


## JWT настройка

В локальной разработке используется:

```yaml
jwt:
  secret: default-secret-for-local-dev-only
  expiration: 86400000
```

Для изменения JWT ключа можно использовать переменную окружения:

Linux/Mac:

```bash
export JWT_SECRET=my-secret-key
```

Windows:

```cmd
set JWT_SECRET=my-secret-key
```


# API Endpoints


## Авторизация


### Регистрация пользователя

```
POST /api/auth/register
```

Пример запроса:

```json
{
  "username": "alice",
  "password": "password123",
  "email": "alice@gmail.com"
}
```

Ответ:

```json
{
  "username": "alice"
}
```


### Авторизация пользователя

```
POST /api/auth/login
```

Пример запроса:

```json
{
  "username": "alice",
  "password": "password123"
}
```

Ответ:

```json
{
  "token": "jwt-token",
  "username": "alice"
}
```


# Работа с JWT

После успешной авторизации сервер возвращает JWT токен.

Для доступа к защищённым endpoint необходимо добавить заголовок:

```
Authorization: Bearer <token>
```

Пример:

```
GET /api/habits
```

Headers:

```
Authorization: Bearer eyJhbGciOiJIUzI1...
```


# Привычки


## Создание привычки

```
POST /api/habits
```

Headers:

```
Authorization: Bearer <token>
```

Body:

```json
{
  "name": "Пить воду",
  "description": "2 литра воды каждый день",
  "target": 7
}
```


## Получение всех привычек пользователя

```
GET /api/habits
```


## Получение привычки по ID

```
GET /api/habits/{id}
```


## Обновление привычки

```
PUT /api/habits/{id}
```

Body:

```json
{
  "name": "Пить воду",
  "description": "3 литра воды каждый день",
  "target": 10
}
```


## Удаление привычки

```
DELETE /api/habits/{id}
```


# Выполнение привычек


## Добавление записи о выполнении

```
POST /api/habits/{id}/records?completed=true
```

Пример:

```
POST /api/habits/1/records?completed=true
```


## Получение истории выполнений

```
GET /api/habits/{id}/records
```


# Статистика


## Статистика конкретной привычки

```
GET /api/habits/{id}/stats
```


## Дневная статистика

```
GET /api/stats/daily
```


## Статистика за неделю

```
GET /api/stats/week
```


# Swagger документация

После запуска приложения Swagger UI доступен:

```
http://localhost:8080/swagger-ui/index.html
```


# Тестирование

Запуск всех тестов:

```bash
./mvnw test
```

В проекте реализованы:

- unit-тесты сервисов с Mockito;
- тесты контроллеров через MockMvc;
- интеграционные тесты с Spring Context.