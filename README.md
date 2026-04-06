# OOP 프로젝트 (Object-Oriented Programming Project)

본 프로젝트는 객체 지향 프로그래밍(OOP) 원칙과 클린 아키텍처/헥사고날 아키텍처를 기반으로 설계된 Kotlin 멀티 모듈 시스템입니다.

## 🛠 기술 스택 (Tech Stack)

- **Language**: Kotlin 1.9.24
- **Framework**: Spring Boot 3.2.3
- **Dependency Management**: Gradle Kotlin DSL
- **Java Version**: JDK 17
- **Test & Coverage**: JUnit 5, JaCoCo

---

## 🏗 전체 프로젝트 구조 (Project Structure)

프로젝트는 관심사 분리를 위해 다음과 같은 5개의 모듈로 구성되어 있습니다.

```text
OOP/
├── oop-boot           # 애플리케이션 시작점 및 전체 설정 (Spring Boot)
├── oop-presentation   # 인바운드 어댑터 (REST API, Controller, Exception Handler)
├── oop-application    # 비즈니스 로직 및 유즈케이스 (Service, Port)
├── oop-domain         # 핵심 비즈니스 모델 및 엔티티 (Pure Domain)
└── oop-infrastructure # 아웃바운드 어댑터 (Persistence, External API Implementation)
```

상세한 아키텍처 설계와 모듈 간의 연결 방식은 [시스템 아키텍처 가이드](./docs/architecture/system-overview.md)를 참조하세요.

---

## 🐘 Gradle 설정 및 공통 의존성 (Gradle Configuration)

루트 `build.gradle.kts`를 통해 모든 서브 프로젝트에 공통 설정을 적용하고 버전 관리를 수행합니다.

### 1. 공통 적용 플러그인 (Shared Plugins)
- `kotlin("jvm")`: Kotlin JVM 지원
- `kotlin("plugin.spring")`: Spring 프레임워크와의 호환성을 위한 Kotlin 플러그인
- `io.spring.dependency-management`: Spring Boot 의존성 버전 관리 자동화
- `jacoco`: 테스트 커버리지 리포트 생성

### 2. 전역 공통 의존성 (Global Dependencies)
모든 서브 모듈에 기본적으로 포함되는 라이브러리입니다:
- `kotlin-reflect`: 코틀린 리플렉션 지원
- `kotlin-stdlib-jdk8`: Kotlin 표준 라이브러리
- `jackson-module-kotlin`: JSON 직렬화/역직렬화를 위한 Kotlin 모듈
- `spring-boot-starter-test`: JUnit 5 기반의 테스트 프레임워크

### 3. JaCoCo 통합 리포트
루트 프로젝트에서 전 모듈의 테스트 결과를 통합하여 확인할 수 있도록 `jacocoRootReport` 태스크가 구성되어 있습니다.
```bash
./gradlew jacocoRootReport
```

---

## 🚀 실행 및 테스트 (Execution & Test)

### 애플리케이션 실행
```bash
./gradlew :oop-boot:bootRun
```

### 전체 테스트 실행 및 커버리지 확인
```bash
./gradlew test
./gradlew jacocoRootReport
```

---
*Last updated: 2026-03-30*
