package com.plugin.projectview.bottomfiles

/**
 * Defines the priority, sort weight, and sort prefix for each category of Java and Kotlin tree node.
 *
 * Sorting Hierarchy:
 * 1. Folders / Directories (Always on Top via platform sort weight)
 * 2. Pinned Top Files (e.g. README.md, module-info.java configured by user)
 * 3. Interfaces (icon (I): Java interface, Kotlin interface, sealed interface)
 * 4. Abstract Classes (icon (C) with abstract marker: Java abstract class, Kotlin abstract/sealed class)
 * 5. Classes (icon (C): Java normal class, Kotlin class, data class, open class)
 * 6. Final Classes (icon (C) with final badge: Java final class)
 * 7. Objects (icon (O): Kotlin singleton object declaration)
 * 8. Records (icon (R): Java record)
 * 9. Enums (icon (E): Java enum, Kotlin enum class)
 * 10. Annotations (icon @: Java annotation, Kotlin annotation class)
 * 11. Kotlin Files (icon K: Kotlin files with top-level functions/properties)
 * 12. Other Files (Non-code/resource files grouped by extension, e.g. .gradle.kts, .json, .properties, .sql, .xml, .yml)
 * 13. Bottom Files (e.g. package-info.java strictly at the bottom)
 */
enum class JavaTypeCategory(
    val displayName: String,
    val weight: Int,
    val sortPrefix: String
) {
    PINNED_TOP("Pinned Top File", 50, "00_"),
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
