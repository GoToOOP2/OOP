plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    "implementation"(project(":oop-domain"))
    "implementation"(libs.spring.boot.jpa)
    "runtimeOnly"(libs.postgresql)
}
