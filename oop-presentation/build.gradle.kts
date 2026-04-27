dependencies {
    "implementation"(project(":oop-common"))
    "implementation"(project(":oop-application"))
    "implementation"(libs.bundles.spring.web)
    "implementation"(libs.spring.boot.validation)
    "implementation"(libs.springdoc.openapi)
    "testImplementation"(libs.mockito.kotlin)
}
