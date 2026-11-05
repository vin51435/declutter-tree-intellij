package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * Specialized PsiFileNode wrapper that forces designated files to the very bottom.
 *
 * Performance:
 * - Pre-computes [cachedSortKey] with Unicode sentinel prefix.
 * - Returns primitive weight of 1000 in O(1) time.
 */
class BottomPsiFileNode(
    project: Project,
    value: PsiFile,
    viewSettings: ViewSettings
) : PsiFileNode(project, value, viewSettings) {

    private val cachedSortKey: String = BottomFileConstants.BOTTOM_SORT_PREFIX + (virtualFile?.name ?: value.name ?: "")

    override fun getWeight(): Int = BottomFileConstants.BOTTOM_SORT_WEIGHT

    override fun getSortKey(): Comparable<*>? = cachedSortKey
}
