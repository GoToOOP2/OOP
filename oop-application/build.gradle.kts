dependencies {
    "implementation"(project(":oop-common"))
    "implementation"(project(":oop-domain"))
    "implementation"(libs.bundles.spring.application)
    "testImplementation"(libs.mockito.kotlin)
    "testImplementation"(libs.fixture.monkey.starter)
    "testImplementation"(libs.fixture.monkey.kotlin)
}
