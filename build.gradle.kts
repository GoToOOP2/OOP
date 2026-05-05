// 루트 프로젝트에서 플러그인을 선언만 하고 실제 적용은 하지 않음 (apply false)
// 각 서브모듈에서 필요할 때 직접 apply해서 쓰는 방식
plugins {
    alias(libs.plugins.kotlin.jvm) apply false           // Kotlin JVM 컴파일러
    alias(libs.plugins.kotlin.spring) apply false        // Kotlin + Spring 호환성 (open class 자동 처리)
    alias(libs.plugins.kotlin.jpa) apply false           // JPA Entity 클래스에 기본 생성자 자동 생성
    alias(libs.plugins.spring.boot) apply false          // Spring Boot 패키징 (bootJar 등)
    alias(libs.plugins.dependency.management) apply false // Spring BOM으로 의존성 버전 자동 관리
    jacoco                                               // 코드 커버리지 측정 도구
}

// 루트 포함 모든 프로젝트에 공통 적용
allprojects {
    group = "com.jaeyong.oop"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral() // 의존성을 Maven Central에서 받아옴
    }
}

// libs.versions.toml에서 버전/번들을 꺼내 변수로 저장 (서브프로젝트 블록 안에서 직접 참조하면 에러나서 미리 빼둠)
val springBootVersion = libs.versions.spring.boot.get()  // Spring Boot 버전 문자열
val bundleKotlin = libs.bundles.kotlin                   // Kotlin stdlib, reflect 등 묶음
val libSpringBootTest = libs.spring.boot.test            // spring-boot-starter-test

// 루트를 제외한 모든 서브모듈에 공통 적용
subprojects {
    // 모든 서브모듈에 플러그인 일괄 적용
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    // JDK 21로 컴파일하도록 툴체인 설정
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    // Spring Boot BOM을 임포트해서 spring 관련 의존성 버전을 자동으로 맞춰줌
    // 덕분에 각 모듈에서 버전 번호 없이 "implementation("org.springframework.boot:spring-boot-starter")" 식으로만 써도 됨
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        }
    }

    // 모든 서브모듈에 Kotlin 기본 의존성 + 테스트 의존성 추가
    dependencies {
        add("implementation", bundleKotlin)      // Kotlin stdlib, reflect 등
        add("testImplementation", libSpringBootTest) // JUnit5, Mockito 등 테스트 도구
    }

    // Kotlin 컴파일 옵션
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict") // null-safety 관련 JSR-305 어노테이션을 strict 모드로 처리
            jvmTarget = "21"
        }
    }

    // 테스트 실행 시 JUnit5 플랫폼 사용
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// 전체 프로젝트 통합 JaCoCo 리포트 설정
tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate report of JaCoCo coverage results from all subprojects."

    // oop-boot은 실행 진입점(Bootstrap)이라 비즈니스 로직이 없으므로 커버리지 집계에서 제외
    val subprojects = subprojects.filter { it.name != "oop-boot" }

    // 각 모듈의 테스트 태스크가 먼저 실행되어야 .exec 파일이 생성됨
    dependsOn(subprojects.map { it.tasks.withType<Test>() })

    // 각 모듈의 JaCoCo 실행 데이터(.exec) 파일을 수집 — 실제 커버리지 원본 데이터
    executionData.setFrom(files(subprojects.map {
        File(it.layout.buildDirectory.get().asFile, "jacoco/test.exec")
    }))

    // 각 모듈의 컴파일된 클래스 파일과 소스 디렉토리를 수집 — 리포트 생성에 필요
    subprojects.forEach { subproject ->
        subproject.plugins.withType<org.gradle.api.plugins.JavaPlugin> {
            val sourceSet = subproject.extensions.getByType<SourceSetContainer>()["main"]
            classDirectories.from(sourceSet.output.classesDirs)
            sourceDirectories.from(sourceSet.allSource.srcDirs)
        }
    }

    // HTML(브라우저용)과 XML(CI 툴 연동용) 두 형식으로 리포트 생성
    reports {
        html.required.set(true)
        xml.required.set(true)
    }

    // 리포트 생성 완료 후 경로 출력
    doLast {
        println(">>> JaCoCo Aggregate Report generated: file://${reports.html.outputLocation.get().asFile}/index.html")
    }
}
