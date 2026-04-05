plugins {
    alias(libs.plugins.kotlin.spring)
}

// Application의 UseCase를 호출하는 Inbound Adapter이다
dependencies {
    implementation(project(":application"))
    implementation(project(":common"))

    implementation(libs.bundles.spring.web)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    testImplementation(libs.bundles.test.mock)
}
