package io.lionpa.kotlinffm.library

import java.io.File

data class PlatformLibraryFile(val os: OS, val arch: Arch, val library: () -> File?) {
    constructor(platform: String, library: () -> File?) : this(
        OS.of(platform.split("-")[0]),
        Arch.of(platform.split("-")[1]),
        library
    )
}
