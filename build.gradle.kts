import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.21"
    id("org.jetbrains.dokka") version "0.9.17"
}

group = "io.wusa"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
}

repositories {
    mavenCentral()
}

// Create dokka Jar task from dokka task output
val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    // dependsOn(tasks.dokka) not needed; dependency automatically inferred by from(tasks.dokka)
    from((tasks.getByName("dokka") as DokkaTask).outputDirectory)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(dokkaJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}