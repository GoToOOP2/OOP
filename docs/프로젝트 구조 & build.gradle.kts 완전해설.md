# 프로젝트 구조 & build.gradle.kts 완전 해설

> Java만 해온 신입 개발자를 위한 설명입니다.
> 우리 프로젝트(`oop`)의 실제 파일을 기준으로 설명합니다.

---

## 1. Gradle이 뭔가요? (가장 먼저)

Java 프로젝트를 Maven으로 관리해봤다면 `pom.xml`을 알 것입니다.
Gradle은 그 `pom.xml`을 **코드(Kotlin/Groovy)로 쓸 수 있게 만든 빌드 도구**입니다.

```
Maven      → pom.xml           로 프로젝트 설정
Gradle     → build.gradle      로 프로젝트 설정 (Groovy 문법)
Gradle KTS → build.gradle.kts  로 프로젝트 설정 (Kotlin 문법, 우리 프로젝트)
```

`.kts`가 붙으면 **Kotlin Script**로 작성된 Gradle 파일입니다.
문법이 Kotlin이라는 것만 다르고, 하는 역할은 같습니다.

---

## 2. 프로젝트 전체 구조

```
oop/  ← 루트(최상위) 프로젝트
│
├── build.gradle.kts           ← [루트] 5개 모듈 전체 공통 설정
├── settings.gradle.kts        ← [루트] "이 프로젝트는 모듈이 몇 개야?" 선언
├── gradle/
│   └── libs.versions.toml    ← [공통] 라이브러리 버전 목록표 (Version Catalog)
│
├── domain/                    ← [모듈1] 핵심 비즈니스 규칙
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/oop/domain/
│
├── application/               ← [모듈2] 비즈니스 로직 조율 (UseCase/Service)
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/oop/application/
│
├── infrastructure/            ← [모듈3] DB, 외부 API 등 기술 구현체
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/oop/infrastructure/
│
├── presentation/              ← [모듈4] HTTP 요청/응답 처리 (Controller)
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/oop/presentation/
│
└── app/                       ← [모듈5] Composition Root (진입점, 모든 모듈 조립)
    ├── build.gradle.kts
    └── src/main/kotlin/com/oop/
        └── OopApplication.kt  ← Spring Boot 시작점 (main 함수)
```

### 각 모듈의 의존 방향

```
app (진입점)
 ├── → presentation  →  application  →  domain
 ├── → application
 ├── → domain
 └── → infrastructure (runtimeOnly)  →  domain
```

| 모듈 | 의존 대상 | 이유 |
|------|----------|------|
| `domain` | 없음 | 가장 순수한 핵심 규칙. 아무것도 몰라야 함 |
| `application` | domain | 도메인 객체와 규칙을 사용해 비즈니스를 조율 |
| `infrastructure` | domain | domain의 Repository 인터페이스를 구현 |
| `presentation` | application | application의 UseCase를 호출해 HTTP 응답 |
| `app` | 전부 | 아무 로직 없이 모든 레이어를 조립만 함 |

---

## 3. `settings.gradle.kts` — "이 프로젝트 모듈 구성 신고서"

```kotlin
rootProject.name = "oop"

include(
    "domain",
    "application",
    "infrastructure",
    "presentation",
    "app"
)
```

**딱 두 가지 역할만 합니다.**

| 설정 | 설명 |
|------|------|
| `rootProject.name = "oop"` | 이 프로젝트의 이름은 "oop"다 |
| `include(...)` | 이 프로젝트 안에 있는 모듈 목록 (5개) |

Maven의 `pom.xml`에서 `<modules>` 태그와 같은 역할입니다.
여기에 선언하지 않으면 Gradle이 해당 폴더를 모듈로 인식하지 않습니다.

---

## 4. `plugins {}` 블록이 뭔가요? (핵심 개념)

Java 개발자라면 `pom.xml`의 `<build><plugins>` 를 알 것입니다.
Gradle의 `plugins {}` 블록도 **같은 개념**입니다.

> **플러그인 = Gradle에 기능을 추가하는 확장팩**

Gradle은 기본적으로 아무것도 할 줄 모릅니다.
"Kotlin 코드를 컴파일해", "Spring Boot jar 파일 만들어" 같은 기능은
**플러그인을 추가해야 사용 가능**합니다.

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)      // "Kotlin 코드 컴파일" 기능 추가
    alias(libs.plugins.spring.boot)     // "Spring Boot jar 만들기" 기능 추가
}
```

### 우리 프로젝트에서 사용하는 플러그인 4개

| 플러그인 | 하는 일 | Java로 비유 |
|----------|---------|-------------|
| `kotlin.jvm` | Kotlin 코드를 JVM 바이트코드로 컴파일 | javac와 같은 역할 |
| `kotlin.spring` | Spring 어노테이션 붙은 클래스를 자동으로 상속 가능하게 처리 | 아래 별도 설명 |
| `spring.boot` | 실행 가능한 Spring Boot jar 파일 생성 | Maven의 spring-boot-maven-plugin |
| `spring.dependency.management` | BOM으로 라이브러리 버전 자동 관리 | Maven의 dependencyManagement |

### `kotlin.spring` 플러그인이 왜 필요한가?

Java에서 클래스는 기본적으로 상속 가능합니다.
```java
// Java: 기본이 상속 가능
public class MyService { }          // 상속 가능
public final class MyService { }    // final 붙어야 상속 불가
```

Kotlin은 **반대**입니다.
```kotlin
// Kotlin: 기본이 상속 불가 (final)
class MyService { }         // 상속 불가 (Java의 final class와 같음)
open class MyService { }    // open 붙어야 상속 가능
```

그런데 Spring은 내부적으로 **프록시(가짜 자식 클래스)** 를 만들어서 동작합니다.
```
@Transactional MyService
        ↓ Spring이 내부적으로
MyService를 상속한 가짜 클래스(프록시) 생성
→ 트랜잭션 시작/종료 코드를 앞뒤에 끼워넣음
```

Kotlin 클래스는 기본이 `final`이라 상속이 불가능 → **프록시 생성 실패 → 오류**

`kotlin.spring` 플러그인이 이 문제를 해결합니다.
```
@Service, @Component, @Transactional 등이 붙은 클래스
→ 컴파일 시 자동으로 open 처리
→ Spring이 프록시 만들 수 있음 → 정상 동작
```

### `apply false` 란?

```kotlin
// 루트 build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)    apply false   // ← 이게 뭐?
    alias(libs.plugins.spring.boot)   apply false
}
```

> "이 플러그인이 있다는 건 알고 있어. 근데 지금 바로 적용하지는 마."

루트에서 `apply false` 없이 선언하면 → **5개 모듈 전부에 즉시 적용**됩니다.
예를 들어 `spring.boot` 플러그인을 루트에서 바로 적용하면
domain, application, infrastructure, presentation까지 Spring Boot 실행 jar를 만들려고 합니다.
실행 jar는 **app 모듈 하나만** 만들면 됩니다.

```
루트에서 apply false → 버전 정보만 등록, 적용은 안 함
        ↓
app/build.gradle.kts에서 alias(libs.plugins.spring.boot) → 여기서만 적용
```

---

## 5. 파일별 역할 완전 해설

### 5-1. 루트 `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)                   apply false
    alias(libs.plugins.kotlin.spring)                apply false
    alias(libs.plugins.spring.boot)                  apply false
    alias(libs.plugins.spring.dependency.management) apply false
}
```
→ 4개 플러그인을 **버전 포함해서 등록만** 해둠.
→ 각 모듈이 필요한 것만 `alias(...)`로 꺼내 쓸 수 있게 준비.

```kotlin
group = "com"
version = "0.0.1-SNAPSHOT"
```
→ 이 프로젝트의 Maven 좌표.
→ `SNAPSHOT` = "아직 개발 중인 버전"이라는 Maven/Gradle 관례.

```kotlin
val springBootVersion = libs.versions.spring.boot.get()
```
→ `libs.versions.toml`에서 `spring-boot = "4.0.4"` 값을 꺼내서 변수에 저장.
→ `subprojects {}` 안에서는 `libs`에 직접 접근이 안 되기 때문에 미리 꺼내 두는 것.

```kotlin
subprojects {
    // 여기 안은 5개 모듈 모두에 적용됨
}
```

```kotlin
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
```
→ 모든 모듈에 공통으로 필요한 2개를 실제 적용.
→ Kotlin 컴파일과 BOM 의존성 관리는 5개 모듈 전부 필요하기 때문.

```kotlin
    repositories { mavenCentral() }
```
→ 라이브러리를 **어디서 다운받을지** 지정.
→ `mavenCentral()` = 전 세계 Java/Kotlin 라이브러리 공식 저장소.

```kotlin
    the<DependencyManagementExtension>().imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
```
→ **BOM(Bill of Materials)** 등록.
→ 이 덕분에 각 모듈에서 라이브러리 추가할 때 버전 생략 가능.

```kotlin
    configure<JavaPluginExtension> {
        toolchain { languageVersion = JavaLanguageVersion.of(21) }
    }
```
→ Java 21로 컴파일하도록 설정.
→ JDK 21이 없으면 Gradle이 자동으로 다운로드해줌.

```kotlin
    tasks.withType<KotlinJvmCompile> {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-Xannotation-default-target=param-property"
            )
        }
    }
```
→ `-Xjsr305=strict` : `@Nullable`, `@NonNull` 어노테이션을 Kotlin이 엄격하게 인식.
→ `-Xannotation-default-target=param-property` : `@Valid` 같은 어노테이션이 생성자 파라미터에도 적용되도록.

```kotlin
    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
```
→ 5개 모듈 **전부에 공통으로 들어갈** 라이브러리.
→ 버전이 없는 이유 = BOM이 자동으로 채워줌.

---

### 5-2. `domain/build.gradle.kts`

```kotlin
// domain은 다른 모듈에 의존하지 않는다
```

**plugins도 없고 dependencies도 없습니다.**

→ 루트 `subprojects {}`에서 `kotlin.jvm`이 이미 적용되어 Kotlin 컴파일은 됨.
→ `kotlin.spring`이 없는 이유 = domain은 `@Service`, `@Component` 같은 Spring Bean을 만들지 않음.
→ 외부 라이브러리 의존성도 없음. **아무것도 의존하지 않는 가장 순수한 레이어.**

> 도메인 규칙, 엔티티 클래스, Repository 인터페이스 등을 추가할 때 필요한 의존성을 그때 추가합니다.

---

### 5-3. `application/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.spring)   // ← domain에는 없던 것
}

// application은 domain만 의존한다 (infrastructure, presentation은 볼 수 없다)
dependencies {
    implementation(project(":domain"))
}
```

**`kotlin.spring` 플러그인이 추가됐습니다.**
→ application 레이어에는 `@Service` 가 붙은 클래스들이 생길 예정.
→ Spring 프록시가 동작하려면 클래스가 `open`이어야 함 → `kotlin.spring` 플러그인 필요.

**`project(":domain")`이 뭔가요?**
→ 같은 프로젝트 안의 다른 모듈을 의존성으로 추가하는 문법.
→ Maven의 `<dependency>`에서 같은 프로젝트 모듈을 참조하는 것과 동일.

> `@Service`, `@Transactional` 등 Spring 어노테이션을 쓰는 서비스 클래스를 만들 때
> `spring-context`, `spring-tx`를 추가합니다.

---

### 5-4. `infrastructure/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.spring)
}

// infrastructure는 domain만 의존한다 (application, presentation은 볼 수 없다)
dependencies {
    implementation(project(":domain"))
}
```

**domain만 의존합니다 (DDD 스타일).**
→ domain에 정의된 Repository 인터페이스를 infrastructure가 구현.
→ domain의 Entity 클래스를 persistence에 사용.

**왜 application을 의존하지 않나요?**
→ Port(인터페이스)를 domain에 두는 DDD 스타일을 선택했기 때문.
→ infrastructure가 알아야 할 인터페이스는 모두 domain에 있음.

> DB 연동 코드를 만들 때 `spring-boot-starter-data-jpa`, `postgresql` 등을 추가합니다.

---

### 5-5. `presentation/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.spring)
    // spring.boot 플러그인 없음 — 진입점은 app 모듈이 담당
}

// presentation은 application만 의존한다
dependencies {
    implementation(project(":application"))

    implementation(libs.spring.boot.starter.web)   // HTTP 처리
}
```

**`spring.boot` 플러그인이 없습니다.**
→ 이전에는 presentation이 진입점이었지만, 지금은 `app` 모듈이 진입점.
→ presentation은 순수하게 **HTTP 요청/응답만** 담당.

**`spring-boot-starter-web`이 있는 이유**
→ `@RestController`, `@GetMapping` 등 HTTP 레이어 어노테이션 사용을 위해 필요.
→ presentation 레이어의 **핵심 의존성**이므로 유지.

**왜 infrastructure, domain을 의존하지 않나요?**
→ presentation은 application만 알면 됨.
→ domain 객체가 필요하다면 application을 통해서 간접 참조.
→ infrastructure는 절대 알 수 없음 (계층 위반).

> Controller 클래스를 만들 때 `jackson-module-kotlin` 등을 추가합니다.

---

### 5-6. `app/build.gradle.kts` ← NEW (Composition Root)

```kotlin
plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)   // ← 이 모듈만 가지고 있음
}

// app은 Composition Root — 모든 모듈을 조립하는 진입점
dependencies {
    implementation(project(":presentation"))
    implementation(project(":application"))
    implementation(project(":domain"))
    runtimeOnly(project(":infrastructure"))

    // @SpringBootApplication, runApplication 컴파일 타임에 필요
    implementation(libs.spring.boot.starter)
}
```

**Composition Root가 뭔가요?**
> 모든 객체(Bean)의 의존성을 한 곳에서 조립하는 지점.

app 모듈은 **코드를 작성하지 않고 조립만** 합니다.
`OopApplication.kt`(main 함수)가 여기에 있고, Spring Boot가 시작되면서
각 레이어의 Bean들을 자동으로 찾아서 연결합니다.

**`spring.boot` 플러그인이 여기만 있는 이유**
→ 실행 가능한 jar 파일(`bootJar`)은 **진입점 하나만** 만들면 됨.
→ `./gradlew :app:bootRun`으로 앱을 실행.

**`runtimeOnly(project(":infrastructure"))`**

```
컴파일 시 → app이 infrastructure 코드를 직접 import 불가 (계층 위반 방지)
실행 시   → infrastructure Bean이 classpath에 올라와 Spring이 자동 연결
```

→ `runtimeOnly`이므로 app이 infrastructure를 "아는" 것이 아님.
→ Spring이 자동으로 Bean을 찾아 연결하므로 직접 참조 없이 동작.

---

## 6. 전체 의존성 흐름 한눈에 보기

```
libs.versions.toml
(버전 목록표)
      ↓ 버전 정보 제공
루트 build.gradle.kts
(공통 규칙: BOM, JDK 21, 공통 의존성)
      ↓ 공통 규칙 적용
┌────────────────────────────────────────────────────────┐
│                                                        │
│  domain/           application/                        │
│  (의존성 없음)      (kotlin.spring, domain 참조)       │
│                                                        │
│  infrastructure/   presentation/                       │
│  (kotlin.spring,   (kotlin.spring,                     │
│   domain 참조)      application + web 참조)            │
│                                                        │
│  app/                                                  │
│  (spring.boot, 모든 모듈 조립, OopApplication.kt)     │
│                                                        │
└────────────────────────────────────────────────────────┘
```

---

## 7. Java `pom.xml` vs Kotlin `build.gradle.kts` 비교표

| Maven (pom.xml) | Gradle KTS (build.gradle.kts) | 설명 |
|-----------------|-------------------------------|------|
| `<groupId>` | `group = "com"` | 조직 식별자 |
| `<version>` | `version = "0.0.1-SNAPSHOT"` | 프로젝트 버전 |
| `<modules>` | `settings.gradle.kts`의 `include()` | 모듈 목록 |
| `<dependencies>` | `dependencies {}` | 라이브러리 추가 |
| `<build><plugins>` | `plugins {}` | 빌드 도구 추가 |
| `<dependencyManagement>` | BOM `mavenBom(...)` | 버전 자동 관리 |
| `<scope>compile</scope>` | `implementation(...)` | 컴파일+런타임 |
| `<scope>test</scope>` | `testImplementation(...)` | 테스트 전용 |
| `<scope>runtime</scope>` | `runtimeOnly(...)` | 런타임 전용 |

---

## 8. 한 줄 요약

| 파일 | 한 줄 요약 |
|------|-----------|
| `settings.gradle.kts` | "이 프로젝트 이름은 oop이고 모듈은 5개야" |
| `루트 build.gradle.kts` | "5개 모듈 전부에 적용될 공통 규칙이야" |
| `gradle/libs.versions.toml` | "우리가 쓰는 라이브러리 버전 목록표야" |
| `domain/build.gradle.kts` | "순수 비즈니스 규칙, 아무것도 의존 안 해" |
| `application/build.gradle.kts` | "UseCase/Service 담당, domain만 봐" |
| `infrastructure/build.gradle.kts` | "DB·외부 API 구현체, domain만 봐 (DDD)" |
| `presentation/build.gradle.kts` | "HTTP 요청/응답 담당, application만 봐" |
| `app/build.gradle.kts` | "모든 모듈을 조립하는 진입점, Spring Boot jar 여기서 만들어" |
