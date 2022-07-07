plugins {
    application
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.racci"
version = if (System.getenv("GITHUB_RUN_NUMBER") == null) "DEV" else {
    val runNumber = System.getenv("GITHUB_RUN_NUMBER")
    val major = getMajor(runNumber)
    val minor = getMinor(runNumber)
    val patch = getPatch(runNumber)
    "$major.$minor.$patch"
}

fun getMajor(input: String): String {
    val length = input.length
    return if (length > 2) {
        var str = ""
        for ((i, c) in input.withIndex()) {
            if (i < length - 2) {
                str += c
            } else break
        }
        str
    } else "0"
}

kotlin.jvmToolchain { (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8)) }

fun getMinor(input: String): Char {
    val length = input.length
    return if (length > 1) {
        input.dropLast(1).last()
    } else '0'
}

fun getPatch(input: String): Char {
    val length = input.length
    return if (length < 2) input[0] else input.last()
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.racci.pgen.MainKt")
}

tasks.shadowJar {
    archiveClassifier.set("")
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
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("com.github.ajalt.mordant:mordant:2.0.0-beta4")
            }
        }
        val desktopMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-core:2.0.0-beta-1")
                implementation("io.ktor:ktor-client-curl:2.0.2")
                implementation("com.soywiz.korlibs.korio:korio:2.5.3")
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
                implementation("io.ktor:ktor-client-java:2.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")
            }
        }
    }
}
