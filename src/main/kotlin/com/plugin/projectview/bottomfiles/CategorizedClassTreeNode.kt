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
 * Guarantees:
 * - Inherits all native icons (I, C, E, R, @), badges, and VCS statuses.
 * - Inherits member expansion (methods/fields) through [ClassTreeNode.getChildrenImpl].
 * - Inherits element equality, navigation, and refactoring support.
 * - Folders remain on top because [getTypeSortWeight] is inherited as 0.
 */
class CategorizedClassTreeNode(
    project: Project,
    val psiClassElement: PsiClass,
    settings: ViewSettings,
    nestedFileNodes: Collection<AbstractTreeNode<*>>?,
    val category: JavaTypeCategory
) : ClassTreeNode(project, psiClassElement, settings, nestedFileNodes ?: emptyList()) {

    /**
     * Category weight evaluated by AlphaComparator.
     * Categorizes classes into tiered groups (Interface=100, Abstract=110, Class=120, etc.).
     */
    override fun getWeight(): Int {
        return category.weight
    }

    /**
     * Lexicographical sort key used when "Sort by Name" is active.
     * Prefixed with category ordering to group types together while preserving alphabetical sorting within groups.
     */
    override fun getSortKey(): Comparable<*>? {
        val name = psiClassElement.name ?: return null
        return "${category.sortPrefix}$name"
    }
}
