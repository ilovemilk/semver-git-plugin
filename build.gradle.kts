plugins {
    `maven-publish`
    `java-gradle-plugin`
    `jacoco`
    kotlin("jvm") version "1.3.21"
    id("com.gradle.plugin-publish") version "0.11.0"
}

group = "io.wusa"
version = "2.3.5"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("io.mockk:mockk:1.9")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}

repositories {
    jcenter()
    mavenCentral()
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports.xml.isEnabled = true
    reports.html.isEnabled = false
    dependsOn(tasks.named("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "5.1.1"
}

gradlePlugin {
    plugins {
        create("semverGitPlugin") {
            id = "io.wusa.semver-git-plugin"
            displayName = "semver-git-plugin"
            description = "Project versioning based on semantic versioning via git tags"
            implementationClass = "io.wusa.SemverGitPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/ilovemilk/semver-git-plugin"
    vcsUrl = "https://github.com/ilovemilk/semver-git-plugin"
    description = "Project versioning based on semantic versioning via git tags"
    tags = listOf("git", "kotlin", "semver", "semantic-versioning", "version", "semantic", "release")
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
