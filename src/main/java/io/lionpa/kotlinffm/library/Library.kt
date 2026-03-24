package io.lionpa.kotlinffm.library

import java.lang.foreign.*
import java.lang.invoke.MethodHandle

open class Library {

    val lookup : SymbolLookup

    constructor(vararg libs: PlatformLibraryFile){
        val os = OS.current()
        val arch = Arch.current()

        val lib = libs.find { lib -> lib.os == os && lib.arch == arch }

        if (lib == null) throw Exception("Not found library for $os-$arch!")

        val file = lib.library.invoke() ?: throw Exception("File of library not founded! Platform: $os-$arch!")

        lookup = SymbolLookup.libraryLookup(file.absolutePath, Arena.global())
    }

    fun method(name: String, returnType: MemoryLayout, vararg argLayouts : MemoryLayout) : MethodHandle {
        return linker.downcallHandle(
            lookup.findOrThrow(name),
            FunctionDescriptor.of(returnType, *argLayouts)
        )
    }

    fun void(name: String, vararg argLayouts : MemoryLayout) : MethodHandle {
        return linker.downcallHandle(
            lookup.findOrThrow(name),
            FunctionDescriptor.ofVoid(*argLayouts)
        )
    }

    fun symbol(name: String) : MemorySegment {
        return lookup.find(name).orElseThrow()
    }

    companion object {
        val linker: Linker = Linker.nativeLinker()
    }

}