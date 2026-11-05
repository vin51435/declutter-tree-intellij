package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.impl.nodes.FileNodeWithNestedFileNodes
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.NavigatableWithText

/**
 * Universal high-performance wrapper for Project View nodes (e.g. Kotlin class/object nodes)
 * that assigns category sort weights and prefixes while transparently delegating all
 * platform operations to the underlying node.
 *
 * Performance:
 * - Pre-computes [cachedSortKey] at construction time for zero allocation during sorting.
 * - Preserves native icons, badges, VCS markers, selection, navigation, and member drill-down.
 */
class CategorizedNodeWrapper(
    project: Project,
    val delegate: ProjectViewNode<*>,
    val category: JavaTypeCategory,
    sortKeyName: String
) : ProjectViewNode<Any>(
    project,
    delegate.value ?: Any(),
    delegate.settings
), NavigatableWithText, FileNodeWithNestedFileNodes {

    private val cachedSortKey: String = "${category.sortPrefix}$sortKeyName"

    override fun getWeight(): Int = category.weight

    override fun getSortKey(): Comparable<*>? = cachedSortKey

    override fun update(presentation: PresentationData) {
        delegate.update(presentation)
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        return delegate.children
    }

    override fun contains(file: VirtualFile): Boolean {
        return delegate.contains(file)
    }

    override fun canRepresent(element: Any?): Boolean {
        return delegate.canRepresent(element)
    }

    override fun getVirtualFile(): VirtualFile? {
        return delegate.virtualFile
    }

    override fun canNavigate(): Boolean = delegate.canNavigate()

    override fun canNavigateToSource(): Boolean = delegate.canNavigateToSource()

    override fun navigate(requestFocus: Boolean) {
        delegate.navigate(requestFocus)
    }

    override fun getNavigateActionText(focusEditor: Boolean): String? {
        return (delegate as? NavigatableWithText)?.getNavigateActionText(focusEditor)
    }

    override fun getTypeSortWeight(sortByType: Boolean): Int {
        return delegate.getTypeSortWeight(sortByType)
    }

    override fun getTitle(): String? {
        return delegate.title
    }

    override fun getComment(): String? {
        return delegate.comment
    }

    override fun isAlwaysExpand(): Boolean = delegate.isAlwaysExpand

    override fun isAlwaysShowPlus(): Boolean = delegate.isAlwaysShowPlus

    override fun expandOnDoubleClick(): Boolean = delegate.expandOnDoubleClick()

    override fun getNestedFileNodes(): Collection<AbstractTreeNode<*>> {
        return (delegate as? FileNodeWithNestedFileNodes)?.nestedFileNodes ?: emptyList()
    }
}
