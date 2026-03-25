plugins {
    alias(libs.plugins.kotlin.spring)
}

// presentation은 application만 의존한다
dependencies {
    implementation(project(":application"))

    implementation(libs.spring.boot.starter.web)
}
