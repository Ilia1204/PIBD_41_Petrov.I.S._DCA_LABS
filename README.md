# Распределённые вычисления и приложения (DCA)

Петров И. С., группа ПИБД-41. **Вариант 22** — учёт дисков в прокате.

> Ведётся учёт дисков в прокате. Есть возможность выдать диск на руки, принять
> новый диск в прокат, получить диск обратно. Отчёт: сколько дисков сейчас на
> руках, а сколько в прокате.

## Структура репозитория

| Путь | Что это |
|------|---------|
| `disk-service/` | Основной сервис (Spring Boot 3, Gradle). CRUD по дискам + отчёт. |
| `docker-compose.yml` | Поднимает PostgreSQL и сервис в одной docker-сети. |

Каждая лабораторная делается в отдельной ветке (`lab-1`, `lab-2`, …) и после
проверки вливается в `main` через Pull Request.

## Лабораторная работа 1. Основной сервис

Одна таблица `disk`, миграции через Liquibase, CRUD-операции предметной области,
Swagger UI. Сервис умеет работать и локально, и целиком в Docker.

### Вариант А. Всё в Docker

```bash
docker compose up -d --build
```

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- Health: http://localhost:8081/actuator/health

Остановить: `docker compose down` (с удалением данных БД — `docker compose down -v`).

### Вариант Б. БД в Docker, сервис из IDE / командной строки

```bash
docker compose up -d postgres
cd disk-service
./gradlew bootRun
```

Настройки подключения по умолчанию (`disk-service/src/main/resources/application.yml`)
уже указывают на `localhost:5432`, база `disk_rental`, пользователь `disk` / `disk`.

## API

| Метод | Путь | Действие |
|-------|------|----------|
| `GET` | `/api/disks` | Список дисков (постранично) |
| `GET` | `/api/disks/{id}` | Диск по id |
| `POST` | `/api/disks` | Принять новый диск в прокат |
| `PUT` | `/api/disks/{id}` | Отредактировать информацию о диске |
| `POST` | `/api/disks/{id}/issue` | Выдать диск на руки (`{"holderName": "..."}`) |
| `POST` | `/api/disks/{id}/return` | Получить диск обратно |
| `DELETE` | `/api/disks/{id}` | Удалить запись |
| `GET` | `/api/disks/report` | Отчёт: на руках / в прокате / всего |

## Требования к машине

Только **Docker** (для варианта А) или дополнительно **JDK 21** (для варианта Б).
Gradle ставить не нужно — используется wrapper `./gradlew`.
