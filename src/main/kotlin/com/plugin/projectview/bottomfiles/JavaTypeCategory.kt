package com.plugin.projectview.bottomfiles

/**
 * Defines the priority, sort weight, and sort prefix for each category of Java tree node.
 *
 * Sorting Hierarchy:
 * 1. Folders / Directories (Always on Top via platform sort weight)
 * 2. Interfaces (icon (I))
 * 3. Abstract Classes (icon (C) with abstract marker)
 * 4. Classes (Normal concrete Java classes, icon (C))
 * 5. Final Classes (Final concrete Java classes, icon (C) with final badge)
 * 6. Records (icon (R))
 * 7. Enums (icon (E))
 * 8. Annotations (icon @)
 * 9. Other Files (Non-Java files like .xml, .properties, .yml, .json)
 * 10. Bottom Files (e.g. package-info.java strictly at the bottom)
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
    RECORD("Record", 140, "05_"),
    ENUM("Enum", 150, "06_"),
    ANNOTATION("Annotation", 160, "07_"),
    OTHER_FILE("Other File", 200, "08_"),
    BOTTOM_FILE("Bottom File", BottomFileConstants.BOTTOM_SORT_WEIGHT, BottomFileConstants.BOTTOM_SORT_PREFIX);
}
