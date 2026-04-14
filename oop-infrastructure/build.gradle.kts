plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    "implementation"(project(":oop-domain"))
    "implementation"(libs.bundles.spring.infrastructure)
    "runtimeOnly"(libs.postgresql)
    "testRuntimeOnly"(libs.h2)
    "implementation"(libs.spring.security.crypto)
    "implementation"(libs.jjwt.api)
    "runtimeOnly"(libs.jjwt.impl)
    "runtimeOnly"(libs.jjwt.jackson)
}
