# API 구조

## User API — `/users`

| 메서드 | 경로 | 설명 | 인증 | 요청 Body | 응답 |
|--------|------|------|------|-----------|------|
| POST | `/users/join` | 회원가입 | 없음 | `JoinRequest { username, password }` | 201 Created |
| POST | `/users/login` | 로그인 | 없음 | `LoginRequest { username, password }` | `TokenResponse { accessToken, refreshToken }` |
| POST | `/users/refresh` | 토큰 갱신 | 없음 | `RefreshRequest { refreshToken }` | `TokenResponse { accessToken, refreshToken }` |
| GET | `/users/me` | 내 정보 조회 (테스트용) | JWT 필요 | 없음 | `String (username)` |

## Post API — `/posts`

| 메서드 | 경로 | 설명 | 인증 | 요청 Body | 응답 |
|--------|------|------|------|-----------|------|
| POST | `/posts` | 게시글 생성 | JWT 필요 | `CreatePostRequest { title*, content* }` | `201 + CreatePostResponse { id }` |
| GET | `/posts/{id}` | 단건 조회 | 없음 | 없음 | `GetPostResponse { id, title, content, authorUsername, createdAt, updatedAt }` |
| GET | `/posts?page=0&size=10&direction=DESC` | 목록 조회 (페이지네이션) | 없음 | 없음 | `PageResponse<GetPostResponse>` |
| PUT | `/posts/{id}` | 게시글 수정 | JWT 필요 | `UpdatePostRequest { title*, content* }` | `UpdatePostResponse { id, title, content, authorUsername, createdAt, updatedAt }` |
| DELETE | `/posts/{id}` | 게시글 삭제 | JWT 필요 | 없음 | `204 No Content` |

> `*` NotBlank 검증. JWT 필요 API는 작성자 본인만 수정/삭제 가능 (`validateOwner` 도메인 검증).

## Health API — `/api/health`

| 메서드 | 경로 | 설명 | 인증 | 응답 |
|--------|------|------|------|------|
| GET | `/api/health` | 서버 상태 확인 | 없음 | `200 + ApiResponse<String>("OK")` |

## 공통 응답 구조

| 타입 | 필드 | 설명 |
|------|------|------|
| `ApiResponse<T>` | `data: T` (nullable) | 모든 API 공통 래퍼. 데이터 없는 응답은 body 없음 |
| `PageResponse<T>` | `content[], totalCount, totalPage, page, size` | 목록 조회 전용. `totalPage = ceil(totalCount / size)` |

## 인증 방식 — JWT Bearer Token

| 항목 | 내용 |
|------|------|
| 헤더 | `Authorization: Bearer <accessToken>` |
| 필터 흐름 | `JwtAuthFilter` → 유효성 검증 → `SecurityContext` 등록 → `@CurrentUser`로 username 주입 |
| 401 Unauthorized | 토큰 없거나 만료된 경우 |
| 403 Forbidden | 작성자가 아닐 때 (`validateOwner` 실패) |

## Request → Command 변환 흐름

```
HTTP 요청
  → Controller: Request DTO → Command/Query (XxxCommand.of(...))
  → Service: Command → 도메인 객체 (Post.create, User.signUp 등) [@Transactional]
  → Adapter: 도메인 객체 ↔ Entity (fromDomain / toDomain)
  → Repository → DB
  → Controller: Result → Response DTO (XxxResponse.from(result)) → ApiResponse
```

- DTO가 도메인 레이어로 넘어가지 않음 — 레이어 경계 유지
- 비즈니스 규칙 검증은 도메인 객체 내부에서 수행 (`Post.validateOwner`, `User.authenticate` 등)
