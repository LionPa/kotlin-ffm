package io.lionpa.kotlinffm.generator

data class StructField(val name: String?, val size: Long, val offset: Long, val offsetAdditions : List<String>)