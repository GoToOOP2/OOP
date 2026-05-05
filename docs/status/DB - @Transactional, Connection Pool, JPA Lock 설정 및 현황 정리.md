# DB - @Transactional, Connection Pool, JPA Lock 설정 및 현황 정리

- 대분류: 배움 | 소분류: 아키텍처 | 1주차

## @Transactional 현황

| Service | @Transactional | readOnly |
|---|---|---|
| JoinService | O | X |
| HealthService | O | X |
| PostService.create/update/delete | O | X |
| PostService.get/getList | O | O (readOnly=true) |
| LoginService | X | - |
| RefreshService | X | - |
| TokenValidationService | X | - |

**설계 포인트**
- Login/Refresh/TokenValidation은 DB 안 건드리고 JWT만 검증 → 트랜잭션 불필요, 정상
- Repository/Adapter 계층에는 @Transactional 없음 → Service 계층에서만 관리, 아키텍처상 올바름
- propagation, isolation 명시 없음 → 전부 기본값 사용 (REQUIRED, DB 기본 격리 수준)

---

## Connection Pool

**전 환경 모두 HikariCP 명시 설정 없음 → Spring Boot 기본값 그대로**

| 항목 | 기본값 |
|---|---|
| maximum-pool-size | 10 |
| minimum-idle | 10 |
| connection-timeout | 30초 |
| idle-timeout | 10분 |
| max-lifetime | 30분 |

| 환경 | 파일 | HikariCP 명시 설정 |
|---|---|---|
| 로컬 | application-local.yml | X (Spring Boot 기본값) |
| 개발 | application-dev.yml | X (Spring Boot 기본값) |
| 운영 | application-prod.yml | X (Spring Boot 기본값) |
| 테스트 | application-test.yml | X (H2 메모리 DB) |

**주의:** 운영(prod)도 기본값이라 트래픽 많아지면 pool 부족 가능성 있음

---

## JPA Lock 설정

| 항목 | 현황 |
|---|---|
| `@Version` (Optimistic Lock) | X - PostEntity, UserEntity, HealthEntity 전부 없음 |
| `@Lock` (Pessimistic Lock) | X - 모든 Repository 메서드 없음 |

**JPA 환경별 설정**

| | 로컬 | 운영 | 테스트 |
|---|---|---|---|
| ddl-auto | update | validate | create-drop |
| show-sql | true | false | true |
| dialect | PostgreSQL 명시 | 자동 감지 | H2 명시 |

- 운영에서 `ddl-auto: validate` → 스키마 변경 막고 검증만 하는 올바른 설정

---

## 종합 평가

- 트랜잭션 구조는 깔끔하지만, Lock이 전혀 없고 Connection Pool도 기본값이라 실제 운영 트래픽에는 취약한 상태
