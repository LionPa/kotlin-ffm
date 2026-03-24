plugins {
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4"
    kotlin("jvm") version "2.2.20"
    id("maven-publish")
}

group = "io.lionpa.kotlinffm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(24)
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xno-param-assertions")
}

// Example task for generator
tasks.register<JavaExec>("generateStruct") {
    group = "KotlinFFM"

    mainClass.set("io.lionpa.kotlinffm.generator.MainKt")
    workingDir = project.projectDir

    classpath = files("PATH/TO/generator-1.0.jar")

    args = listOf(
        "build/generator/Example.kt", // Where you want to generate new file
        "io.lionpa.kotlinffm", // File package
        "Struct" // Optional. Need if your project has struct-like classes. Example: "Struct, Component"
    )
}
