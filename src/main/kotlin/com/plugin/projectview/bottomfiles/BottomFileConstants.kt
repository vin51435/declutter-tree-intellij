package com.plugin.projectview.bottomfiles

/**
 * Constants and fallback configuration for bottom file sorting.
 */
object BottomFileConstants {

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
     * Delegates to persistent user settings for dynamic configuration.
     */
    @JvmStatic
    fun isBottomFile(fileName: String?): Boolean {
        if (fileName == null) return false
        return try {
            ProjectViewSortingSettings.getInstance().isPinnedBottom(fileName)
        } catch (_: Throwable) {
            fileName == "package-info.java"
        }
    }
}
