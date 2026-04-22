plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    "implementation"(project(":oop-domain"))
    "implementation"(libs.bundles.spring.infrastructure)
    "implementation"(libs.spring.security.crypto)
    "implementation"(libs.jjwt.api)
    "runtimeOnly"(libs.postgresql)
    "runtimeOnly"(libs.jjwt.impl)
    "runtimeOnly"(libs.jjwt.jackson)
    "testImplementation"(libs.mockito.kotlin)
    "testRuntimeOnly"(libs.h2)
}
