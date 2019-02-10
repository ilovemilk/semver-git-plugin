plugins {
    `maven-publish`
    `java-gradle-plugin`
    `jacoco`
    kotlin("jvm") version "1.3.21"
}

group = "io.wusa"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}

repositories {
    mavenCentral()
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports.xml.isEnabled = true
    reports.html.isEnabled = true
    dependsOn(tasks.named("test"))
}

tasks.getting(Test::class) {
    useJUnitPlatform()
    dependsOn(tasks.named("cleanTest"))
}

gradlePlugin {
    plugins {
        create("semverGitPlugin") {
            id = "io.wusa.semver-git-plugin"
            implementationClass = "io.wusa.SemverGitPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.wusa"
            artifactId = "semver-git-plugin"

            from(components["java"])
        }
    }
}