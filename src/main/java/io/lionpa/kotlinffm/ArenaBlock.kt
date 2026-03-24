package io.lionpa.kotlinffm

import java.lang.foreign.Arena

inline fun <T> arena(crossinline block: Arena.() -> T) : T {
    val arena = Arena.ofConfined()
    try {
        return block.invoke(arena)
    } catch (e : Exception){
        throw e
    } finally {
        arena.close()
    }
}

inline fun <T> gcArena(crossinline block: Arena.() -> T) : T {
    val arena = Arena.ofAuto()
    try {
        return block.invoke(arena)
    } catch (e : Exception){
        throw e
    }
}

inline fun <T> sharedArena(crossinline block: Arena.() -> T) : T {
    val arena = Arena.ofShared()
    try {
        return block.invoke(arena)
    } catch (e : Exception) {
        throw e
    }
}

inline fun <T> native(crossinline block: NativeAllocator.() -> T) : T {
    try {
        return block.invoke(NativeAllocator)
    } catch (e : Exception){
        throw e
    }
}