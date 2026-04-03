plugins {
    alias(libs.plugins.kotlin.spring)
}

// presentation은 application과 common만 의존한다
dependencies {
    implementation(project(":application"))
    implementation(project(":common"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    testImplementation(libs.mockk)
}
