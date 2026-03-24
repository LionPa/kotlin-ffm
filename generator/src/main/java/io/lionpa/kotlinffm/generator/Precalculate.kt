package io.lionpa.kotlinffm.generator

import java.io.File
import java.text.ParseException

object Precalculate {

    val structsInfo = HashMap<String, StructInfo>()

    lateinit var struct : Regex
    val functionRegex = Regex("([^()]+)\\(([^()]+)\\)")
    val splitRegex = Regex("([^,]+)(?:\\s*,\\s*|$)")
    val simpleLayoutRegex = Regex("(\\w+)\\.layout")
    val commentRegex = Regex("/\\*(?:.|[\\r\\n])*?\\*/|//.*")
    val layoutSplit = Regex(",\\s*(?![^()]*\\))")
    val packageRegex = Regex("package\\s+([^\\s;]+)")

    fun findStructures(dir : File) {
        traverse(dir) {
            findStructuresInFile(this)
        }
    }

    private fun findStructuresInFile(file: File) {
        val text = file.readText().replace(commentRegex, "")

        val structs = struct.findAll(text)
        val packageName = packageRegex.find(text)!!.groups[1]!!.value

        for (struct in structs){
            val name = struct.groups[1]!!.value
            println(name)
            val curlyBraces = if (struct.groups[4] == null)
                null
            else
                struct.groups[4]!!.value

            val info = StructInfo(
                packageName,
                name,
                struct.groups[3]!!.value,
                curlyBraces,
                ArrayList(),
                false
            )

            structsInfo[name] = info
        }
    }

    fun calculateFields(){
        for (structInfo in structsInfo.values){
            calculateStruct(structInfo)
        }
    }

    fun calculateStruct(structInfo: StructInfo){
        if (structInfo.calculated) return

        for (field in structInfo.layout.split(layoutSplit)) {
            val field = "$field)"
            val result = functionRegex.find(field) ?: continue

            val func = result.groups[1]!!.value.trim()
            val value = result.groups[2]!!.value.trim().replace("\"","")

            if (func.contains("layout")) {
                val res = layoutRegex.find(field)

                if (res == null) {
                    println("$field layoutRegex not found")
                    return
                }

                val struct = res.groups[1]!!.value
                val name = res.groups[2]!!.value

                calculateStruct(structsInfo[struct]!!)

                val layoutSize = structsInfo[struct]!!.size()

                structInfo.fields.add(StructField(name, layoutSize, structInfo.offset(), structInfo.offsetAdditions()))
                continue
            }

            if (func == "padding") {
                val structField = StructField(null, value.toLong(), structInfo.offset(), structInfo.offsetAdditions())

                structInfo.fields.add(structField)
                continue
            }

            if (func == "array") {
                calculateArray(structInfo, value)
                continue
            }


            val fixedSize = sizeOf(func)

            val structField = StructField(value, fixedSize, structInfo.offset(), structInfo.offsetAdditions())

            structInfo.fields.add(structField)
        }

        structInfo.calculated = true
    }

    fun calculateArray(structInfo : StructInfo, value: String){
        val split = splitRegex.findAll(value).toList()

        val offsetAdditions = structInfo.offsetAdditions()

        val name = split[0].groups[1]!!.value
        val size = split[1].groups[1]!!.value
        val layout = split[2].groups[1]!!.value.trim()

        var layoutSize : Long

        if (layout.contains("layout")) {
            val struct = simpleLayoutRegex.find(layout)!!.groups[1]!!.value
            calculateStruct(structsInfo[struct]!!)

            layoutSize = structsInfo[struct]!!.size()

            offsetAdditions.addAll(structsInfo[struct]!!.offsetAdditions())
        } else {
            layoutSize = sizeOf(layout)
        }

        offsetAdditions.add(" + ($size * $layoutSize)")


        val structField = StructField(name, 0, structInfo.offset(), offsetAdditions)

        structInfo.fields.add(structField)
    }

    fun sizeOf(type: String) : Long {
        return when (type) {
            "address" -> 8.toLong()
            "long" -> 8.toLong()
            "double" -> 8.toLong()
            "float" -> 4.toLong()
            "int" -> 4.toLong()
            "short" -> 2.toLong()
            "byte" -> 1.toLong()
            "bool" -> 1.toLong()

            else -> throw ParseException("Nonexisting type! $type", 0)
        }
    }

    fun traverse(file: File, block : File.() -> Unit){
        if (file.isDirectory)
            for (s in file.listFiles()) traverse(s, block)

        if (file.name.endsWith(".kt")) block.invoke(file)
    }
}