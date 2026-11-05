package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.NavigatableWithText

/**
 * Universal wrapper for Project View nodes (e.g. Kotlin class/object nodes, Kotlin file nodes,
 * or any other [ProjectViewNode]) that assigns category sort weights and prefixes.
 *
 * Preserves:
 * - Native IntelliJ icons (I, C, O, E, @, K), badges, and VCS statuses.
 * - Selection stability, auto-scroll from source, and tree expansion state.
 * - Navigation shortcuts (Enter, F4, double-click).
 * - "Folders Always on Top" by delegating [getTypeSortWeight].
 */
class CategorizedNodeWrapper(
    project: Project,
    val delegate: ProjectViewNode<*>,
    val category: JavaTypeCategory,
    val sortKeyName: String
) : ProjectViewNode<Any>(
    project,
    delegate.value ?: Any(),
    delegate.settings
), NavigatableWithText {

    override fun getWeight(): Int {
        return category.weight
    }

    override fun getSortKey(): Comparable<*>? {
        return "${category.sortPrefix}$sortKeyName"
    }

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

    override fun canNavigate(): Boolean {
        return delegate.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return delegate.canNavigateToSource()
    }

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
}
