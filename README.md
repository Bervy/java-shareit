---

# *Java-shareit*

Описание проекта
-
Приложение предоставляющее возможность сдавать в аренду свои предметы и арендовать предметы у других пользователей.

Использованные технологии:
-

- Java 11,Maven, Spring-Boot, Hibernate, Postgresql, Lombok, Docker, Jpa, AOP

Функционал приложения:
-

1. ### Проект реализован по микро-сервисной архитектуре:
    * gateway - валидация входящих в запрос данных
    * server - обработка запроса и возвращение ответа

2. ### Основной функционал:

    * Создавать\редактировать\получать\удалять пользователя
    * Создавать\редактировать\получать\удалять предмет пользователем
    * создавать\удалять комментарии
    * Создавать\редактировать статус бронирования владельцем предмета\получать информацию о бронировании предмета
    * создавать\получать информацию о запросах на бронирование предмета

Инструкция по запуску:
-

1. Чтобы запустить сервисы по отдельности (через main) нужна запущенная бд Postgres. С помощью pgAdmin4 создайте базу
   данных:
   Необходимо создать базу данных postgreSQL _**shareit**_:
    * POSTGRES_USER = postgres
    * POSTGRES_PASSWORD = 123
    * POSTGRES_DB = shareit

2. Для запуска проекта потребуется docker.
3. Сначала собираем проект mvn clean package.
4. Команда "docker-compose up" запускает оба сервиса с бд