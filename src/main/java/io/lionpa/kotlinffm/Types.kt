@file:Suppress("NOTHING_TO_INLINE")

package io.lionpa.kotlinffm

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.SequenceLayout
import java.lang.foreign.ValueLayout

fun address(name: String) : MemoryLayout = ValueLayout.ADDRESS.withName(name).withByteAlignment(8)

fun byte(name: String) : MemoryLayout = ValueLayout.JAVA_BYTE.withName(name)

fun bool(name: String) : MemoryLayout = ValueLayout.JAVA_BOOLEAN.withName(name)

fun char(name: String) : MemoryLayout = ValueLayout.JAVA_CHAR.withName(name)

fun short(name: String) : MemoryLayout = ValueLayout.JAVA_SHORT.withName(name)

fun int(name: String) : MemoryLayout = ValueLayout.JAVA_INT.withName(name)

fun long(name: String) : MemoryLayout = ValueLayout.JAVA_LONG.withName(name)

fun float(name: String): MemoryLayout = ValueLayout.JAVA_FLOAT.withName(name)

fun double(name: String) : MemoryLayout = ValueLayout.JAVA_DOUBLE.withName(name)

fun padding(byte: Long) : MemoryLayout = MemoryLayout.paddingLayout(byte)

fun array(name: String, size: Int, layout: MemoryLayout) : SequenceLayout =
    MemoryLayout.sequenceLayout(size.toLong(), layout).withName(name)

fun array(name: String, size: Long, layout: MemoryLayout) : SequenceLayout =
    MemoryLayout.sequenceLayout(size, layout).withName(name)


// Only for code generation // TODO Add to generator
fun string(name: String) : MemoryLayout = ValueLayout.ADDRESS.withName(name).withByteAlignment(8)


/* DEBUG */

inline fun address(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.ADDRESS.withName(name).withByteAlignment(8) else null

inline fun byte(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_BYTE.withName(name) else null

inline fun bool(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_BOOLEAN.withName(name) else null

inline fun char(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_CHAR.withName(name) else null

inline fun short(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_SHORT.withName(name) else null

inline fun int(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_INT.withName(name) else null

inline fun long(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_LONG.withName(name) else null

inline fun float(include: Boolean, name: String): MemoryLayout? = if (include) ValueLayout.JAVA_FLOAT.withName(name) else null

inline fun double(include: Boolean, name: String) : MemoryLayout? = if (include) ValueLayout.JAVA_DOUBLE.withName(name) else null

inline fun padding(include: Boolean, byte: Long) : MemoryLayout? = if (include) MemoryLayout.paddingLayout(byte) else null

inline fun array(include: Boolean, name: String, size: Int, layout: MemoryLayout) : SequenceLayout? =
    if (include) MemoryLayout.sequenceLayout(size.toLong(), layout).withName(name)
    else null

inline fun array(include: Boolean, name: String, size: Long, layout: MemoryLayout) : SequenceLayout? =
    if (include) MemoryLayout.sequenceLayout(size, layout).withName(name)
    else null


val address : AddressLayout = ValueLayout.ADDRESS

val byte : ValueLayout.OfByte = ValueLayout.JAVA_BYTE

val bool : ValueLayout.OfBoolean = ValueLayout.JAVA_BOOLEAN

val char : ValueLayout.OfChar = ValueLayout.JAVA_CHAR

val short : ValueLayout.OfShort = ValueLayout.JAVA_SHORT

val int : ValueLayout.OfInt = ValueLayout.JAVA_INT

val long : ValueLayout.OfLong = ValueLayout.JAVA_LONG

val float : ValueLayout.OfFloat = ValueLayout.JAVA_FLOAT

val double : ValueLayout.OfDouble = ValueLayout.JAVA_DOUBLE