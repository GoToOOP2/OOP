plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.dependency.management) apply false
    jacoco
}

allprojects {
    group = "com.jaeyong.oop"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Spring Boot 의존성 버전 관리 및 공통 라이브러리 추출
val springBootVersion = libs.versions.spring.boot.get()
val libKotlinReflect = libs.kotlin.reflect
val libKotlinStdlib = libs.kotlin.stdlib
val libJacksonKotlin = libs.jackson.kotlin
val libSpringBootTest = libs.spring.boot.test

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    // Spring Boot 의존성 버전 관리
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        }
    }

    dependencies {
        add("implementation", libKotlinReflect)
        add("implementation", libKotlinStdlib)
        add("implementation", libJacksonKotlin)
        add("testImplementation", libSpringBootTest)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// 전체 프로젝트 통합 JaCoCo 리포트 설정
tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate report of JaCoCo coverage results from all subprojects."

    // 테스트 결과를 생성한 모듈들을 필터링
    val subprojects = subprojects.filter { it.name != "oop-boot" }

    dependsOn(subprojects.map { it.tasks.withType<Test>() })

    // 각 모듈의 실행 데이터 수집
    executionData.setFrom(files(subprojects.map {
        File(it.layout.buildDirectory.get().asFile, "jacoco/test.exec")
    }))

    // 각 모듈의 클래스 파일과 소스 코드 수집
    subprojects.forEach { subproject ->
        subproject.plugins.withType<org.gradle.api.plugins.JavaPlugin> {
            val sourceSet = subproject.extensions.getByType<SourceSetContainer>()["main"]
            classDirectories.from(sourceSet.output.classesDirs)
            sourceDirectories.from(sourceSet.allSource.srcDirs)
        }
    }

    reports {
        html.required.set(true)
        xml.required.set(true)
    }

    // 작업 완료 후 리포트 경로 출력 (클릭 시 바로 열기 위함)
    doLast {
        println(">>> JaCoCo Aggregate Report generated: file://${reports.html.outputLocation.get().asFile}/index.html")
    }
}
