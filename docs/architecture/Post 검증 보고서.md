# Post CRUD 검증 보고서

> 작성일: 2026-04-16

---

## 1. 검증 범위

게시글(Post) CRUD 기능의 전 레이어 단위 테스트 및 실제 API 동작 검증.

---

## 2. 단위 테스트 결과

총 **33개 테스트 PASS**.

### Domain — PostTest (10개)

| # | 테스트 | 결과 |
|---|--------|------|
| 1 | 게시글 정상 생성 | ✅ |
| 2 | DB 데이터로 게시글 복원 | ✅ |
| 3 | 정상 수정 | ✅ |
| 4 | 수정 시 작성자 불일치 → ACCESS_DENIED | ✅ |
| 5 | 정상 삭제 | ✅ |
| 6 | 삭제 시 작성자 불일치 → ACCESS_DENIED | ✅ |
| 7 | 이미 삭제된 게시글 재삭제 → INVALID_STATE | ✅ |
| 8 | TitleVO 빈값 → 예외 | ✅ |
| 9 | TitleVO 255자 초과 → 예외 | ✅ |
| 10 | ContentVO 빈값 → 예외 | ✅ |

### Application — PostServiceTest (10개)

| # | 테스트 | 결과 |
|---|--------|------|
| 1 | 정상 생성 → CreatePostResult | ✅ |
| 2 | 정상 단건 조회 → GetPostResult | ✅ |
| 3 | 단건 조회 게시글 없음 → NOT_FOUND | ✅ |
| 4 | 단건 조회 작성자 없음 → NOT_FOUND | ✅ |
| 5 | 정상 목록 조회 → List | ✅ |
| 6 | 빈 목록 조회 → 빈 리스트 | ✅ |
| 7 | 정상 수정 → UpdatePostResult | ✅ |
| 8 | 수정 게시글 없음 → NOT_FOUND | ✅ |
| 9 | 정상 삭제 | ✅ |
| 10 | 삭제 게시글 없음 → NOT_FOUND | ✅ |

### Infrastructure — PostPersistenceAdapterTest (5개)

| # | 테스트 | 결과 |
|---|--------|------|
| 1 | save → 도메인 Post 반환 | ✅ |
| 2 | findByIdAndDeletedFalse → 존재하면 Post | ✅ |
| 3 | findByIdAndDeletedFalse → 없으면 null | ✅ |
| 4 | findAllByDeletedFalse → List 반환 | ✅ |
| 5 | findAllByDeletedFalse → 빈 리스트 | ✅ |

### Presentation — PostControllerTest (8개)

| # | 테스트 | 결과 |
|---|--------|------|
| 1 | POST /api/posts 정상 생성 → 201 | ✅ |
| 2 | POST /api/posts 비로그인 → 400 A001 | ✅ |
| 3 | GET /api/posts/{id} 정상 조회 → 200 | ✅ |
| 4 | GET /api/posts 목록 조회 → 200 | ✅ |
| 5 | PUT /api/posts/{id} 정상 수정 → 200 | ✅ |
| 6 | PUT /api/posts/{id} 비로그인 → 400 A001 | ✅ |
| 7 | DELETE /api/posts/{id} 정상 삭제 → 200 | ✅ |
| 8 | DELETE /api/posts/{id} 비로그인 → 400 A001 | ✅ |

---

## 3. API 통합 검증 결과

애플리케이션 기동 후 curl로 전수 호출. 총 **10개 시나리오 PASS**.

| Step | API | 예상 | 실제 | 결과 |
|------|-----|------|------|------|
| 1 | POST /api/posts (토큰 O) | 201, postId 반환 | 201, `{"code":"SUCCESS","data":{"postId":2}}` | ✅ |
| 2 | POST /api/posts (토큰 X) | 400, A001 | 400, `{"code":"A001","data":null}` | ✅ |
| 3 | GET /api/posts | 200, 배열 | 200, 게시글 목록 배열 | ✅ |
| 4 | GET /api/posts/{id} | 200, 상세 | 200, 전체 필드 포함 | ✅ |
| 5 | GET /api/posts/9999 | 400, D001 | 400, `{"code":"D001","data":null}` | ✅ |
| 6 | PUT /api/posts/{id} (토큰 O) | 200, 수정 반영 | 200, 수정된 내용 | ✅ |
| 7 | PUT /api/posts/{id} (토큰 X) | 400, A001 | 400, `{"code":"A001","data":null}` | ✅ |
| 8 | DELETE /api/posts/{id} (토큰 O) | 200 | 200, `{"code":"SUCCESS","data":null}` | ✅ |
| 9 | DELETE /api/posts/{id} (토큰 X) | 400, A001 | 400, `{"code":"A001","data":null}` | ✅ |
| 10 | GET /api/posts/{id} (삭제 후) | 400, D001 | 400, `{"code":"D001","data":null}` | ✅ |

---

## 4. 리팩터링 반영 사항

| 항목 | 내용 |
|------|------|
| PostService 통합 | Service 4개 → PostService 1개로 통합 (UseCase 분리 유지) |
| `!!` 제거 | Post 관련 코드 `requireNotNull`로 교체 |
| KDoc 보강 | UseCase/Command/Result/Service/Adapter/Controller/DTO 전체 |
| GetPostCommand 추가 | `getById(Long)` → `getById(GetPostCommand)` 일관성 개선 |
| N+1 해결 | 목록 조회 `findById` loop → `findByIds` 배치 조회 |

---

## 5. 미해결 이슈

없음.
