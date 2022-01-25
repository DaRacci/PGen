plugins {
    application
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.racci"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.racci.pgen.MainKt")
}

tasks.shadowJar {
    val target = kotlin.targets.getAt("jvm")
    from(target.compilations["main"].output)
    val runtimeClasspath = target.compilations["main"].compileDependencyFiles
    mergeServiceFiles()
    configurations = listOf(runtimeClasspath)
}

kotlin {

    explicitApi()

    jvm()

    linuxX64() {
        compilations["main"].enableEndorsedLibs = false
        binaries {
            executable {
                entryPoint = "dev.racci.pgen.main"
            }
        }
    }

    sourceSets {

        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val desktopMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-core:1.6.7")
                implementation("io.ktor:ktor-client-curl:1.6.7")
                implementation("com.soywiz.korlibs.korio:korio:2.4.10")
            }
        }
        val linuxX64Main by getting { dependsOn(desktopMain) }
//        val mingwX64Main by getting { dependsOn(desktopMain) }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")
            }
        }
    }
}
