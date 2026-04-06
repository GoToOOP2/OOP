plugins {
    alias(libs.plugins.spring.boot)
}

dependencies {
    "implementation"(project(":oop-common"))
    "implementation"(project(":oop-domain"))
    "implementation"(project(":oop-application"))
    "implementation"(project(":oop-infrastructure"))
    "implementation"(project(":oop-presentation"))
    "implementation"(libs.bundles.spring.web)
    "implementation"(libs.springdoc.openapi)
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
