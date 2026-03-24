package io.lionpa.kotlinffm.library

enum class OS() {
    WINDOWS("win", "windows"),
    LINUX("linux"),
    MACOS("macos", "mac");

    constructor(vararg names: String) : this() {
        for (name in names){
            OSMap.map[name.lowercase()] = this
        }
    }

    companion object {
        fun of(name: String) : OS {
            return OSMap.map[name.lowercase()] ?: throw Exception("Unknow os $name")
        }

        fun current() : OS {
            val name = System.getProperty("os.name").lowercase()

            if (name.contains("win")) return WINDOWS
            if (name.contains("linux")) return LINUX
            if (name.contains("mac")) return MACOS

            throw Exception("System running on unknow OS $name")
        }
    }
}

private object OSMap {
    val map = HashMap<String, OS>()
}