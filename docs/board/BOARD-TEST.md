# Board — 테스트 설계

> 단위 테스트만 작성, Mock은 Application 레이어 이상에서만 사용

---

## 테스트 전략

| 레이어 | Mock 사용 | 이유 |
|---|---|---|
| Domain | 없음 | 순수 Kotlin 객체, 외부 의존 없음 |
| Application | PostPort Mock | DB 없이 비즈니스 흐름만 검증 |
| Infrastructure | 없음 (Entity 변환만) | toDomain/fromDomain 변환 검증 |
| Presentation | UseCase Mock | HTTP 요청/응답 형식 검증 |

---

## Domain

### PostTitleTest

| 테스트 | 검증 |
|---|---|
| 정상 제목으로 생성 성공 | `PostTitle.of("제목")` → 예외 없음 |
| 공백 제목 생성 실패 | `errorCode == POST_TITLE_BLANK` |
| 100자 제목 생성 성공 | 경계값 — 정확히 100자 통과 |
| 101자 제목 생성 실패 | `errorCode == POST_TITLE_TOO_LONG` |

### PostContentTest

| 테스트 | 검증 |
|---|---|
| 정상 내용으로 생성 성공 | `PostContent.of("내용")` → 예외 없음 |
| 공백 내용 생성 실패 | `errorCode == POST_CONTENT_BLANK` |
| 5000자 내용 생성 성공 | 경계값 — 정확히 5000자 통과 |
| 5001자 내용 생성 실패 | `errorCode == POST_CONTENT_TOO_LONG` |

### PostTest

| 테스트 | 검증 |
|---|---|
| `create()` — 정상 생성 | id=null, createdAt 설정됨 |
| `restore()` — DB 복원 | id, createdAt, updatedAt 그대로 복원 |
| `validateOwner()` — 본인 검증 성공 | 예외 없음 |
| `validateOwner()` — 타인 검증 실패 | `errorCode == POST_ACCESS_DENIED` |
| `update()` — 정상 수정 | 새 객체 반환, title/content/updatedAt 변경 |
| `update()` — 타인 수정 실패 | `errorCode == POST_ACCESS_DENIED` |

---

## Application

> `PostPort` Mockito Mock 사용

### CreatePostServiceTest

| 테스트 | 검증 |
|---|---|
| 정상 생성 | `postPort.save()` 호출됨, `CreatePostResult` 반환 |

### GetPostServiceTest

| 테스트 | 검증 |
|---|---|
| 정상 조회 | `GetPostResult` 반환 |
| 존재하지 않는 게시글 조회 | `errorCode == NOT_FOUND` |

### GetPostListServiceTest

| 테스트 | 검증 |
|---|---|
| 정상 목록 조회 | `posts`, `totalCount`, `page`, `size` 반환 |

### UpdatePostServiceTest

| 테스트 | 검증 |
|---|---|
| 정상 수정 | `postPort.save()` 호출됨, `UpdatePostResult` 반환 |
| 존재하지 않는 게시글 수정 | `errorCode == NOT_FOUND` |
| 타인 게시글 수정 | `errorCode == POST_ACCESS_DENIED` |

### DeletePostServiceTest

| 테스트 | 검증 |
|---|---|
| 정상 삭제 | `postPort.deleteById()` 호출됨 |
| 존재하지 않는 게시글 삭제 | `errorCode == NOT_FOUND` |
| 타인 게시글 삭제 | `errorCode == POST_ACCESS_DENIED` |

---

## Infrastructure

### PostEntityTest

| 테스트 | 검증 |
|---|---|
| `fromDomain()` — Post → PostEntity 변환 | 모든 필드 일치 |
| `toDomain()` — PostEntity → Post 변환 | 모든 필드 일치 |

---

## Presentation

> `MockMvc` + UseCase Mock 사용

### PostControllerTest

| 테스트 | 검증 |
|---|---|
| `POST /posts` — 정상 생성 | `201 Created`, 응답 필드 확인 |
| `POST /posts` — 빈 제목 | `400 Bad Request` (C001) |
| `GET /posts/{id}` — 정상 조회 | `200 OK`, 응답 필드 확인 |
| `GET /posts/{id}` — 없는 게시글 | `404 Not Found` (D001) |
| `GET /posts` — 목록 조회 | `200 OK`, `posts/totalCount/page/size` 확인 |
| `PUT /posts/{id}` — 정상 수정 | `200 OK` |
| `PUT /posts/{id}` — 없는 게시글 | `404 Not Found` (D001) |
| `PUT /posts/{id}` — 타인 수정 | `403 Forbidden` (D005) |
| `DELETE /posts/{id}` — 정상 삭제 | `204 No Content` |
| `DELETE /posts/{id}` — 없는 게시글 | `404 Not Found` (D001) |
| `DELETE /posts/{id}` — 타인 삭제 | `403 Forbidden` (D005) |
