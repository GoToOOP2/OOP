# 인스턴스 변수, static 자원 사용 현황 식별

- 대분류: 배움 | 소분류: 아키텍처 | 1주차

## 인스턴스 변수

**모든 Spring Bean(@Service, @Repository, @Controller)은 상태 없음**
- 의존성 주입 필드만 존재, mutable 상태 없음 → 멀티스레드 안전

**주목할 곳 1개**
- `PasswordEncryptorAdapter` → `private val encoder = BCryptPasswordEncoder()`
- `val`이라 재할당 불가, BCryptPasswordEncoder 자체는 스레드세이프 → 실질적 문제 없음

**도메인/DTO 클래스 전부 `val`(불변)으로 설계**

---

## static 자원

| 위치 | 종류 | 예시 |
|---|---|---|
| 모든 도메인/DTO/결과 클래스 | `companion object` factory 메서드 | `Post.create()`, `Post.reconstruct()`, `LoginResult.of()` |
| `PostTitle`, `PostContent` | `const val` 상수 | `MAX_LENGTH = 100`, `MAX_LENGTH = 5000` |
| `ApiResponse` | `const val` + factory 메서드 | `SUCCESS_CODE = "SUCCESS"`, `success()`, `fail()` |
| `ErrorCode` | enum 상수 | D/A/C/S 시리즈 에러코드 |
| `JwtHandlerAdapter` | `private val secretKey` (lazy 초기화) | HMAC-SHA 서명 키 |

## 전체 패턴 요약
- 모든 객체 생성을 `companion object` factory 메서드로 제어
- private constructor + `from()` / `of()` / `create()` / `reconstruct()` 네이밍 일관되게 사용
- Side-effect 최소화, 함수형 설계 원칙 준수
