plugins {
    java
    application
    id("com.jfrog.bintray") version "1.8.5"
}

group = "io.github.eroshenkoam"

description = "Allure PDF"

val gradleScriptDir by extra("${rootProject.projectDir}/gradle")

tasks.withType(Wrapper::class) {
    gradleVersion = "6.1.1"
}

application {
    mainClassName = "io.github.eroshenkoam.allure.AllurePDF"
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.1.4")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("io.qameta.allure:allure-model:2.13.1")

    implementation("org.slf4j:slf4j-log4j12:1.7.25")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("commons-logging:commons-logging:1.2")

    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("com.github.librepdf:openpdf:1.3.13")
    implementation("org.apache.xmlgraphics:fop:2.4") {
        exclude(group = "com.sun.media", module = "jai-codec")
        exclude(group = "javax.media", module = "jai-core")
    }

    implementation("info.picocli:picocli:4.1.4")
    implementation("commons-io:commons-io:2.6")

    testImplementation("junit:junit:4.12")
}

val sourceJar by tasks.creating(Jar::class) {
    from(sourceSets.getByName("main").allSource)
    archiveClassifier.set("sources")
}

val javadocJar by tasks.creating(Jar::class) {
    from(tasks.getByName("javadoc"))
    archiveClassifier.set("javadoc")
}

apply(from = "$gradleScriptDir/maven.gradle")


artifacts.add("archives", sourceJar)
artifacts.add("archives", javadocJar)

tasks.withType(Javadoc::class) {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

val startScripts by tasks.existing(CreateStartScripts::class) {
    applicationName = "allure-pdf"
    classpath = classpath?.plus(files("src/lib/config"))
    doLast {
        unixScript.writeText(unixScript.readText()
                .replace(Regex("(?m)^APP_HOME="), "export APP_HOME=")
                .replace("\$(uname)\" = \"Darwin", "")
        )
    }
}

tasks.build {
    dependsOn(tasks.installDist)
}
