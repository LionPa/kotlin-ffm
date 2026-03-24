package io.lionpa.kotlinffm

import io.lionpa.kotlinffm.internal.NativeLibrary
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator

object NativeAllocator : SegmentAllocator {

    override fun allocate(byteSize: Long, byteAlignment: Long): MemorySegment {
        return NativeLibrary.calloc(byteSize)
    }

    fun free(memorySegment: MemorySegment){
        NativeLibrary.free(memorySegment)
    }
}