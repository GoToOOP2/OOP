plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)        // JPA 엔티티용 no-arg 생성자 자동 생성
}

// infrastructure는 domain만 의존한다 (application, presentation은 볼 수 없다)
dependencies {
    implementation(project(":domain"))

    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)
}
