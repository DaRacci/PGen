plugins {
    application
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.racci"
// this is kinda disgusting but it keeps it short
// TODO make this nicer
version = with(System.getenv("GITHUB_RUN_NUMBER") ?: "DEV") {
    if (this == "DEV") return@with this
    val split = this.toCharArray()
    val major = if (split.size > 3) {
        split.joinToString(".", limit = 2)
    } else if (split[0] == '0') 1 else split[0]
    "$major.${split.getOrElse(1) { '0' }}.${split.getOrElse(2) { '0' }}"
}

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

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

kotlin {

    explicitApi()

    jvm()

    infix fun <T> T.and(other: T) = listOf(this, other)

    (mingwX64() and linuxX64()).forEach { platform ->
        platform.compilations["main"].enableEndorsedLibs = false
        platform.binaries {
            executable {
                entryPoint = "dev.racci.pgen.main"
            }
        }
    }

    mingwX64 {
        val main by compilations.getting
        val libcurl by main.cinterops.creating
        val libssl by main.cinterops.creating
        val zlib by main.cinterops.creating
        val libcrypto by main.cinterops.creating
    }

    sourceSets {

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
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
                implementation("io.ktor:ktor-client-core:2.0.0-beta-1")
                implementation("io.ktor:ktor-client-curl:2.0.0-beta-1")
                implementation("com.soywiz.korlibs.korio:korio:2.4.10")
            }
        }
        val linuxX64Main by getting {
            dependsOn(desktopMain)
            dependencies {
            }
        }
        val mingwX64Main by getting {
            dependsOn(desktopMain)
            dependencies {
                // implementation("com.squareup.okhttp3:okhttp:4.9.3")
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-java:2.0.0-beta-1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")
            }
        }
    }
}
