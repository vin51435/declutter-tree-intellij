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
     * Macro-level category weight evaluated by GroupByTypeComparator.
     * Returning a high integer places this node below standard files.
     */
    override fun getTypeSortWeight(sortByType: Boolean): Int {
        return BottomFileConstants.BOTTOM_SORT_WEIGHT
    }

    /**
     * Fallback weight for generic TreeStructure comparators.
     */
    override fun getWeight(): Int {
        return BottomFileConstants.BOTTOM_SORT_WEIGHT
    }

    /**
     * Lexicographical sort key used when "Sort by Type" is disabled.
     * Evaluated using fast in-memory VirtualFile name without triggering PSI read locks.
     */
    override fun getSortKey(): Comparable<*>? {
        val fileName = virtualFile?.name ?: value?.name ?: return null
        return BottomFileConstants.BOTTOM_SORT_PREFIX + fileName
    }
}
