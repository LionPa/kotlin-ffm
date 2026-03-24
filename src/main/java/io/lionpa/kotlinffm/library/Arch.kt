package io.lionpa.kotlinffm.library


enum class Arch() {
    AMD64("amd64", "x86_64"),
    ARM("arm");

    constructor(vararg names: String) : this() {
        for (name in names){
            ArchMap.map[name.lowercase()] = this
        }
    }

    companion object {
        fun of(name: String) : Arch {
            return ArchMap.map[name.lowercase()] ?: throw Exception("Unknow arch $name")
        }

        fun current() : Arch {
            val name = System.getProperty("os.arch")

            return of(name)
        }
    }
}

private object ArchMap {
    val map = HashMap<String, Arch>()
}