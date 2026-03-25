# libs.versions.toml (Version Catalog) 완전 해설

> 이 문서는 우리 프로젝트(`oop`)의 실제 파일을 기준으로 설명합니다.

---

## 먼저 — `libs.versions.toml`이 뭔가요?

### 한 줄 정의
> **프로젝트에서 쓰는 외부 라이브러리와 도구들의 이름·버전을 한 파일에 모아둔 목록표**

---

### "외부 라이브러리"가 뭔가요?

코드를 짜다 보면 내가 직접 만들지 않은 기능을 가져다 써야 할 때가 있습니다.

예를 들어:
- HTTP 요청을 받는 기능 → Spring Web
- DB에 데이터를 저장하는 기능 → Spring Data JPA
- JSON을 객체로 변환하는 기능 → Jackson
- 테스트를 작성하는 기능 → JUnit

이런 것들을 **외부 라이브러리**라고 합니다.
이 라이브러리들은 인터넷 저장소(Maven Central)에 올라가 있고,
Gradle이 빌드할 때 자동으로 다운로드해 줍니다.

---

### 라이브러리를 가져오려면 "주소"가 필요합니다

라이브러리를 가져오려면 아래처럼 **정확한 좌표**를 알아야 합니다.

```kotlin
implementation("org.springframework.boot:spring-boot-starter-web:4.0.4")
//              ^그룹(만든 조직)         ^이름(라이브러리명)     ^버전
```

이걸 Gradle에 알려주면, Gradle이 인터넷에서 찾아서 다운로드합니다.
마치 택배를 보낼 때 **주소:받는사람:우편번호** 를 적어야 하는 것과 같습니다.

---

### Version Catalog가 뭔가요?

**Version Catalog = 라이브러리 주소록**

프로젝트에서 쓰는 라이브러리가 10개, 20개, 30개가 넘어가면
이 "주소"들을 각 모듈의 build.gradle.kts 파일에 흩어서 쓰면 관리가 힘들어집니다.

그래서 Gradle 7.4부터 공식으로 도입한 기능이 **Version Catalog**입니다.
"라이브러리 주소록을 딱 한 파일에 모아두고, 코드에서는 이름으로만 참조하자"는 개념입니다.

```
Version Catalog 없이                   Version Catalog 사용
─────────────────────────────────────  ─────────────────────────────────────────
각 모듈에 주소를 직접 씀               한 파일에 주소를 모아두고
                                       코드에서는 이름으로 참조
"org.springframework.boot:             libs.spring.boot.starter.web
  spring-boot-starter-web:4.0.4"       ↑ IDE가 자동완성해 줌
  ↑ 외워야 함, 오타 위험              버전 변경 시 한 곳만 수정
버전 변경 시 모든 파일 수정
```

---

### `libs.versions.toml` 파일이 뭔가요?

Version Catalog를 정의하는 **실제 파일**입니다.

- **이름**: `libs.versions.toml` (Gradle 규약. 이 이름을 써야 자동 감지됨)
- **위치**: `gradle/libs.versions.toml` (프로젝트 루트의 gradle 폴더 안)
- **형식**: TOML (읽기 쉬운 설정 파일 형식)

```
내가 쓰는 라이브러리를 이 파일에 등록
         ↓
build.gradle.kts에서 libs.xxx 로 참조
         ↓
Gradle이 빌드할 때 인터넷에서 다운로드
```

즉, `libs.versions.toml` = **Version Catalog를 구현한 파일**입니다.

---

## 0. 왜 이게 필요한가? (배경부터 이해하기)

### Version Catalog가 없던 시절의 문제

멀티모듈 프로젝트에서 라이브러리를 추가할 때 이런 식으로 썼습니다.

```kotlin
// presentation/build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-web:4.0.4")

// infrastructure/build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.0.4")

// application/build.gradle.kts
implementation("org.springframework:spring-context:6.2.5")
```

**문제 1 - 버전이 여기저기 흩어짐**
Spring Boot를 4.0.4 → 4.1.0으로 올리려면?
→ 모든 모듈의 build.gradle.kts 파일을 열어서 `4.0.4`를 찾아 하나씩 바꿔야 함.
→ 하나라도 놓치면? **버전 불일치로 런타임 오류 발생**

**문제 2 - 오타 위험**
```kotlin
// 어느 모듈은 이렇게 쓰고
implementation("org.springframework.boot:spring-boot-starter-web:4.0.4")

// 다른 모듈은 오타로 이렇게 씀
implementation("org.springframework.boot:spring-boot-starter-web:4.0.4 ")  // 뒤에 공백!
```

**문제 3 - IDE 자동완성 없음**
라이브러리 이름을 통째로 외워서 문자열로 직접 써야 함. 오타가 나도 **컴파일 전까지는 모름**.

---

### Version Catalog가 해결하는 것

> **"라이브러리 정보를 딱 한 군데에만 정의하고, 코드에서는 이름으로 참조한다"**

```
gradle/libs.versions.toml   ← 라이브러리 정보가 모두 여기 정의됨
         ↓
build.gradle.kts에서 libs.spring.boot.starter.web 처럼 이름으로 참조
→ IDE 자동완성 지원
→ 버전 변경 시 toml 파일 한 곳만 수정
→ 오타 시 빌드 시점에 즉시 오류 발생
```

---

## 1. 파일 위치

```
oop/
├── gradle/
│   ├── libs.versions.toml   ← 여기!
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── domain/
├── application/
├── infrastructure/
├── presentation/
└── app/
```

**왜 `gradle/` 폴더 안에?**
Gradle이 `gradle/libs.versions.toml` 경로를 **자동으로 감지**합니다. (Gradle 7.4+)
별도 설정 없이 이 경로에 파일을 두면 바로 사용 가능합니다.

---

## 2. 파일 전체 구조

```toml
[versions]    ← 버전 번호만 모아두는 곳
[plugins]     ← Gradle 플러그인 정의
[libraries]   ← 라이브러리(의존성) 정의
[bundles]     ← 여러 라이브러리를 묶어서 한 번에 추가 (우리 프로젝트는 미사용)
```

**TOML이 뭔가요?**
Tom's Obvious Minimal Language의 약자. `.properties`보다 구조적이고, JSON보다 읽기 쉬운 설정 파일 형식입니다.
```toml
# 이런 형태로 씁니다
key = "value"
[섹션이름]
key = { 속성1 = "값1", 속성2 = "값2" }
```

---

## 3. `[versions]` 섹션 — 버전 번호만 모아두는 창고

```toml
[versions]
kotlin                       = "2.2.21"
spring-boot                  = "4.0.4"
spring-dependency-management = "1.1.7"
```

### 역할
버전 번호를 **이름(별칭)**에 매핑합니다.
아래 `[plugins]`, `[libraries]`에서 이 이름을 참조해서 버전을 가져옵니다.

### 핵심: `version.ref`로 참조
```toml
[versions]
kotlin = "2.2.21"          # ← 여기 정의된 값을

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
#                                                              ^^^^^^^^
#                                                         "kotlin" 버전을 가져다 씀 (= "2.2.21")
```

**버전 업그레이드 시 단 한 줄만 변경하면 됩니다**
```toml
# kotlin을 2.2.21 → 2.3.0으로 올리고 싶다면?
[versions]
kotlin = "2.3.0"   # ← 이 한 줄만 바꾸면

# kotlin을 사용하는 모든 플러그인/라이브러리에 자동 반영됨
```

---

## 4. `[plugins]` 섹션 — Gradle 플러그인 정의

```toml
[plugins]
kotlin-jvm                   = { id = "org.jetbrains.kotlin.jvm",            version.ref = "kotlin" }
kotlin-spring                = { id = "org.jetbrains.kotlin.plugin.spring",  version.ref = "kotlin" }
spring-boot                  = { id = "org.springframework.boot",             version.ref = "spring-boot" }
spring-dependency-management = { id = "io.spring.dependency-management",     version.ref = "spring-dependency-management" }
```

### 플러그인이 뭔가요?
Gradle에 **기능을 추가하는 도구**입니다.
- `kotlin-jvm` → "이 프로젝트는 Kotlin 코드를 JVM용으로 컴파일할 수 있게 해줘"
- `kotlin-spring` → "Spring 어노테이션 붙은 클래스를 자동으로 open으로 만들어줘"
- `spring-boot` → "Spring Boot 방식으로 실행 가능한 jar 파일 만들어줘"
- `spring-dependency-management` → "BOM으로 의존성 버전 자동 관리해줘"

### 각 필드 의미
```toml
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
#  ^별칭         ^플러그인 공식 ID                  ^[versions]의 kotlin 값 참조
```

| 필드 | 설명 |
|------|------|
| `id` | 플러그인의 공식 식별자. 전 세계에서 유일한 이름 |
| `version.ref` | `[versions]`에서 정의한 버전 이름을 참조 |

### build.gradle.kts에서 사용
```kotlin
// 루트 build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    //    ^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //    toml의 kotlin-jvm → libs.plugins.kotlin.jvm (하이픈이 점으로 변환됨)
}

// app/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}
```

---

## 5. `[libraries]` 섹션 — 라이브러리(의존성) 정의

```toml
[libraries]
# Kotlin
kotlin-reflect               = { group = "org.jetbrains.kotlin",     name = "kotlin-reflect" }
kotlin-test-junit5           = { group = "org.jetbrains.kotlin",     name = "kotlin-test-junit5" }

# Spring Boot
spring-boot-starter          = { group = "org.springframework.boot", name = "spring-boot-starter" }
spring-boot-starter-web      = { group = "org.springframework.boot", name = "spring-boot-starter-web" }
spring-boot-starter-test     = { group = "org.springframework.boot", name = "spring-boot-starter-test" }

# Test
junit-platform-launcher      = { group = "org.junit.platform",       name = "junit-platform-launcher" }
```

> **지금은 실제로 사용하는 의존성만 최소로 유지합니다.**
> JPA, PostgreSQL, Jackson 등은 해당 기능을 구현할 때 추가합니다.

### 각 필드 의미
```toml
spring-boot-starter-web = { group = "org.springframework.boot", name = "spring-boot-starter-web" }
#  ^별칭                    ^그룹(조직)                            ^아티팩트 이름
```

Maven/Gradle의 라이브러리 좌표는 **group:name:version** 3개로 구성됩니다.
```
org.springframework.boot : spring-boot-starter-web : 4.0.4
      ^group                      ^name               ^version
```

### 왜 `version`이 없는 라이브러리들이 있나요?

```toml
# version이 없다!
spring-boot-starter-web = { group = "org.springframework.boot", name = "spring-boot-starter-web" }
```

→ **BOM이 버전을 자동으로 채워주기 때문입니다.**

루트 `build.gradle.kts`에서 Spring Boot BOM을 등록했습니다:
```kotlin
mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
```

이 BOM 안에 `spring-boot-starter-web`의 정확한 버전 정보가 들어있어서,
toml에 버전을 따로 명시하지 않아도 자동으로 올바른 버전이 적용됩니다.

### build.gradle.kts에서 사용
```kotlin
// presentation/build.gradle.kts
dependencies {
    implementation(libs.spring.boot.starter.web)  // spring-boot-starter-web
}

// app/build.gradle.kts
dependencies {
    implementation(libs.spring.boot.starter)      // spring-boot-starter
}
```

---

## 6. 네이밍 규칙 — 하이픈(-)이 점(.)으로 바뀐다

Version Catalog에서 가장 헷갈리는 부분입니다. 규칙은 단순합니다.

> **toml에서 `-`(하이픈)으로 구분한 이름 → Kotlin 코드에서 `.`(점)으로 접근**

```toml
# toml 정의
spring-boot-starter-web  = { ... }
spring-boot-starter-test = { ... }
kotlin-test-junit5       = { ... }
```

```kotlin
// Kotlin 코드에서 접근
libs.spring.boot.starter.web    // spring-boot-starter-web
libs.spring.boot.starter.test   // spring-boot-starter-test
libs.kotlin.test.junit5         // kotlin-test-junit5
```

### 플러그인도 동일한 규칙
```toml
# toml 정의
kotlin-jvm    = { ... }
spring-boot   = { ... }
```

```kotlin
// Kotlin 코드에서 접근
libs.plugins.kotlin.jvm     // kotlin-jvm
libs.plugins.spring.boot    // spring-boot
```

---

## 7. 전체 흐름 — 라이브러리 하나가 어떻게 프로젝트에 들어오는가

`presentation` 모듈에 `spring-boot-starter-web`이 추가되는 전체 흐름:

```
1. gradle/libs.versions.toml
   ┌─────────────────────────────────────────────────────────────────┐
   │ [versions]                                                       │
   │ spring-boot = "4.0.4"                                           │
   │                                                                  │
   │ [libraries]                                                      │
   │ spring-boot-starter-web = { group = "org.springframework.boot", │
   │                              name  = "spring-boot-starter-web" }│
   └─────────────────────────────────────────────────────────────────┘
          ↓ Gradle이 자동으로 읽음

2. build.gradle.kts (루트)
   ┌─────────────────────────────────────────────────────────────────┐
   │ subprojects {                                                    │
   │   mavenBom("...spring-boot-dependencies:$springBootVersion")    │
   │ }       ← spring-boot-starter-web의 버전이 여기서 자동 결정됨   │
   └─────────────────────────────────────────────────────────────────┘
          ↓

3. presentation/build.gradle.kts
   ┌─────────────────────────────────────────────────────────────────┐
   │ dependencies {                                                   │
   │   implementation(libs.spring.boot.starter.web)                  │
   │ }       ← toml에 정의된 라이브러리를 이름으로 참조              │
   └─────────────────────────────────────────────────────────────────┘
          ↓

4. Gradle이 실제로 다운받는 것
   org.springframework.boot:spring-boot-starter-web:4.0.4
   (group)                  (name)                  (BOM이 채워준 version)
```

---

## 8. Version Catalog 도입 전후 비교 (실제 코드)

### 도입 전
```kotlin
// presentation/build.gradle.kts
plugins {
    kotlin("plugin.spring") version "2.2.21"   // 버전 직접 명시
    id("org.springframework.boot") version "4.0.4"
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.4")
}
```

### 도입 후 (현재 우리 프로젝트)
```kotlin
// presentation/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.spring)   // 버전 없음 → toml에서 관리
}
dependencies {
    implementation(libs.spring.boot.starter.web)   // 버전 없음 → BOM + toml에서 관리
}
```

**달라진 점:**
- 버전 숫자가 build.gradle.kts에서 완전히 사라짐
- 문자열 `"org.springframework.boot:spring-boot-starter-web"` 대신 `libs.spring.boot.starter.web` 으로 IDE 자동완성 지원

---

## 9. 정리

| 섹션 | 역할 | 참조 방법 |
|------|------|-----------|
| `[versions]` | 버전 번호 정의 | `version.ref = "이름"` (내부에서만 사용) |
| `[plugins]` | Gradle 플러그인 정의 | `alias(libs.plugins.xxx.xxx)` |
| `[libraries]` | 라이브러리 의존성 정의 | `libs.xxx.xxx` |

**핵심 한 줄 요약:**
> `libs.versions.toml`은 **프로젝트에서 사용하는 모든 외부 라이브러리와 도구의 정보를 한 곳에 모아둔 명세서**이며, 코드에서는 이름으로만 참조해 버전 관리를 중앙화한다.
