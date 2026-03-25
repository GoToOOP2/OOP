plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
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
