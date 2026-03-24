# Kotlin-FFM
Zero-overhead Kotlin wrapper for Java FFM. Easy native library linking and memory manipulation 

---

## Using in Your Projects

### Gradle

Add the dependency and JitPack repository:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.LionPa:kotlin-ffm:main-SNAPSHOT")
}
```

---

## Guide

### Arenas

This wrapper provides **arena blocks**. Within an arena block, you can allocate memory and structs. After exiting the block, all memory allocated in it becomes invalid.

```kotlin
arena {
    // Memory allocations
}
// Allocations are invalidated
```

Available arena blocks:

1. `arena { }` — uses `Arena.ofConfined()`
2. `gcArena { }` — uses `Arena.ofAuto()`
3. `sharedArena { }` — uses `Arena.ofShared()`
4. `native { }` — uses `NativeAllocator`

The first three arenas are standard FFM arenas. The `native` block is different: it **must only be used when native code will manage (and eventually free) the memory**.

---

### Examples

#### Temporary data (Java owns memory)

```kotlin
arena {
    val a = allocate(int)
    val b = allocate(int)
        
    a.set(int, 0L, 10)
    b.set(int, 0L, 32)

    val result = CoolMathLib.sum(a, b) as Int // Native code does NOT store your data
    println(result)
}
// Memory is invalid here
```

In this example, the native code does not store the data, so memory becomes invalid after the arena block exits.

---

#### Persistent data (native owns memory)

```kotlin
native {
    val a = allocate(int)
    val b = allocate(int)

    a.set(int, 0L, 10)
    b.set(int, 0L, 32)

    CoolMathLib.storeSumAtIndex(201, a, b) // Native code stores your data
}

// Somewhere else
val result = CoolMathLib.getSumAtIndex(201) as Int
println(result)
```

Here, you **cannot use other arena blocks** for this memory because those would invalidate it. Native code is responsible for managing the memory.

---

### Structs

A struct defines a data layout in memory.

#### Defining a layout

```kotlin
import io.lionpa.kotlinffm.*

object Position : Struct(
    float("x"),
    float("y"),
    float("z"),
)
```

This creates a layout, but you cannot access struct data yet. To access data, define var handles:

```kotlin
import io.lionpa.kotlinffm.*

object Position : Struct(
    float("x"),
    float("y"),
    float("z"),
) {
    val x = varHandle<Float>("x")
    val y = varHandle<Float>("y")
    val z = varHandle<Float>("z")
}
```

---

#### Accessing struct data (without code generation)

```kotlin
arena {
    val pos = allocate(Position.layout) // Memory segment of size Position
        
    pos[Position.x] = 10f // Equivalent to Position.x.varHandle.set(pos, 0L, 10f)
    pos[Position.y] = 32f
    pos[Position.z] = 0f

    println(pos[Position.x] + pos[Position.y]) // Output: 42.0
}
```

---

### Libraries

With Kotlin-FFM, you can easily bind native libraries.

#### Example

```kotlin
object Flecs : Library(
    PlatformLibraryFile(OS.WINDOWS, Arch.AMD64) { File("library/flecs.dll") }
) {
    val ecs_id_EcsPipeline = symbol(ecsId(EcsPipeline))

    val createWorld = method("ecs_init", long)
    val progressWorld = method("ecs_progress", bool, long, float)
    val deleteWorld = method("ecs_fini", int, long)

    val createEntity = method("ecs_new", long, long)
    val deleteEntity = void("ecs_delete", long, long)

    val setComponent = void("ecs_set_id", long, long, long, long, address)
    val getComponent = method("ecs_get_id", address, long, long, long)
}
```

For multiplatform applications, you need six native libraries (2 architectures: x86_64, ARM × 3 OS: Windows, macOS, Linux). At runtime, your application only requires **one library file**.

* `method(name, returnType, argumentTypes...)` — defines a native method
* `void(name, argumentTypes...)` — defines a void native method
* `symbol(name)` — retrieves the address of a native variable

---

### Code Generator

After updating structs, you can run the Code Generator to produce a Kotlin utility file.

#### Accessing struct data (after code generation)

```kotlin
arena {
    val pos = position {
        x = 10f
        y = 32f
        z = 0f
    }

    println(pos.x + pos.y) // Output: 42.0
}
```

Performance remains the same as manual access.

---

#### Example Gradle task for generator

```kotlin
tasks.register<JavaExec>("generateStruct") {
    group = "KotlinFFM"

    mainClass.set("io.lionpa.kotlinffm.generator.MainKt")
    workingDir = project.projectDir

    classpath = files("PATH/TO/generator-1.0.jar")

    args = listOf(
        "build/generator/Example.kt", // Output file where generated code will be written
        "io.lionpa.kotlinffm",        // Package for output
        "Struct"                      // Optional: comma-separated list of struct-like classes. Example: "Struct, Component"
    )
}
```

#### Download code generator
Latest version of code generator you can download [HERE](https://github.com/LionPa/kotlin-ffm/releases/tag/generator)

---
