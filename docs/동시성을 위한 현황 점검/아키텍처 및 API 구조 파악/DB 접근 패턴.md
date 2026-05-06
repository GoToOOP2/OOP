# DB 접근 패턴

애플리케이션이 데이터베이스와 통신하는 방식을 작성한 문서.  
어떤 기술을 사용하는지, 각 기술의 역할이 무엇인지, 트랜잭션을 어느 레이어에서 관리하는지를 다룬다.

## 사용 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Data JPA | Spring Boot 3.2.3 | 기본 CRUD, 단건 조회 |
| JOOQ | 3.18.13 | 복잡한 페이지네이션 쿼리 (윈도우 함수) |
| PostgreSQL | - | 운영 DB |
| H2 | - | 테스트 DB (인메모리) |

---

## 기술 선택 기준

| 상황 | 사용 기술 |
|------|-----------|
| 단순 CRUD, 단건 조회, 삭제 | JPA |
| 페이지 목록과 전체 개수를 한 번의 쿼리로 조회 | JOOQ |

JPA는 메서드 이름만으로 쿼리를 생성해주지만 윈도우 함수(`COUNT() OVER()`)를 표현하기 어렵다. 이 경우에만 JOOQ를 사용해 단일 쿼리로 처리한다.

---

## 주요 쿼리

### 페이지 목록 조회 (JOOQ)
```sql
SELECT *, COUNT(*) OVER() AS total_count
FROM posts
ORDER BY id DESC
LIMIT ? OFFSET ?
```
`total_count`로 전체 개수를 별도 쿼리 없이 함께 반환한다.

---

## 도메인별 구성

### Post — JPA + JOOQ 하이브리드

| 메서드 | 설명 | 위임 |
|--------|------|------|
| `save` | 게시글 저장 | JPA |
| `findPostEntityById` | ID로 단건 조회 | JPA |
| `count` | 전체 게시글 수 조회 | JPA |
| `deleteById` | ID로 삭제 | JPA |
| `findPageWithTotal` | 페이지 목록 + 전체 개수 단일 쿼리 조회 | JOOQ |

`PostEntityRepositoryImpl`(클래스)이 `PostJpaRepository`와 `PostJooqRepository`를 주입받아 메서드별로 알맞은 구현에 위임.

### User — JPA 단독

`UserJpaRepository`가 `JpaRepository`와 `UserEntityRepository`를 동시에 상속/구현. 별도 조합 클래스 없이 인터페이스 단계에서 해결.

### Health — JPA 단독

`HealthJpaRepository`가 `JpaRepository`와 `HealthEntityRepository`를 동시에 상속/구현. 별도 조합 클래스 없이 인터페이스 단계에서 해결.

---

## 트랜잭션 경계

트랜잭션은 애플리케이션 서비스 레이어에서만 선언.

| 작업 | 선언 |
|------|------|
| 쓰기 (create, update, delete) | `@Transactional` |
| 읽기 (get, getList) | `@Transactional(readOnly = true)` |

Repository 레이어에는 `@Transactional` 없음.
