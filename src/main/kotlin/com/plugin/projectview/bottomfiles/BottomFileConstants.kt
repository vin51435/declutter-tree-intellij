package com.plugin.projectview.bottomfiles

/**
 * Constants and configuration for bottom file sorting.
 */
object BottomFileConstants {

    /**
     * Set of target file names that must be forced to the bottom of their parent directory.
     * Pre-compiled Set ensures O(1) hash lookup with zero runtime overhead.
     */
    val TARGET_FILE_NAMES: Set<String> = hashSetOf(
        "package-info.java"
    )

    /**
     * High sorting weight evaluated by AlphaComparator (fallback for ProjectView node sorting).
     * Standard files and ClassTreeNode elements have weight 20 (and default weight 30).
     * A weight of 1000 guarantees matching nodes are sorted beneath all regular files and classes.
     */
    const val BOTTOM_SORT_WEIGHT: Int = 1_000

    /**
     * Unicode sentinel prefix for alphabetical sorting when "Sort by Type" is disabled.
     * \uFFFF is the maximum character in the Unicode Basic Multilingual Plane (BMP),
     * ensuring that natural string comparisons place this node after all ASCII and UTF-8 filenames.
     */
    const val BOTTOM_SORT_PREFIX: String = "\uFFFF"

    /**
     * Checks if the given file name is designated as a bottom file.
     * Executes in O(1) time without string manipulation or regex evaluation.
     */
    @JvmStatic
    fun isBottomFile(fileName: String?): Boolean {
        if (fileName == null) return false
        return TARGET_FILE_NAMES.contains(fileName)
    }
}
