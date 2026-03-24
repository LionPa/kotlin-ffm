package io.lionpa.kotlinffm

import java.lang.foreign.Arena
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout

open class Struct {
    val layout : StructLayout
    val byteSize : Long
    val structAlignment : Long

    constructor(vararg args : MemoryLayout){
        layout = MemoryLayout.structLayout(*args)
        byteSize = layout.byteSize()
        structAlignment = layout.byteAlignment()
    }

    fun <T> varHandle(varName: String) : TypeVarHandle<T> {
        val varHandle = layout.varHandle(MemoryLayout.PathElement.groupElement(varName))

        return TypeVarHandle(varHandle)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun invoke(arena: Arena) : MemorySegment {
        return arena.allocate(this.layout)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun invoke(arena: Arena, size: Long) : MemorySegment {
        return arena.allocate(sequenceLayout(size, layout))
    }
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun StructLayout.invoke(name: String) : StructLayout{
    return this.withName(name)
}

