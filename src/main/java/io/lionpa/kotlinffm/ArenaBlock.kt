package io.lionpa.kotlinffm

import java.lang.foreign.Arena

inline fun <T> arena(crossinline block: Arena.() -> T) : T{
    val arena = Arena.ofConfined()
    try {
        return block.invoke(arena)
    } catch (e : Exception){
        throw e
    } finally {
        arena.close()
    }

}