# @Transactional, Connection Pool, JPA Lock 설정 및 현황

## 개요

| 항목 | 내용 |
|------|------|
| 점검 목적 | 동시성 리팩토링을 위한 트랜잭션/커넥션/락 설정 현황 파악 |
| 점검 범위 | Application 레이어 (Service), Infrastructure 레이어 (Repository, Entity) |

| 항목 | 설정 요약 | 비고 |
|------|---------|------|
| @Transactional | 메서드 레벨 7건 (PostService 5, HealthService 1, JoinService 1) | 클래스 레벨 없음. readOnly 2건 적용 |
| Connection Pool | HikariCP (Spring Boot 기본값 전체 사용) | 명시적 설정 없음 |
| JPA Lock | @Version, @Lock 모두 미적용 | 낙관적/비관적 락 없음 |

---

## @Transactional 현황

### 점검 항목 설명

| 항목 | 의미 | 동시성과의 연결 |
|------|------|---------------|
| **선언 위치** (클래스 vs 메서드) | 클래스에 선언하면 모든 메서드에 적용, 메서드에 선언하면 그 메서드에만 적용 | 클래스 레벨이면 의도치 않게 넓은 범위에 트랜잭션이 걸려있을 수 있음 |
| **propagation** (전파 방식) | 트랜잭션이 이미 열려있을 때, 새로운 메서드 호출이 어떻게 합류할지 결정. `DEFAULT(=REQUIRED)` = 이미 있으면 합류, 없으면 새로 생성 | DEFAULT 외 값이 있으면 트랜잭션 중첩 구조가 있다는 뜻 — 락 범위가 복잡해짐 |
| **isolation** (격리 수준) | 다른 트랜잭션이 진행 중일 때, 중간 상태를 어디까지 볼 수 있는지 결정. `DEFAULT` = DB 기본값 (PostgreSQL은 READ COMMITTED = 커밋된 데이터만 읽기) | DEFAULT 외 값이 있으면 의도적으로 동시성을 제어하고 있다는 뜻 |
| **readOnly** | 이 트랜잭션이 읽기 전용인지 선언. `true`면 DB가 읽기 최적화 적용 + 실수로 쓰기 시 예외 | `false`인데 실제로 쓰기가 없으면 불필요하게 쓰기 락을 잡고 있는 것 |

> **점검 이유**: 현재 트랜잭션이 어떻게 설정되어 있는지 알아야, 나중에 Lock이나 isolation을 추가할 때 어디서 충돌이 생길지 예측할 수 있다.

---

### PostService

| 메서드 | 선언 위치 | propagation | isolation | readOnly |
|--------|----------|-------------|-----------|----------|
| `create` | 메서드 | DEFAULT | DEFAULT | false |
| `get` | 메서드 | DEFAULT | DEFAULT | true |
| `getList` | 메서드 | DEFAULT | DEFAULT | true |
| `update` | 메서드 | DEFAULT | DEFAULT | false |
| `delete` | 메서드 | DEFAULT | DEFAULT | false |

```kotlin
@Transactional
override fun create(command: CreatePostCommand): CreatePostResult { ... }

@Transactional(readOnly = true)
override fun get(command: GetPostQuery): GetPostResult { ... }

@Transactional(readOnly = true)
override fun getList(command: GetPostListQuery): PageResult<GetPostResult> { ... }

@Transactional
override fun update(command: UpdatePostCommand): UpdatePostResult { ... }

@Transactional
override fun delete(command: DeletePostCommand) { ... }
```

### HealthService

| 메서드 | 선언 위치 | propagation | isolation | readOnly |
|--------|----------|-------------|-----------|----------|
| `checkHealth` | 메서드 | DEFAULT | DEFAULT | false |

```kotlin
@Transactional
override fun checkHealth(): HealthCheckResult { ... }
```

### JoinService

| 메서드 | 선언 위치 | propagation | isolation | readOnly |
|--------|----------|-------------|-----------|----------|
| `join` | 메서드 | DEFAULT | DEFAULT | false |

```kotlin
@Transactional
override fun join(command: JoinCommand) { ... }
```

### @Transactional 미적용 서비스

| 서비스 | 비고 |
|--------|------|
| `LoginService` | @Transactional 없음 |
| `RefreshService` | @Transactional 없음 |
| `TokenValidationService` | @Transactional 없음 |

---

## Connection Pool 현황

### Pool 구현체

| 항목 | 값 |
|------|-----|
| Pool 구현체 | HikariCP (Spring Boot 3.2.3 기본값) |
| 명시적 설정 | 없음 — 전 항목 기본값 사용 |

### HikariCP 설정값

| 항목 | 현재 값 | 비고 |
|------|---------|------|
| `maximum-pool-size` | 10 (기본값) | 동시에 사용 가능한 최대 Connection 수 |
| `minimum-idle` | 10 (기본값) | 최소 유지 Connection 수 |
| `connection-timeout` | 30000ms (기본값) | Pool 고갈 시 대기 후 예외 발생까지 시간 |
| `idle-timeout` | 600000ms (기본값) | 미사용 Connection 반환까지 시간 (10분) |
| `max-lifetime` | 1800000ms (기본값) | Connection 최대 유지 시간 (30분) |

### 설정 파일 위치

```
oop-boot/src/main/resources/
├── application.yml          # HikariCP 설정 없음
├── application-local.yml    # HikariCP 설정 없음
├── application-dev.yml      # HikariCP 설정 없음
└── application-prod.yml     # HikariCP 설정 없음
```

---

## JPA Lock 현황

### 낙관적 락 (@Version)

| Entity | @Version 필드 | 적용 여부 |
|--------|--------------|----------|
| `PostEntity` | 없음 | 미적용 |
| `UserEntity` | 없음 | 미적용 |
| `HealthEntity` | 없음 | 미적용 |

### 비관적 락 (@Lock)

| Repository | @Lock 메서드 | 적용 여부 |
|-----------|-------------|----------|
| `PostJpaRepository` | 없음 | 미적용 |
| `UserJpaRepository` | 없음 | 미적용 |
| `HealthJpaRepository` | 없음 | 미적용 |

### Entity 코드 현황

```kotlin
// PostEntity — @Version 없음
@Entity
@Table(name = "posts")
class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
)

// PostJpaRepository — @Lock 없음
interface PostJpaRepository : JpaRepository<PostEntity, Long> {
    fun findPostEntityById(id: Long): PostEntity?
}
```

---

## 점검 결과 요약

| 항목 | 건수 |
|------|------|
| @Transactional 적용 메서드 수 | 7건 |
| readOnly = true 메서드 수 | 2건 (PostService.get, PostService.getList) |
| DEFAULT 외 propagation | 0건 |
| DEFAULT 외 isolation | 0건 |
| 명시적 HikariCP 설정 | 0건 |
| @Version 필드 | 0건 |
| @Lock 어노테이션 | 0건 |
