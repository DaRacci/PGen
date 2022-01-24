plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "dev.racci"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {

    for (target in listOf(linuxX64(), mingwX64())) {
        target.apply {
            compilations["main"].enableEndorsedLibs = false
            binaries {
                executable {
                    entryPoint = "main"
                }
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
                implementation("io.ktor:ktor-client-core:1.6.7")
                implementation("io.ktor:ktor-client-curl:1.6.7")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("com.soywiz.korlibs.korio:korio:2.4.10")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val linuxX64Main by getting { dependsOn(commonMain) }
        val mingwX64Main by getting { dependsOn(commonMain) }
    }
}
