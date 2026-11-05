package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * Custom PsiFileNode wrapper for files categorized by type (e.g. non-class files or Java files with multiple classes).
 */
class CategorizedPsiFileNode(
    project: Project,
    value: PsiFile,
    viewSettings: ViewSettings,
    val category: JavaTypeCategory
) : PsiFileNode(project, value, viewSettings) {

    /**
     * Category weight evaluated by AlphaComparator.
     */
    override fun getWeight(): Int {
        return category.weight
    }

    /**
     * Lexicographical sort key used when "Sort by Name" is active.
     */
    override fun getSortKey(): Comparable<*>? {
        val fileName = virtualFile?.name ?: value?.name ?: return null
        return "${category.sortPrefix}$fileName"
    }
}
