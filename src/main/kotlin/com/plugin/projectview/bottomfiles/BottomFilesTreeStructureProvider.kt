package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiClassOwner
import java.util.ArrayList

/**
 * Intercepts Project Tool Window child nodes and organizes them into categorized groups:
 * 1. Folders remain at the top (handled natively by IntelliJ)
 * 2. Interfaces
 * 3. Abstract Classes
 * 4. Classes (Normal)
 * 5. Final Classes
 * 6. Records
 * 7. Enums
 * 8. Annotations
 * 9. Other non-class files
 * 10. Bottom files (package-info.java) strictly at the very bottom
 *
 * Implements [DumbAware] so tree rendering remains fast and active during background indexing.
 */
class BottomFilesTreeStructureProvider : TreeStructureProvider, DumbAware {

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>> {
        if (children.isEmpty()) {
            return children
        }

        // --- FAST PATH: ZERO ALLOCATIONS IF NO FILES/CLASSES ARE PRESENT ---
        var hasCategorizableNodes = false
        for (child in children) {
            if (child is ClassTreeNode || child is PsiFileNode) {
                hasCategorizableNodes = true
                break
            }
        }
        if (!hasCategorizableNodes) {
            return children
        }

        // --- SLOW PATH: WRAP AND CATEGORIZE NODES ---
        val effectiveSettings = settings ?: ViewSettings.DEFAULT
        val modifiedChildren = ArrayList<AbstractTreeNode<*>>(children.size)

        for (child in children) {
            when {
                // Already wrapped nodes
                child is BottomPsiFileNode || child is CategorizedClassTreeNode || child is CategorizedPsiFileNode -> {
                    modifiedChildren.add(child)
                }

                // Java Class Tree Node (e.g. top-level class file transformed by ClassesTreeStructureProvider)
                child is ClassTreeNode -> {
                    val project = child.project
                    val psiClass = child.psiClass
                    if (project != null && psiClass != null && psiClass.isValid) {
                        val category = JavaTypeClassifier.classify(psiClass)
                        modifiedChildren.add(
                            CategorizedClassTreeNode(
                                project = project,
                                psiClassElement = psiClass,
                                settings = effectiveSettings,
                                nestedFileNodes = child.nestedFileNodes,
                                category = category
                            )
                        )
                    } else {
                        modifiedChildren.add(child)
                    }
                }

                // File Node
                child is PsiFileNode -> {
                    val fileName = child.virtualFile?.name ?: child.value?.name
                    val project = child.project
                    val psiFile = child.value

                    if (project != null && psiFile != null && psiFile.isValid) {
                        if (BottomFileConstants.isBottomFile(fileName)) {
                            // Bottom files (e.g. package-info.java)
                            modifiedChildren.add(BottomPsiFileNode(project, psiFile, effectiveSettings))
                        } else if (psiFile is PsiClassOwner && psiFile.classes.isNotEmpty()) {
                            // Java file with classes that wasn't converted to ClassTreeNode
                            val category = JavaTypeClassifier.classify(psiFile.classes[0])
                            modifiedChildren.add(
                                CategorizedPsiFileNode(project, psiFile, effectiveSettings, category)
                            )
                        } else {
                            // Other non-class files (e.g. .xml, .properties, .yml, .json, .md)
                            modifiedChildren.add(
                                CategorizedPsiFileNode(project, psiFile, effectiveSettings, JavaTypeCategory.OTHER_FILE)
                            )
                        }
                    } else {
                        modifiedChildren.add(child)
                    }
                }

                // Folders (PsiDirectoryNode) and other non-file nodes are kept unmodified
                else -> {
                    modifiedChildren.add(child)
                }
            }
        }

        return modifiedChildren
    }
}
