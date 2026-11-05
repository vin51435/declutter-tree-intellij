package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * Specialized PsiFileNode wrapper that overrides Project View comparator keys
 * to force the file to the very bottom of its parent directory.
 *
 * Subclassing PsiFileNode guarantees:
 * 1. Zero UI stutter: No PSI parsing or icon recalculations during tree rendering.
 * 2. Visual integrity: Inherits all default icons, Git/VCS status badges, and context menu actions.
 * 3. Selection stability: Inherits PsiFileNode's final getEqualityObject() returning the underlying PsiFile.
 */
class BottomPsiFileNode(
    project: Project,
    value: PsiFile,
    viewSettings: ViewSettings
) : PsiFileNode(project, value, viewSettings) {

    /**
     * Weight evaluated by AlphaComparator (the primary sorting comparator in ProjectView).
     * Standard files and classes have weight 20 (and default weight 30).
     * Returning 1000 places this node strictly after all standard files and classes.
     *
     * IMPORTANT: We deliberately do NOT override getTypeSortWeight()! In IntelliJ's
     * GroupByTypeComparator, any node with getTypeSortWeight() != 0 is classified as a
     * directory/folder under "Folders Always on Top", which caused package-info.java to be
     * pinned to the top. Inheriting getTypeSortWeight() = 0 preserves its file classification.
     */
    override fun getWeight(): Int {
        return BottomFileConstants.BOTTOM_SORT_WEIGHT
    }

    /**
     * Lexicographical sort key used when "Sort by Name" is active.
     * Evaluated using fast in-memory VirtualFile name without triggering PSI read locks.
     */
    override fun getSortKey(): Comparable<*>? {
        val fileName = virtualFile?.name ?: value?.name ?: return null
        return BottomFileConstants.BOTTOM_SORT_PREFIX + fileName
    }
}
