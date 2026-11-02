# 0005 — Spring Boot 4 instead of Spring Boot 3

**Status:** Accepted

## Context

The original plan named Spring Boot 3. When the project was generated, Spring Initializr offered only Spring Boot 4.x: the 3.x line has reached the end of its open-source support.

## Decision

Use **Spring Boot 4.1.2** (the current default) on Java 21.

## Consequences

- ✅ A supported version that receives security and bug fixes.
- ✅ Modular starters: the application only pulls in what it uses (`spring-boot-starter-webmvc`, and a matching test starter per feature).
- ❌ Several test annotations moved to new packages, and Jackson 3 replaces Jackson 2. Many online examples and AI suggestions still assume Boot 3, so every change is compiled and tested before it is committed.
