plugins {
    id("java")
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

fun properties(key: String) = providers.gradleProperty(key)

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(properties("platformVersion").get())
        bundledPlugin("com.intellij.java")
        bundledPlugin("org.jetbrains.kotlin")
    }

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(properties("jvmToolchainVersion").get().toInt())
}

intellijPlatform {
    pluginConfiguration {
        id = properties("pluginGroup")
        name = properties("pluginName")
        version = properties("pluginVersion")

        vendor {
            name = "Senior IntelliJ Platform Team"
            url = "https://github.com/example/project-view-bottom-files"
        }

        ideaVersion {
            sinceBuild = properties("platformSinceBuild")
            // untilBuild is intentionally omitted so the plugin is forward-compatible
            // with all versions >= 242 (2024.2+, 2024.3+, 2025.x, 2026.x, and Android Studio)
        }
    }

    signing {
        // Ready for JetBrains Marketplace signing certificates
    }

    publishing {
        // Ready for JetBrains Marketplace publishing token
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
