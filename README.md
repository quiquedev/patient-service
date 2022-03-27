# Patients Service

The purpose of the code found in this repository is to get to know a bit about me as a software developer. This is not
a specific solution to a problem since there are not requirements.

## About
The simple implementation of a very simple REST api specified [here](openapi.yaml) by using Spring Boot, Kotlin and 
[jOOQ](https://www.jooq.org) in a functional way with the help of 
[Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html).
[Testcontainers](https://www.testcontainers.org) are used for dockerized test suites.

## How to run it
Run from the project root the following commands:

1. make create-local-env
2. make run
3. make destroy-local-env