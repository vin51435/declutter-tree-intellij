package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * Custom PsiFileNode wrapper for files categorized by type and extension.
 *
 * Performance:
 * - Pre-computes [cachedSortKey] at construction time.
 * - Zero PSI reads or string allocations during comparator sort passes.
 */
class CategorizedPsiFileNode(
    project: Project,
    value: PsiFile,
    viewSettings: ViewSettings,
    val category: JavaTypeCategory,
    customSortKey: String? = null
) : PsiFileNode(project, value, viewSettings) {

    private val cachedSortKey: String = customSortKey ?: "${category.sortPrefix}${virtualFile?.name ?: value.name ?: ""}"

    override fun getWeight(): Int = category.weight

    override fun getSortKey(): Comparable<*>? = cachedSortKey
}
