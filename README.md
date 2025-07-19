<h1 align="center">inventory-management</h1>

<p align="center">
  <a href="https://github.com/zapolyarnydev/inventory-management/actions/workflows/run-tests.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/zapolyarnydev/inventory-management/run-tests.yml?style=flat&label=Unit%20%26%20integration%20tests" alt="Unit & integration test status"/>
  </a>
</p>

## 🌐 Language

- [English](README.md)
- [Русский](README.ru.md)

## About the app
A microservices-based application built with Spring Boot and Kafka for inventory management and order processing.

## Technologies
- Spring boot
- PostreSQL
- Hibernate
- Kafka
- Docker
- JUnit 5 & Mockito
- Testcontainers
- GitHub Workflows

## Functionality
The system consists of two main services: Inventory and Orders.
- Inventory service:
  - Register and delete inventory items
  - Increase and decrease items quantity
- Order service:
  - Place orders based on available inventory
  - Publish order events to Kafka topics

## App Architecture
<p align="center">
  <img src="sources-for-readme/invmanagement.png" alt="App architecture" width="800"/>
</p>

## License

Copyright (c) 2025 ZapolyarnyDev

This project is licensed under the MIT License – see the LICENSE file for details.
