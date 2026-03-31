# OOP

## Local 개발 환경 셋팅 가이드

---

### Quick Start

```bash
git clone <repository-url>
cd oop2
docker compose up -d
./gradlew clean build
./gradlew :app:bootRun
```

→ http://localhost:8080 접속 확인

---

### 1. 사전 요구 사항 (Prerequisites)

| 항목 | 버전 | 설치 확인 |
|---|---|---|
| JDK | 21 | `java --version` |
| Docker | Desktop 최신 | `docker --version` |

> Kotlin(2.2.21), Gradle(9.4.0)은 프로젝트에 포함된 Wrapper가 자동 관리하므로 별도 설치 불필요

---

### 2. 프로젝트 복제 (Clone)

```bash
git clone <repository-url>
cd oop2
```

---

### 3. 환경 변수 및 설정 (Configuration)

`compose.yaml`(DB 생성)과 `application.yml`(DB 연결)의 값이 **반드시 일치**해야 합니다.

| 항목 | 값 | compose.yaml | application.yml |
|---|---|---|---|
| DB명 | `oop` | `POSTGRES_DB` | `datasource.url` |
| 사용자 | `oop` | `POSTGRES_USER` | `datasource.username` |
| 비밀번호 | `oop1234` | `POSTGRES_PASSWORD` | `datasource.password` |

> 현재는 기본값이 설정되어 있어 별도 수정 없이 바로 실행 가능합니다.
> 값을 변경하려면 **두 파일 모두** 수정해야 합니다.

#### 주요 설정 파일

| 파일 | 역할 |
|---|---|
| `compose.yaml` | PostgreSQL 컨테이너 설정 (DB 생성) |
| `app/src/main/resources/application.yml` | Spring Boot 설정 (DB 연결) |
| `gradle/libs.versions.toml` | 의존성 버전 카탈로그 |

---

### 4. 인프라 실행 (Infrastructure Setup)

> ⚠️ **반드시 빌드/실행 전에 먼저 수행해야 합니다.** DB가 없으면 애플리케이션이 기동되지 않습니다.

Docker Desktop이 **실행 중**인 상태에서:

```bash
docker compose up -d
```

정상 실행 확인:

```bash
docker ps
```

```
CONTAINER ID   IMAGE                  STATUS    PORTS                    NAMES
xxxxxxxxxxxx   postgres:17-alpine     Up ...    0.0.0.0:5432->5432/tcp   oop-postgres
```

컨테이너 종료:

```bash
docker compose down        # 컨테이너 중지 (데이터 유지)
docker compose down -v     # 컨테이너 중지 + 데이터 초기화
```

---

### 5. 빌드 및 실행 (Build & Run)

빌드:

```bash
./gradlew clean build
```

```
BUILD SUCCESSFUL in Xs
```

실행:

```bash
./gradlew :app:bootRun
```

```
Started OopApplication in X.XXX seconds
```

접속 확인:

```bash
curl http://localhost:8080
```

---

### 6. 테스트 실행 (Running Tests)

```bash
./gradlew test                    # 전체 테스트
./gradlew :domain:test            # 특정 모듈 테스트
```

- 테스트 프레임워크: **JUnit 5** (JUnit Platform)
- 테스트 리포트: `{모듈}/build/reports/tests/test/index.html`

---

### 7. 프로젝트 구조 (Project Structure)

```
oop2/
├── domain/           # 엔티티, 비즈니스 규칙 (의존성 없음)
├── application/      # 유스케이스, 서비스 (domain만 의존)
├── infrastructure/   # DB 구현체, 외부 연동 (domain만 의존)
├── presentation/     # Controller, API (application만 의존)
├── app/              # Composition Root — 모든 모듈 조립 & 진입점
├── compose.yaml      # PostgreSQL 컨테이너 설정
└── build.gradle.kts  # 루트 빌드 설정
```

```
presentation → application → domain ← infrastructure
                                ↑
                               app (모든 모듈 조립)
```

---

### 8. 코딩 컨벤션 (Coding Standards)

- [Kotlin 공식 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html) 준수
- Kotlin 컴파일러 옵션: `-Xjsr305=strict` (JSR-305 null-safety 엄격 적용)

---

### 9. Troubleshooting

| 증상 | 원인 | 해결 |
|---|---|---|
| `Unable to determine Dialect without JDBC metadata` | PostgreSQL 컨테이너가 실행되지 않음 | `docker compose up -d` 실행 |
| `Port 5432 already in use` | 다른 프로세스가 5432 포트 사용 중 | `lsof -i :5432`로 확인 후 종료 |
| `docker: command not found` | Docker Desktop 미설치 또는 미실행 | Docker Desktop 설치 및 실행 |
| `Unsupported class file major version 65` | JDK 버전 불일치 | JDK 21 설치 후 `java --version` 확인 |
| 빌드 실패 (원인 불명) | Gradle 캐시 문제 | `./gradlew clean build --no-build-cache` |
