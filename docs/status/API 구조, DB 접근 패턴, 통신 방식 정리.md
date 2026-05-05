# API 구조, DB 접근 패턴, 통신 방식 정리

- 대분류: 배움 | 소분류: 아키텍처 | 1주차

## API 구조

**Controller 3개**
- `HealthController` → GET /api/health (서버 상태 확인)
- `PostController` → POST/GET/PUT/DELETE /posts (게시글 CRUD + 페이지네이션)
- `UserController` → /users/join, /users/login, /users/refresh, /users/me (회원가입/로그인/토큰갱신)

**공통 응답 포맷**
- 단건: `ApiResponse<T>` → `{ code, data }`
- 목록: `PageResponse<T>` → `{ content, totalCount, totalPage, page, size }`

---

## DB 접근 패턴 - JPA + JOOQ 하이브리드

```
PostEntityRepository (인터페이스)
  └── PostEntityRepositoryImpl (구현체)
        ├── PostJpaRepository   → 기본 CRUD (save, findById, delete)
        └── PostJooqRepository  → 페이지네이션 (정렬/오프셋 포함 복잡 쿼리)
```

- 단순 CRUD는 JPA, 정렬/페이징 포함 복잡 쿼리는 JOOQ로 분리
- User, Health는 단순 조회라 JPA만 사용
- DB: PostgreSQL (운영), H2 (테스트)

---

## 통신 방식

**헥사고날 아키텍처**
- Domain 계층이 Port(인터페이스)만 정의 → Infrastructure가 Adapter(구현체)로 구현
- 데이터 흐름: Controller → UseCase → Service → Port → Adapter → JPA/JOOQ

**JWT 인증 흐름**
- Access Token 30분 / Refresh Token 7일 (JJWT + HMAC-SHA)
- 요청마다 `JwtAuthFilter`가 Authorization 헤더 검증
- 검증 통과 시 request attribute에 username 저장 → `@CurrentUser`로 컨트롤러 주입
- 비밀번호: BCrypt 암호화

**외부 통신**
- WebClient, FeignClient, Kafka 없음 → REST + JWT만 사용
