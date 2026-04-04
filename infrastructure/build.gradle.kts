plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)        // JPA 엔티티용 no-arg 생성자 자동 생성
}

// Domain의 Port를 구현(Adapter)하며, common을 참조한다
dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation(libs.bundles.spring.data)
}
