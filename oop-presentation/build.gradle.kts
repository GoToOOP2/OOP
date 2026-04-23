dependencies {
    "implementation"(project(":oop-common"))
    "implementation"(project(":oop-application"))
    "implementation"(libs.bundles.spring.web)
    "implementation"(libs.spring.boot.validation)
    "implementation"(libs.springdoc.openapi)
    "testImplementation"(libs.fixture.monkey.starter)
    "testImplementation"(libs.fixture.monkey.kotlin)
}
