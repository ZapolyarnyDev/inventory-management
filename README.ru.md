<h1 align="center">inventory-management</h1>

<p align="center">
  <a href="https://github.com/zapolyarnydev/inventory-management/actions/workflows/run-tests.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/zapolyarnydev/inventory-management/run-tests.yml?style=flat&label=Unit%20%26%20integration%20tests" alt="Unit & integration test status"/>
  </a>
</p>

## 🌐 Язык

- [English](README.md)
- [Русский](README.ru.md)

## О приложении
Микросервисное приложение на базе Spring Boot и Kafka для управления запасами склада и оформления заказов.

## Технологии
- Spring Boot  
- PostgreSQL  
- Hibernate  
- Apache Kafka  
- Docker  
- JUnit 5 и Mockito  
- Testcontainers  
- GitHub Workflows  

## Функционал
Приложение состоит из двух основных микросервисов: **Inventory** и **Orders**.
- **Сервис склада**:
  - Регистрация и удаление товаров  
  - Увеличение и уменьшение количества на складе  
- **Сервис заказов**:
  - Оформление заказов по доступным товарам  
  - Публикация событий заказов в Kafka  

## Архитектура приложения
<p align="center">
  <img src="sources-for-readme/invmanagement.png" alt="Архитектура приложения" width="800"/>
</p>

## Лицензия

Copyright (c) 2025 Zapolyarny

Проект распространяется под лицензией MIT — см. файл [LICENSE](LICENSE) для подробностей.
