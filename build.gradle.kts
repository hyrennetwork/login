plugins {
    kotlin("jvm") version "1.5.10"
}

group = "net.hyren"
version = "0.1-ALPHA"

repositories {
    mavenCentral()

    maven("https://repository.hyren.net/") {
        credentials {
            username = System.getenv("MAVEN_USERNAME")
            password = System.getenv("MAVEN_PASSWORD")
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    jar {
        archiveFileName.set("${project.name}.jar")
    }
}

dependencies {
    // kotlin
    compileOnly(kotlin("stdlib"))

    // paper-spigot-server
    compileOnly("org.github.paperspigot:paper-spigot-server:1.8.8-R0.1-SNAPSHOT")

    // waterfall chat
    compileOnly("io.github.waterfallmc:waterfall-chat:1.16-R0.5-SNAPSHOT")

    // minecraft-server
    compileOnly("net.hyren:minecraft-server:1.8.8-SNAPSHOT")

    // exposed
    compileOnly("org.jetbrains.exposed:exposed-dao:0.31.1")

    // core
    compileOnly("net.hyren:core-shared:0.1-ALPHA")
    compileOnly("net.hyren:core-spigot:0.1-ALPHA")
}
