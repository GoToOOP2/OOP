plugins {
    alias(libs.plugins.kotlin.spring)
}

// Domain의 Inbound Port를 구현하며, Outbound Port를 사용한다
dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
}
