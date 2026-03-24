plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "io.lionpa.kotlinffm.generator"
version = "1.0"

kotlin {
    jvmToolchain(24)
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.lionpa.kotlinffm.generator.MainKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "io.lionpa.kotlinffm.generator.MainKt")
    }

    from(configurations.runtimeClasspath.get().map {
        println(it)
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
