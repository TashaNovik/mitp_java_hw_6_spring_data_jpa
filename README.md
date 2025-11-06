Приложение успешно переведено с **Spring JDBC** на **Spring Data JPA (Hibernate)**.

## Как запустить

### Запуск с H2 (in-memory база данных)
```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

Затем откройте в браузере:
- Приложение: http://localhost:8080/api/messages
- H2 Console: http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

### Запуск с PostgreSQL
```bash
./gradlew bootRun
```

**Требования**: PostgreSQL должен быть запущен на `localhost:5432`

## Тестирование

### Запуск всех тестов
```bash
./gradlew test
```

### Проверка N+1
```bash
# Запустите приложение
./gradlew bootRun --args='--spring.profiles.active=h2'

# В другом терминале:
curl http://localhost:8080/api/messages/nplus1
# Смотрите логи - должно быть много SQL запросов

curl http://localhost:8080/api/messages/optimized
# Смотрите логи - должен быть ОДИН SQL запрос с JOIN
```

### Проверка LazyInitializationException
```bash
curl http://localhost:8080/api/messages/fail/1
# Вернет ошибку 500

curl http://localhost:8080/api/messages/success/1
# Вернет корректный JSON с данными
```

---

## Структура проекта

```
src/main/java/com/example/hellospring/
├── config/
│   └── JacksonConfig.java          # Конфигурация Jackson для Hibernate
├── controller/
│   └── MessageController.java      # REST API endpoints
├── dto/
│   ├── CreateMessageRequest.java   # DTO для создания сообщения
│   └── MessageResponse.java        # DTO для ответа API
├── exception/
│   └── MessageNotFoundException.java
├── model/
│   ├── Message.java                # JPA сущность
│   └── User.java                   # JPA сущность
├── repository/
│   ├── MessageRepository.java      # Spring Data JPA репозиторий
│   └── UserRepository.java         # Spring Data JPA репозиторий
├── service/
│   └── MessageService.java         # Бизнес-логика
└── DemoApplication.java            # Главный класс

src/main/resources/
├── application.properties           # Основная конфигурация
├── application-h2.properties        # Профиль H2
├── application-mysql.properties     # Профиль MySQL
└── application-postgresql.properties # Профиль PostgreSQL
```

---
