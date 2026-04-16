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

## Infrastructure

### PostEntityTest

> 팩토리 메서드(`entity()`, `post()`)로 기본값 제공 — 테스트마다 필요한 필드만 오버라이드

| 테스트 | 검증 |
|---|---|
| `toDomain()` — 전체 필드 매핑 | PostEntity → Post 모든 필드 일치 |
| `toDomain()` — updatedAt null | null 필드 정상 변환 |
| `fromDomain()` — 전체 필드 매핑 | Post → PostEntity 모든 필드 일치 |
| `fromDomain()` — id null | 저장 전 상태(id=null) 정상 변환 |
| 왕복 변환 (`fromDomain → toDomain`) | 변환 후 모든 필드 동일 |

---

