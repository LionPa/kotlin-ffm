package io.lionpa.kotlinffm

import java.lang.foreign.MemorySegment
import java.lang.invoke.VarHandle

@JvmInline
value class TypeVarHandle<T>(val varHandle: VarHandle)

operator fun <T> MemorySegment.set(typeVarHandle: TypeVarHandle<T>, value : T){
    typeVarHandle.varHandle.set(this, 0L, value)
}

inline operator fun <reified T> MemorySegment.get(typeVarHandle: TypeVarHandle<T>) : T {
    return typeVarHandle.varHandle.get(this, 0L) as T
}