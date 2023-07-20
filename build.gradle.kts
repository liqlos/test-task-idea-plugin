plugins {
    id("org.jetbrains.intellij") version "1.14.2"
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

group = "org.jetbrains"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2023.1.3")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    patchPluginXml {
        version.set("${project.version}")
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        
            """.trimIndent())
    }
}