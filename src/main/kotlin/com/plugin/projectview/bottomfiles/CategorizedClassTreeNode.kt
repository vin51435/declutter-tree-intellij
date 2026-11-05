package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass

/**
 * Custom Project View tree node extending IntelliJ's [ClassTreeNode] to assign
 * category-based sort weights and keys.
 *
 * Performance:
 * - Pre-computes [cachedSortKey] at construction time to guarantee O(1) sort comparisons without allocations.
 * - [getWeight] returns a primitive int with zero PSI lookups.
 * - Inherits native icon rendering, member expansion, and element equality.
 */
class CategorizedClassTreeNode(
    project: Project,
    val psiClassElement: PsiClass,
    settings: ViewSettings,
    nestedFileNodes: Collection<AbstractTreeNode<*>>?,
    val category: JavaTypeCategory
) : ClassTreeNode(project, psiClassElement, settings, nestedFileNodes ?: emptyList()) {

    private val cachedSortKey: String = "${category.sortPrefix}${psiClassElement.name ?: ""}"

    override fun getWeight(): Int = category.weight

    override fun getSortKey(): Comparable<*>? = cachedSortKey
}
