package io.lionpa.kotlinffm.internal

import io.lionpa.kotlinffm.address
import io.lionpa.kotlinffm.library.Library
import io.lionpa.kotlinffm.long
import java.lang.foreign.MemorySegment

internal object NativeLibrary : Library(linker.defaultLookup()) {

    private val calloc = method("calloc", address, long, long)
    //private val aligned_alloc = method("aligned_alloc", address, long, long)

    private val free = void("free", address)

    fun calloc(bytes: Long) : MemorySegment {
        return (calloc.invokeExact(1L, bytes) as MemorySegment).reinterpret(bytes)
    }

    //fun alloc(alignment : Long, bytes: Long) : MemorySegment {
    //    return aligned_alloc.invokeExact(alignment, bytes) as MemorySegment
    //}

    fun free(memorySegment: MemorySegment){
        free.invokeExact(memorySegment)
    }
}