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
            name = "Vin"
            url = "https://github.com/vin51435/declutter-tree-intellij"
        }

        ideaVersion {
            sinceBuild = properties("platformSinceBuild")
            // JetBrains standard: unsetting untilBuild allows the plugin to be compatible with all current
            // and future IDE versions (e.g. 242, 243, 251, 262+, and Android Studio)
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    patchPluginXml {
        untilBuild = provider { null }
    }

    test {
        useJUnitPlatform()
    }
}
