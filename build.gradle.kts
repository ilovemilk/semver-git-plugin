plugins {
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.21"
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
            version = "0.0.1-SNAPSHOT"

            from(components["java"])
        }
    }
}