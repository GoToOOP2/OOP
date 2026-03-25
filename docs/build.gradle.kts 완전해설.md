# build.gradle.kts (루트) 완전 해설

이 파일은 **4개 모듈 전체의 공통 설정을 한 곳에서 관리**하는 루트 빌드 파일입니다.

---

## 전체 구조 한눈에 보기

```
루트 build.gradle.kts
├── plugins {}          → "어떤 도구를 쓸 것인가" 선언
├── group / version     → 프로젝트 식별 정보
├── val springBootVersion → 버전 값 캡처
└── subprojects {}      → 모든 하위 모듈에 공통 적용할 설정
    ├── apply(plugin)       → 플러그인 실제 적용
    ├── repositories {}     → 라이브러리 다운로드 위치
    ├── BOM 설정            → 버전 자동 관리
    ├── Java toolchain      → JDK 버전 설정
    ├── Kotlin 컴파일 옵션  → 컴파일러 설정
    └── dependencies {}     → 모든 모듈에 공통으로 들어갈 라이브러리
```

---

## 1. `plugins {}` 블록

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)               apply false  // Kotlin JVM 플러그인
    alias(libs.plugins.kotlin.spring)            apply false  // Kotlin Spring 플러그인
    alias(libs.plugins.spring.boot)              apply false  // Spring Boot 플러그인
    alias(libs.plugins.spring.dependency.management) apply false  // 의존성 버전 관리 플러그인
}
```

### `alias(libs.plugins.xxx)` 란?
`gradle/libs.versions.toml`에 정의해둔 플러그인을 이름으로 참조하는 것
```toml
# libs.versions.toml
[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
```
→ `alias(libs.plugins.kotlin.jvm)` 으로 참조 가능. 버전을 여기서 관리함.

### `apply false` 란?
> "이 플러그인이 존재한다는 건 알지만, **지금 당장 적용하지는 마라**"

루트 프로젝트에 바로 적용하면 4개 모듈 전체에 적용됨.
각 모듈이 **필요한 것만 골라서 적용**할 수 있도록 등록만 해두는 것.

```
루트에서 apply false → 버전만 등록
        ↓
presentation/build.gradle.kts에서 alias(libs.plugins.spring.boot) → 실제 적용
```

---

## 2. `group` / `version`

```kotlin
group = "com"
version = "0.0.1-SNAPSHOT"
```

Maven/Gradle에서 라이브러리를 식별하는 좌표.
- `group` : 조직/패키지 식별자 (com.google, org.springframework 같은 것)
- `version` : 현재 프로젝트 버전 (SNAPSHOT = 개발 중인 버전)

---

## 3. `val springBootVersion`

```kotlin
val springBootVersion = libs.versions.spring.boot.get()
```

`libs`(Version Catalog)는 `subprojects {}` 클로저 **안에서는 접근 불가**하기 때문에,
루트 레벨에서 값을 미리 꺼내서 변수에 저장해 두는 것.

---

## 4. `subprojects {}` 블록

> **domain, application, infrastructure, presentation 4개 모듈 전체에 아래 내용을 자동으로 적용**

```kotlin
subprojects {
    // 여기 안에 있는 것들은 4개 모듈 모두에 적용됨
}
```

### 4-1. 플러그인 적용

```kotlin
apply(plugin = "org.jetbrains.kotlin.jvm")         // Kotlin 코드를 JVM용으로 컴파일
apply(plugin = "io.spring.dependency-management")  // 의존성 버전 자동 관리 (BOM)
```

위 1번에서 `apply false`로 등록만 해뒀던 것 중에서, **모든 모듈에 공통으로 필요한 2개만** 여기서 실제 적용.
- Kotlin JVM → Kotlin 코드 컴파일하려면 필수
- dependency-management → 아래 BOM 설정을 사용하기 위해 필수

### 4-2. `repositories {}`

```kotlin
repositories {
    mavenCentral()
}
```

라이브러리를 **어디서 다운받을지** 지정.
- `mavenCentral()` = 전 세계 Java/Kotlin 라이브러리가 올라와 있는 공식 저장소 (인터넷)
- 없으면 Gradle이 라이브러리를 찾지 못함

### 4-3. BOM 설정 ⭐ (핵심!)

```kotlin
the<DependencyManagementExtension>().imports {
    mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
}
```

#### BOM(Bill of Materials)이 뭔가요?

Spring Boot는 수십 개의 라이브러리를 사용하는데, 이 라이브러리들 사이에 **버전 충돌**이 생길 수 있음.

예시:
```
spring-web 6.1.0 은 jackson 2.15.x 를 필요로 함
내가 jackson 2.10.x 를 직접 선언하면? → 충돌!
```

BOM은 "이 버전들을 함께 쓰면 안전하다"는 **호환 버전 목록표**

```kotlin
// BOM 없이 쓰면: 버전을 모두 직접 써야 함
implementation("org.springframework.boot:spring-boot-starter-web:4.0.4")
implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
implementation("org.springframework.boot:spring-boot-starter-test:4.0.4")
// ...수십 개...

// BOM 있으면: 버전 생략 가능! BOM이 알아서 맞는 버전으로 채워줌
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
implementation("org.springframework.boot:spring-boot-starter-test")
```

### 4-4. Java toolchain

```kotlin
configure<JavaPluginExtension> {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

"Java 21로 컴파일해라" 라는 설정.
- Gradle이 Java 21이 없으면 **자동으로 다운로드**해줌
- 팀원들이 각자 다른 버전의 JDK를 써도 **항상 21로 통일**됨

### 4-5. Kotlin 컴파일 옵션

```kotlin
tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",                          // null 안전성 강화
            "-Xannotation-default-target=param-property" // 어노테이션 타겟 설정
        )
    }
}
```

- `-Xjsr305=strict` : Spring의 `@Nullable`, `@NonNull` 어노테이션을 Kotlin이 엄격하게 인식
- `-Xannotation-default-target=param-property` : `@Valid` 같은 어노테이션이 생성자 파라미터와 프로퍼티 모두에 적용되도록

### 4-6. `dependencies {}` (공통 의존성)

```kotlin
dependencies {
    "implementation"("org.jetbrains.kotlin:kotlin-reflect")                    // Kotlin 리플렉션
    "testImplementation"("org.springframework.boot:spring-boot-starter-test")  // 테스트
    "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")            // JUnit5
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")            // JUnit 실행기
}
```

4개 모듈 **모두에 공통으로 필요한** 라이브러리들.

| 의존성 | 역할 | 스코프 |
|--------|------|--------|
| `kotlin-reflect` | `@Service` 등 어노테이션 처리, Spring DI에 필수 | `implementation` (컴파일+런타임) |
| `spring-boot-starter-test` | JUnit, Mockito, AssertJ 등 테스트 도구 묶음 | `testImplementation` (테스트만) |
| `kotlin-test-junit5` | Kotlin 스타일 테스트 작성 지원 | `testImplementation` (테스트만) |
| `junit-platform-launcher` | JUnit 5 테스트를 Gradle이 실행할 때 필요 | `testRuntimeOnly` (테스트 실행만) |

---

## 의존성 스코프 한눈에 보기

```
implementation     → 컴파일할 때 필요 + 실행할 때도 필요
testImplementation → 테스트 코드 컴파일할 때 필요 + 테스트 실행할 때도 필요
testRuntimeOnly    → 테스트 실행할 때만 필요 (코드에서 직접 import는 안 함)
runtimeOnly        → 실행할 때만 필요 (ex: DB 드라이버)
```

---

## 전체 흐름 요약

```
libs.versions.toml      ← 버전 정보 중앙 관리
       ↓
build.gradle.kts (루트) ← 공통 설정 + BOM 등록 + 공통 의존성
       ↓
각 모듈 build.gradle.kts ← 모듈별 추가 의존성만 선언
```

즉, **루트 build.gradle.kts는 "모든 모듈의 규칙집"** 역할입니다.
각 모듈은 이 규칙 위에서 자신에게 필요한 것만 추가하면 됩니다.

