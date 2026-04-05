plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

group = "com"
version = "0.0.1-SNAPSHOT"

// libs는 subprojects {} 클로저 안에서 직접 접근 불가 → 루트 레벨에서 미리 캡처
val springBootVersion = libs.versions.spring.boot.get()
val kotlinReflect = libs.kotlin.reflect
val testBundle = libs.bundles.test

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    // Spring Boot BOM으로 의존성 버전 통합 관리
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<org.gradle.testing.jacoco.tasks.JacocoReport> {
        dependsOn(tasks.withType<Test>())
        reports {
            html.required.set(true)
            xml.required.set(false)
            csv.required.set(false)
        }
    }

    // 모든 모듈 공통 의존성 (버전은 BOM이 관리하므로 문자열로 선언)
    dependencies {
        "implementation"(kotlinReflect)

        "testImplementation"(testBundle)
    }
}
