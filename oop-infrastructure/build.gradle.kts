plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    "implementation"(project(":oop-common"))
    "implementation"(project(":oop-domain"))
    "implementation"(libs.bundles.spring.infrastructure)
    "implementation"(libs.bundles.jjwt)
    "implementation"(libs.bcrypt)
    "runtimeOnly"(libs.postgresql)
    "testRuntimeOnly"(libs.h2)
}
