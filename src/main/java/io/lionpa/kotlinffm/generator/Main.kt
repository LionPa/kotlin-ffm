package io.lionpa.kotlinffm.generator

import java.io.File

val varHandle = Regex("val\\s+([^\\s=]+)\\s*=\\s*varHandle<([^>]+)>\\([\"']([^\"']+)[\"']\\)")
val layoutRegex = Regex("(\\w+)\\.layout\\(\"([^\"]*)\"\\)")


fun main(args: Array<String>) {
    println("Generating...")
    val dir = File(".")

    val fileName = args.getOrNull(0)
    val packageName = args.getOrNull(1)
    val structClasses = args.getOrNull(2) ?: "Struct"

    val structClassesSplit = Precalculate.splitRegex.findAll(structClasses).map { m -> m.groups[1]!!.value }.toList()

    Precalculate.struct = Regex("object\\s+([^\\s(:]+)\\s*:\\s*(${structClassesSplit.joinToString("|")})\\(((?:[^()]|\\([^()]*\\))*)\\)\\s*(\\{([\\s\\S]*?)})?")

    Precalculate.findStructures(dir)
    Precalculate.calculateFields()

    val result = StringBuilder()
    val imports = defaultImports()

    result.append(
        """
            interface MemSeg {
                fun memorySegment() : MemorySegment
                fun address() : Long
                fun struct() : Struct
            }
        """.trimIndent()
    )

    for (structInfo in Precalculate.structsInfo.values){
        result.append(generateMemorySegment(structInfo, imports))
    }

    val prepend = """@file:Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")

package ${packageName ?: "//TODO"}

${imports.joinToString("\n") }
"""

    write(prepend + result.toString(), fileName)
    println("Generated ${Precalculate.structsInfo.size} struct(s)!")
}

fun defaultImports() : MutableSet<String> {
    val imports = mutableSetOf<String>()

    imports.add("import io.lionpa.kotlinffm.*")
    imports.add("import java.lang.foreign.MemorySegment")
    imports.add("import java.lang.foreign.SegmentAllocator")

    return imports
}


fun generateMemorySegment(info: StructInfo, imports : MutableSet<String>) : String {
    val result = StringBuilder()

    val structName = info.structName

    imports.add("import ${info.packageName}.$structName")

    addGenerationComment(result, structName)

    result.append("\n@JvmInline\nvalue class ${structName}MemorySegment(val memorySegment: MemorySegment) : MemSeg {\n")
    result.append("""
    override fun memorySegment(): MemorySegment = memorySegment
    override fun address(): Long = memorySegment.address()
    override fun struct(): Struct = $structName
""")

    if (info.curlyBraces != null)
        for (varHandle in varHandle.findAll(info.curlyBraces)) {

            val ktVarName = varHandle.groups[1]!!.value
            val type = varHandle.groups[2]!!.value
            val varName = varHandle.groups[3]!!.value

            val field = info.findFieldWithName(varName)!!


            result.append(
                """
    inline var $varName: $type // OFFSET ${field.offset} ${field.offsetAdditions}
        inline get() = $structName.$ktVarName.varHandle.get(memorySegment, 0L) as $type
        inline set(value) = $structName.$ktVarName.varHandle.set(memorySegment, 0L, value) 
"""
            )
        }

    for (embedded in layoutRegex.findAll(info.layout)) {
        val struct = embedded.groups[1]!!.value
        val varName = embedded.groups[2]!!.value

        val field = info.findFieldWithName(varName)!!

        val string = """
    inline val $varName: ${struct}MemorySegment
        inline get() = ${struct}MemorySegment(memorySegment.asSlice(${field.offset}L${field.offsetAdditions.joinToString("")}, $struct.layout))
"""

        result.append(string)
    }


    result.append("}\n\n")


    addWrapperConstructors(result, structName)
    addArray(result, structName)

    return result.toString()
}

private fun addGenerationComment(builder: StringBuilder, structName: String){
    builder.append(
        """
            
            /**
            * Generated from [$structName]
            */""".trimIndent()
    )
}


private fun addWrapperConstructors(builder: StringBuilder, name: String){
    val firstSmall = name.replaceFirstChar { c -> c.lowercase() }
    builder.append(
        """
            @Suppress("NOTHING_TO_INLINE")
            inline fun SegmentAllocator.${firstSmall}() : ${name}MemorySegment {
                return ${name}MemorySegment(allocate(${name}.layout))
            }
            
            inline fun SegmentAllocator.${firstSmall}(crossinline block : ${name}MemorySegment.() -> Unit) : ${name}MemorySegment {
                val $firstSmall = ${name}MemorySegment(allocate(${name}.layout))
                block(${firstSmall})
                return $firstSmall
            }
            
            inline operator fun ${name}MemorySegment.invoke(crossinline block : ${name}MemorySegment.() -> Unit) = block(this)
            
            @Suppress("NOTHING_TO_INLINE")
            inline fun MemorySegment.as${name}() : ${name}MemorySegment {
                return ${name}MemorySegment(this)
            }
            
        """.trimIndent()
    )
}

private fun addArray(builder: StringBuilder, name: String){
    builder.append("""
        @JvmInline
        value class ${name}Array(val memorySegment: MemorySegment) {
            @Suppress("NOTHING_TO_INLINE")
            inline operator fun get(index: Int) : ${name}MemorySegment = memorySegment.asSlice(index * ${name}.byteSize, ${name}.byteSize).as${name}()
        }
        
        @Suppress("NOTHING_TO_INLINE")
        inline fun MemorySegment.as${name}Array() : ${name}Array {
            return ${name}Array(this)
        }
    """.trimIndent())
}

fun write(text: String, fileName: String?){
    val output = File(fileName ?: "kotlin-ffm-generator/Result.kt")

    output.parentFile.mkdirs()

    output.createNewFile()

    output.writeText(text)
}