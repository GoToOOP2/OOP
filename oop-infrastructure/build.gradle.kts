plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    "implementation"(project(":oop-domain"))
    "implementation"(libs.bundles.spring.infrastructure)
    "runtimeOnly"(libs.postgresql)
}
