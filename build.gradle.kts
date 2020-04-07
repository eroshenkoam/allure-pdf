plugins {
    java
    id("com.palantir.graal") version("0.6.0")
}

group = "org.example"
version = "1.0-SNAPSHOT"

description = "Allure Server Java Client"

tasks.withType(Wrapper::class) {
    gradleVersion = "6.1.1"
}

graal {
    graalVersion(project.property("graalVersion") as String?)

    mainClass("io.eroshenkoam.allure.AllurePDF")
    outputName("allure-pdf")
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.1.4")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("io.qameta.allure:allure-model:2.13.1")
    implementation("com.github.librepdf:openpdf:1.3.13")
    implementation("info.picocli:picocli:4.1.4")
    implementation("commons-io:commons-io:2.6")

    testImplementation("junit:junit:4.12")
}
