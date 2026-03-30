plugins {
    id("org.springframework.boot")
}

dependencies {
    "implementation"(project(":oop-domain"))
    "implementation"(project(":oop-application"))
    "implementation"(project(":oop-infrastructure"))
    "implementation"(project(":oop-presentation"))
    "implementation"("org.springframework.boot:spring-boot-starter-web")
    "implementation"("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
