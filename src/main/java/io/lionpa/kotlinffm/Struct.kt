package io.lionpa.kotlinffm

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
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
    inline operator fun invoke(allocator: SegmentAllocator) : MemorySegment {
        return allocator.allocate(this.layout)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun invoke(allocator: SegmentAllocator, size: Long) : MemorySegment {
        return allocator.allocate(sequenceLayout(size, layout))
    }
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun StructLayout.invoke(name: String) : StructLayout{
    return this.withName(name)
}

