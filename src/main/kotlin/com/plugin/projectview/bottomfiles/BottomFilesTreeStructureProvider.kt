package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import java.util.ArrayList

/**
 * Intercepts Project Tool Window child nodes and wraps matching target files into [BottomPsiFileNode].
 *
 * Implements [DumbAware] so tree rendering remains fast and active during background indexing.
 */
class BottomFilesTreeStructureProvider : TreeStructureProvider, DumbAware {

    /**
     * Modifies the collection of child nodes for a parent directory.
     *
     * Performance Guarantees:
     * - Fast-Path (Zero Allocations): Scans existing nodes first. If no target file is present
     *   (which is true for 99%+ of directories), the original collection is returned unmodified.
     * - No AST Parsing: Name checking reads the pre-cached in-memory pointer from [com.intellij.openapi.vfs.VirtualFile].
     * - Safe PSI Resolution: Only accesses project and file validity checks when wrapping is strictly needed.
     */
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>> {
        if (children.isEmpty()) {
            return children
        }

        // --- FAST PATH: ZERO-ALLOCATION SCAN ---
        var containsTarget = false
        for (child in children) {
            if (isMatchingFileNode(child)) {
                containsTarget = true
                break
            }
        }

        // In the vast majority of folders, no matching file exists: return immediately.
        if (!containsTarget) {
            return children
        }

        // --- SLOW PATH: TARGET FILE DETECTED ---
        val effectiveSettings = settings ?: ViewSettings.DEFAULT
        val modifiedChildren = ArrayList<AbstractTreeNode<*>>(children.size)

        for (child in children) {
            if (child is PsiFileNode && isMatchingFileNode(child)) {
                val project = child.project
                val psiFile = child.value

                // Verify project and file validity before wrapping to avoid PSI invalidation crashes
                if (project != null && psiFile != null && psiFile.isValid) {
                    modifiedChildren.add(BottomPsiFileNode(project, psiFile, effectiveSettings))
                } else {
                    modifiedChildren.add(child)
                }
            } else {
                modifiedChildren.add(child)
            }
        }

        return modifiedChildren
    }

    /**
     * Fast O(1) check using the in-memory VirtualFile name without triggering PSI read locks.
     */
    private inline fun isMatchingFileNode(node: AbstractTreeNode<*>): Boolean {
        if (node !is PsiFileNode) return false
        val virtualFile = node.virtualFile ?: return false
        return BottomFileConstants.isBottomFile(virtualFile.name)
    }
}
