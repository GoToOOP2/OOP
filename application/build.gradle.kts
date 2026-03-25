plugins {
    alias(libs.plugins.kotlin.spring)
}

// application은 domain만 의존한다 (infrastructure, presentation은 볼 수 없다)
dependencies {
    implementation(project(":domain"))
}
