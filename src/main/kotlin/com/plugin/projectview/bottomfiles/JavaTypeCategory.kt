package com.plugin.projectview.bottomfiles

/**
 * Defines the priority, sort weight, and sort prefix for each category of Java and Kotlin tree node.
 *
 * Sorting Hierarchy:
 * 1. Folders / Directories (Always on Top via platform sort weight)
 * 2. Interfaces (icon (I): Java interface, Kotlin interface, sealed interface)
 * 3. Abstract Classes (icon (C) with abstract marker: Java abstract class, Kotlin abstract/sealed class)
 * 4. Classes (icon (C): Java normal class, Kotlin class, data class, open class)
 * 5. Final Classes (icon (C) with final badge: Java final class)
 * 6. Objects (icon (O): Kotlin singleton object declaration)
 * 7. Records (icon (R): Java record)
 * 8. Enums (icon (E): Java enum, Kotlin enum class)
 * 9. Annotations (icon @: Java annotation, Kotlin annotation class)
 * 10. Kotlin Files (icon K: Kotlin files with top-level functions/properties)
 * 11. Other Files (Non-code/resource files grouped by extension, e.g. .gradle.kts, .json, .properties, .sql, .xml, .yml)
 * 12. Bottom Files (e.g. package-info.java strictly at the bottom)
 */
enum class JavaTypeCategory(
    val displayName: String,
    val weight: Int,
    val sortPrefix: String
) {
    INTERFACE("Interface", 100, "01_"),
    ABSTRACT_CLASS("Abstract Class", 110, "02_"),
    CLASS("Class", 120, "03_"),
    FINAL_CLASS("Final Class", 130, "04_"),
    OBJECT("Object", 135, "05_"),
    RECORD("Record", 140, "06_"),
    ENUM("Enum", 150, "07_"),
    ANNOTATION("Annotation", 160, "08_"),
    KOTLIN_FILE("Kotlin File", 170, "09_"),
    OTHER_FILE("Other File", 200, "10_"),
    BOTTOM_FILE("Bottom File", BottomFileConstants.BOTTOM_SORT_WEIGHT, BottomFileConstants.BOTTOM_SORT_PREFIX);
}
