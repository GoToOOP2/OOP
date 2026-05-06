# CRUD 동작별 Race Condition 발생 가능 경로 분석

> 작성일: 2026-05-06  
> 대상 프로젝트: OOP 게시판 CRUD  
> 문서 책임: 현재 코드에서 Race Condition이 발생할 수 있는 경로를 식별한다. 해결 방안은 다루지 않는다.

---

## Race Condition이란?

두 개 이상의 스레드 또는 프로세스가 공유 자원에 동시에 접근할 때, **실행 순서에 따라 결과가 달라지는 상태**를 말한다.

서버 애플리케이션에서는 주로 DB row가 공유 자원이 되며, 여러 요청이 동시에 같은 row를 읽고 수정할 때 발생한다.

### Race Condition의 대표 패턴

| 패턴 | 설명 |
|---|---|
| **Lost Update** | A와 B가 같은 데이터를 읽고 각각 수정 → 한쪽의 수정이 덮어써져 유실 |
| **Check-then-Act** | 조건을 확인한 뒤 행동하는 사이에 다른 스레드가 상태를 바꿈 |
| **Dirty Read** | 아직 커밋되지 않은 데이터를 다른 트랜잭션이 읽음 |
| **Non-Repeatable Read** | 같은 트랜잭션 안에서 같은 row를 두 번 읽었는데 값이 달라짐 |
| **Phantom Read** | 조회 중 다른 트랜잭션이 데이터를 추가/삭제해 같은 조건의 조회 결과가 달라짐 |
| **ABA Problem** | 값이 A → B → A로 바뀌었는데 변경이 없었던 것처럼 보임 |
| **Write Skew** | 두 트랜잭션이 각각 다른 row를 읽고 수정하는데, 합쳐보면 제약을 위반 |

이 프로젝트에서 식별된 패턴은 **Lost Update**, **Check-then-Act**, **Phantom Read**이다. 나머지 패턴은 현재 코드 구조상 발생 경로가 없다.

- **Dirty Read**: Spring의 `@Transactional` 기본 격리 수준(`READ COMMITTED`)에서는 커밋된 데이터만 읽으므로 발생하지 않는다.
- **Non-Repeatable Read**: 현재 코드에서 같은 row를 한 트랜잭션 안에서 두 번 읽는 흐름이 없다.
- **ABA Problem**: 현재 코드에서 이전 값과 현재 값을 비교하는 CAS(Compare-And-Swap) 로직이 없다.
- **Write Skew**: 현재 코드에서 서로 다른 row를 읽고 교차 수정하는 트랜잭션 구조가 없다.

---

## Race Condition 후보 식별 체크리스트

아래 항목을 순서대로 확인해 Race Condition 발생 가능 경로를 식별한다.

| # | 점검 항목 | 위험 신호 |
|---|---|---|
| 1 | write 메서드에 `@Transactional`이 있는가 | 없으면 원자성 보장 없음 |
| 2 | 트랜잭션 격리 수준이 명시되어 있는가 | 명시 없으면 DB 기본값(`READ COMMITTED`) 적용 |
| 3 | 조회 후 수정·저장하는 흐름(Read-Modify-Write)이 있는가 | 있으면 Lost Update 후보 |
| 4 | Entity에 `@Version` 필드가 있는가 | 없으면 낙관적 락 불가 |
| 5 | Repository에 `@Lock`이 있는가 | 없으면 비관적 락 불가 |
| 6 | 조건 확인 후 insert/update하는 흐름(Check-then-Act)이 있는가 | 있으면 중복 처리 후보 |
| 7 | 한 트랜잭션 안에서 쿼리가 두 번 이상 분리 실행되는가 | 있으면 Phantom Read 후보 |
| 8 | DB 유니크 제약 위반 예외(`DataIntegrityViolationException`)를 처리하는가 | 없으면 500 에러 노출 |

---

## CRUD 동작별 분석

### POST /posts — 게시글 생성

```kotlin
// PostService.kt
@Transactional
override fun create(command: CreatePostCommand): CreatePostResult {
    val post = Post.create(command.title, command.content, command.authorUsername)
    return CreatePostResult.from(postPort.store(post))
}
```

조회 없이 바로 INSERT하는 구조다. `GenerationType.IDENTITY`로 DB auto-increment가 id 중복을 방지하며, INSERT 자체는 DB가 원자적으로 처리한다. Read-Modify-Write 패턴이 없으므로 Race Condition 후보가 아니다.

**결론: Race Condition 발생 가능성 없음**

---

### GET /posts/:id — 게시글 단건 조회

```kotlin
// PostService.kt
@Transactional(readOnly = true)
override fun get(command: GetPostQuery): GetPostResult {
    val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
    return GetPostResult.from(post)
}
```

`readOnly = true`로 write가 없다. 공유 상태를 변경하지 않으므로 Race Condition 후보가 아니다.

**결론: Race Condition 발생 가능성 없음**

---

### GET /posts — 게시글 목록 조회

```kotlin
// PostService.kt
@Transactional(readOnly = true)
override fun getList(command: GetPostListQuery): PageResult<GetPostResult> {
    val posts = postPort.getAll(command.page, command.size, command.direction)  // 쿼리 1
    val total = postPort.countAll()                                              // 쿼리 2
    return PageResult.of(posts.map { GetPostResult.from(it) }, total, command.page, command.size)
}
```

`getAll()`과 `countAll()`이 **별개의 쿼리로 분리**되어 실행된다. 두 쿼리 사이에 다른 트랜잭션이 게시글을 추가하면 목록과 total이 불일치하는 Phantom Read가 발생할 수 있다.

현재 트랜잭션 격리 수준은 명시되지 않아 DB 기본값인 `READ COMMITTED`가 적용된다. `READ COMMITTED`는 Phantom Read를 막지 않는다.

**Race Condition 시나리오**

```
Thread A: getAll(page=0, size=10) → 게시글 10개 조회
Thread B: create(new post) → INSERT 완료 (커밋)
Thread A: countAll() → total = 11  ← 목록(10개)과 total(11) 불일치
```

**결론: Phantom Read 발생 가능 (위험도 낮음)**

---

### PUT /posts/:id — 게시글 수정

```kotlin
// PostService.kt
@Transactional
override fun update(command: UpdatePostCommand): UpdatePostResult {
    val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)  // Read
    val updated = post.update(command.title, command.content, command.requesterUsername)   // Modify
    return UpdatePostResult.from(postPort.store(updated))                                  // Write
}
```

전형적인 Read-Modify-Write 패턴이다. `@Version` 필드도, `@Lock`도 없어 동시 수정 시 Lost Update가 발생한다.

`PostEntity`의 모든 필드가 Kotlin `val`(불변)이지만, `postPort.store()`는 내부적으로 `jpa.save()`를 호출하며 id가 존재하면 JPA가 `UPDATE` SQL을 실행한다. 즉, **새 객체로 덮어쓰기**가 일어나므로 불변 구조가 Lost Update를 막지 않는다.

**Race Condition 시나리오**

```
Thread A: getById(1) → Post(title="원본")
Thread B: getById(1) → Post(title="원본")

Thread A: store(Post(title="A의 수정")) → UPDATE 완료
Thread B: store(Post(title="B의 수정")) → UPDATE 완료  ← A의 수정 유실
```

**결론: Lost Update 발생 가능 (위험도 높음)**

---

### DELETE /posts/:id — 게시글 삭제

```kotlin
// PostService.kt
@Transactional
override fun delete(command: DeletePostCommand) {
    val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)  // Check
    post.validateOwner(command.requesterUsername)
    postPort.delete(command.id)                                                            // Act
}
```

`getById()`로 존재를 확인한 뒤 `delete()`를 호출하는 Check-then-Act 패턴이다.

두 스레드가 동시에 같은 게시글을 삭제하려 할 때, 둘 다 `getById()`에서 존재를 확인하고 진입할 수 있다. JPA `deleteById()`는 대상 row가 없어도 예외를 던지지 않으므로 **두 번째 삭제 시도가 조용히 성공**한다. 데이터 오염은 없으나 권한 검증을 통과한 뒤 이미 삭제된 row에 delete가 실행되는 상황이 발생한다.

**Race Condition 시나리오**

```
Thread A: getById(1) → 존재 확인, validateOwner 통과
Thread B: getById(1) → 존재 확인, validateOwner 통과

Thread A: delete(1) → 삭제 완료
Thread B: delete(1) → 이미 없는 row → 예외 없이 통과
```

**결론: 중복 삭제 발생 가능 (위험도 낮음)**

---

### POST /join — 회원가입

```kotlin
// User.kt (도메인 객체 내부)
fun signUp(...): User {
    if (userPort.isUsernameTaken(username)) {  // Check
        throw BaseException(ErrorCode.DUPLICATE)
    }
    return User(username = username, password = passwordEncryptor.encrypt(password))
}

// JoinService.kt
@Transactional
override fun join(command: JoinCommand) {
    val user = User.signUp(...)
    userPort.register(user)  // Act
}
```

`isUsernameTaken()`으로 중복을 확인한 뒤 `register()`로 저장하는 Check-then-Act 패턴이다. 두 스레드가 동시에 같은 username으로 가입하면 둘 다 `isUsernameTaken()`에서 `false`를 받고 진입할 수 있다.

DB의 `username UNIQUE` 제약이 최종 방어선이 되어 실제 중복 저장은 막히지만, 이때 발생하는 `DataIntegrityViolationException`을 현재 코드에서 catch하지 않아 **500 Internal Server Error**로 클라이언트에 노출된다.

**Race Condition 시나리오**

```
Thread A: isUsernameTaken("sion") → false
Thread B: isUsernameTaken("sion") → false

Thread A: register(User("sion")) → INSERT 성공
Thread B: register(User("sion")) → DataIntegrityViolationException → 500 에러 노출
```

**결론: Check-then-Act로 인한 예외 처리 누락 (위험도 중간)**

---

## 종합 결과

| CRUD 동작 | 엔드포인트 | Race Condition 패턴 | 위험도 |
|---|---|---|---|
| 게시글 생성 | `POST /posts` | 없음 | 없음 |
| 게시글 단건 조회 | `GET /posts/:id` | 없음 | 없음 |
| 게시글 목록 조회 | `GET /posts` | Phantom Read | 낮음 |
| **게시글 수정** | **`PUT /posts/:id`** | **Lost Update** | **높음** |
| 게시글 삭제 | `DELETE /posts/:id` | 중복 삭제 | 낮음 |
| 회원가입 | `POST /join` | Check-then-Act | 중간 |
