# static 자원 사용 현황 파악

## 개요

| 항목 | 내용 |
|------|------|
| 점검 목적 | 동시성 리팩토링을 위한 static 자원 위험 요소 파악 |
| 점검 범위 | 전체 레이어 (presentation, application, domain, infrastructure) |
| 점검 대상 | companion object 내 필드, object 선언, @JvmStatic |
| 점검 기준 | var 또는 val + 가변 컬렉션 → 위험 / const val, val + 불변 타입, fun → 안전 |

---

## 위험 판단 기준 근거

static 자원은 클래스 인스턴스가 아닌 **클래스 자체에 귀속**된다.
즉, 모든 스레드가 같은 메모리를 공유하므로 가변 상태가 있으면 Race Condition이 발생한다.

```kotlin
// companion object var — 위험
class PostService {
    companion object {
        var requestCount = 0              // 모든 스레드가 같은 변수를 읽고 씀
        val cache = mutableListOf<Post>() // val이어도 내부가 가변 → 위험
    }
}

// companion object const val + fun — 안전
class PostService {
    companion object {
        const val MAX_SIZE = 100          // 읽기만 가능, 쓰기 불가
        fun create(title: String) = ...   // 실행 후 반환, 상태 저장 없음
    }
}
```

> **"static = 위험"이 아니라 "static + 가변 상태 = 위험"이다**

---

## 위험도 기준

| 위험도 | 조건                                                                                            |
|--------|-----------------------------------------------------------------------------------------------|
| 위험 | `companion object` 또는 `object` 내 `var` 필드                                                     |
| 위험 | `companion object` 또는 `object` 내 `val` + `MutableList` / `MutableMap` 등 가변 컬렉션                |
| 위험 | `@JvmStatic` + 가변 상태                                                                          |
| 안전 | `const val` 상수                                                                                |
| 안전 | `fun` (정적 팩토리 메서드) <br/>(호출되면 실행하고 결과를 반환하고 끝이에요. 어딘가에 값을 저장하지 않음 -> 즉 실제로 저장만안하면 동시성 이슈가 없음) |
| 안전 | `val` + 불변 타입                                                                                 |

---

## 레이어별 점검 현황

### Presentation 레이어

| 클래스 | static 자원 | 위험도 |
|--------|-------------|--------|
| `ApiResponse` | `const val SUCCESS_CODE`, `fun success()`, `fun fail()` | 안전 |
| `GetPostResponse` | `fun from()` | 안전 |
| `CreatePostResponse` | `fun from()` | 안전 |
| `UpdatePostResponse` | `fun from()` | 안전 |
| `PageResponse` | `fun from()` | 안전 |
| `TokenResponse` | `fun of()` | 안전 |

### Application 레이어

| 클래스 | static 자원 | 위험도 |
|--------|-------------|--------|
| `CreatePostCommand` | `fun of()` | 안전 |
| `UpdatePostCommand` | `fun of()` | 안전 |
| `DeletePostCommand` | `fun of()` | 안전 |
| `GetPostQuery` | `fun from()` | 안전 |
| `GetPostListQuery` | `fun of()` | 안전 |
| `CreatePostResult` | `fun from()` | 안전 |
| `UpdatePostResult` | `fun from()` | 안전 |
| `GetPostResult` | `fun from()` | 안전 |
| `HealthCheckResult` | `fun from()` | 안전 |
| `LoginCommand` | `fun of()` | 안전 |
| `JoinCommand` | `fun of()` | 안전 |
| `RefreshCommand` | `fun of()` | 안전 |
| `TokenValidationCommand` | `fun of()` | 안전 |
| `LoginResult` | `fun of()` | 안전 |
| `RefreshResult` | `fun of()` | 안전 |
| `TokenValidationResult` | `fun of()` | 안전 |
| `PageResult` | `fun of()` | 안전 |

### Domain 레이어

| 클래스 | static 자원 | 위험도 |
|--------|-------------|--------|
| `Post` | `fun create()`, `fun reconstruct()` | 안전 |
| `PostTitle` | `const val MAX_LENGTH`, `fun from()` | 안전 |
| `PostContent` | `const val MAX_LENGTH`, `fun from()` | 안전 |
| `Health` | `fun ok()`, `fun reconstruct()` | 안전 |
| `User` | `fun reconstruct()`, `fun signUp()`, `fun login()` | 안전 |
| `UsernameVO` | `fun from()` | 안전 |
| `RawPasswordVO` | `fun from()` | 안전 |
| `EncodedPasswordVO` | `fun from()` | 안전 |
| `TokenVO` | `fun from()` | 안전 |

### Infrastructure 레이어

| 클래스 | static 자원 | 위험도 |
|--------|-------------|--------|
| `PostEntity` | `fun fromDomain()` | 안전 |
| `UserEntity` | `fun fromDomain()` | 안전 |
| `HealthEntity` | `fun fromDomain()` | 안전 |

---

## 점검 결과 요약

| 항목 | 건수 |
|------|------|
| 점검한 클래스 수 | 33개 |
| `companion object` | 33개 |
| `object` 선언 | 0개 |
| `@JvmStatic` | 0개 |
| **위험한 static 자원** | **0건** |
| 안전한 static 자원 | 33건 |

**위험한 static 자원이 없다.**
모든 `companion object`는 `const val` 상수 또는 정적 팩토리 메서드(`fun`)만 포함하고 있어 가변 상태가 전혀 없다.
