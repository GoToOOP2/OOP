plugins {
    alias(libs.plugins.kotlin.spring)
}

// infrastructure는 domain만 의존한다 (application, presentation은 볼 수 없다)
dependencies {
    implementation(project(":domain"))
}
