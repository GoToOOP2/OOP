plugins {
    val kotlinVersion = "1.9.24"
    val springBootVersion = "3.2.3"
    val dependencyManagementVersion = "1.1.4"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.springframework.boot") version springBootVersion apply false
    id("io.spring.dependency-management") version dependencyManagementVersion apply false
    jacoco
}

allprojects {
    group = "com.jaeyong.oop"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    // Spring Boot 의존성 버전 관리
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")
        }
    }

    dependencies {
        // 'add'를 사용하여 설정 이름을 명시적으로 지정 (IntelliJ 인식률 향상)
        add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
        add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin")
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
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
