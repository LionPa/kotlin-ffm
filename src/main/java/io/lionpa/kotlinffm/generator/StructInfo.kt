package io.lionpa.kotlinffm.generator

data class StructInfo(
    val packageName : String,
    val structName : String,
    val layout : String,
    val curlyBraces : String?,
    val fields: MutableList<StructField>,
    var calculated : Boolean
) {
    fun offset() : Long {
        var offset = 0.toLong()
        for (field in fields){
            offset += field.size
        }
        return offset
    }

    fun size() : Long {
        var size = 0.toLong()
        for (field in fields){
            size += field.size
        }
        return size
    }

    fun offsetAdditions() : MutableList<String> {
        if (fields.isEmpty()) return mutableListOf()
        return fields.last().offsetAdditions.toMutableList()
    }

    fun findFieldWithName(name: String) : StructField? {
        for (field in fields){
            if (field.name == name) return field
        }
        return null
    }
}