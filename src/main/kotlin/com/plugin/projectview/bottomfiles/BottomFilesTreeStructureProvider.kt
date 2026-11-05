package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

/**
 * High-performance, zero-stutter Project Tool Window tree structure provider
 * that organizes Java and Kotlin elements into configurable categorized groups:
 * 1. Folders remain at the top (handled natively by IntelliJ)
 * 2. Pinned Top Files (e.g. README.md, module-info.java configured by user)
 * 3. Interfaces (Java, Kotlin, Sealed)
 * 4. Abstract Classes (Java, Kotlin, Sealed)
 * 5. Classes (Java normal, Kotlin normal, Data, Open)
 * 6. Final Classes (Java final)
 * 7. Objects (Kotlin singleton objects)
 * 8. Records (Java records)
 * 9. Enums (Java enums, Kotlin enum classes)
 * 10. Annotations (Java annotations, Kotlin annotation classes)
 * 11. Kotlin Files (Kotlin files with top-level functions/properties)
 * 12. Other Files (Non-code / resource files optionally grouped by extension)
 * 13. Pinned Bottom Files (package-info.java) strictly at the very bottom
 *
 * Performance Guarantees:
 * - Zero Allocation Fast-Path: Returns immediately if all nodes are already categorized or are directories.
 * - Cooperative Cancellation: Invokes [ProgressManager.checkCanceled] to keep UI responsive.
 * - Pre-computed Sort Keys: All sort keys are cached once at node creation, eliminating string allocation during sorting.
 * - Dumb-Mode Resilience: Implements [DumbAware] with safe exception handling so indexing is never interrupted.
 */
class BottomFilesTreeStructureProvider : TreeStructureProvider, DumbAware {

    companion object {
        // Cache for uppercase extension prefixes to prevent repeated string allocations
        private val EXTENSION_PREFIX_CACHE = ConcurrentHashMap<String, String>()

        @JvmStatic
        private fun getExtensionPrefix(ext: String): String {
            if (ext.isEmpty()) return ""
            return EXTENSION_PREFIX_CACHE.computeIfAbsent(ext) { "${it.uppercase()}_" }
        }
    }

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>> {
        if (children.isEmpty()) {
            return children
        }

        // --- FAST PATH: ZERO ALLOCATIONS IF NO NODES REQUIRE CATEGORIZATION ---
        var hasUnprocessedNodes = false
        for (child in children) {
            // Already wrapped nodes
            if (child is BottomPsiFileNode || child is CategorizedClassTreeNode || 
                child is CategorizedPsiFileNode || child is CategorizedNodeWrapper) {
                continue
            }
            // Directory / folder node (handled by platform for "Folders Always on Top")
            if (child !is ProjectViewNode<*> || child.getTypeSortWeight(false) != 0) {
                continue
            }
            hasUnprocessedNodes = true
            break
        }

        if (!hasUnprocessedNodes) {
            return children
        }

        // --- SLOW PATH: WRAP AND CATEGORIZE UNWRAPPED NODES ---
        val effectiveSettings = settings ?: ViewSettings.DEFAULT
        val sortingSettings = ProjectViewSortingSettings.getInstance()
        val modifiedChildren = ArrayList<AbstractTreeNode<*>>(children.size)

        for (child in children) {
            ProgressManager.checkCanceled()

            when {
                // Already wrapped nodes
                child is BottomPsiFileNode || child is CategorizedClassTreeNode || 
                child is CategorizedPsiFileNode || child is CategorizedNodeWrapper -> {
                    modifiedChildren.add(child)
                }

                // Directories / folders (getTypeSortWeight != 0) - preserve untouched for "Folders Always on Top"
                child !is ProjectViewNode<*> || child.getTypeSortWeight(false) != 0 -> {
                    modifiedChildren.add(child)
                }

                // Java ClassTreeNode (e.g. top-level Java class file transformed by ClassesTreeStructureProvider)
                child is ClassTreeNode -> {
                    val project = child.project
                    val psiClass = child.psiClass
                    if (project != null && psiClass != null && psiClass.isValid) {
                        val fileName = child.virtualFile?.name ?: psiClass.containingFile?.name
                        if (sortingSettings.isPinnedTop(fileName)) {
                            modifiedChildren.add(
                                CategorizedClassTreeNode(
                                    project = project,
                                    psiClassElement = psiClass,
                                    settings = effectiveSettings,
                                    nestedFileNodes = child.nestedFileNodes,
                                    category = JavaTypeCategory.PINNED_TOP
                                )
                            )
                        } else if (sortingSettings.enableTypeGrouping) {
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
                    } else {
                        modifiedChildren.add(child)
                    }
                }

                // PsiFileNode (Java files, Kotlin files, bottom files, resources)
                child is PsiFileNode -> {
                    val fileName = child.virtualFile?.name ?: child.value?.name ?: ""
                    val project = child.project
                    val psiFile = child.value

                    if (project != null && psiFile != null && psiFile.isValid) {
                        if (sortingSettings.isPinnedBottom(fileName)) {
                            // Designated bottom files (e.g. package-info.java)
                            modifiedChildren.add(BottomPsiFileNode(project, psiFile, effectiveSettings))
                        } else if (sortingSettings.isPinnedTop(fileName)) {
                            // Designated pinned top files (e.g. README.md, module-info.java)
                            modifiedChildren.add(
                                CategorizedPsiFileNode(
                                    project = project,
                                    value = psiFile,
                                    viewSettings = effectiveSettings,
                                    category = JavaTypeCategory.PINNED_TOP,
                                    customSortKey = "${JavaTypeCategory.PINNED_TOP.sortPrefix}$fileName"
                                )
                            )
                        } else if (sortingSettings.enableTypeGrouping) {
                            val category = resolveCategoryForFile(psiFile)
                            val ext = child.virtualFile?.extension ?: ""
                            val sortKey = if (sortingSettings.groupResourcesByExtension && category == JavaTypeCategory.OTHER_FILE && ext.isNotEmpty()) {
                                "${category.sortPrefix}${getExtensionPrefix(ext)}$fileName"
                            } else {
                                "${category.sortPrefix}$fileName"
                            }
                            modifiedChildren.add(
                                CategorizedPsiFileNode(project, psiFile, effectiveSettings, category, sortKey)
                            )
                        } else {
                            modifiedChildren.add(child)
                        }
                    } else {
                        modifiedChildren.add(child)
                    }
                }

                // Other ProjectViewNodes (such as Kotlin's KtClassOrObjectTreeNode)
                else -> {
                    val project = child.project
                    val value = child.value
                    if (project != null && value != null) {
                        val fileName = child.virtualFile?.name ?: child.name ?: (value as? PsiNamedElement)?.name ?: ""
                        if (sortingSettings.isPinnedTop(fileName)) {
                            modifiedChildren.add(
                                CategorizedNodeWrapper(project, child, JavaTypeCategory.PINNED_TOP, fileName)
                            )
                        } else if (sortingSettings.enableTypeGrouping) {
                            val category = resolveCategoryForElement(value)
                            if (category != null) {
                                val name = child.name ?: (value as? PsiNamedElement)?.name ?: ""
                                modifiedChildren.add(
                                    CategorizedNodeWrapper(project, child, category, name)
                                )
                            } else {
                                modifiedChildren.add(child)
                            }
                        } else {
                            modifiedChildren.add(child)
                        }
                    } else {
                        modifiedChildren.add(child)
                    }
                }
            }
        }

        return modifiedChildren
    }

    private fun resolveCategoryForElement(element: Any?): JavaTypeCategory? {
        if (element == null) return null

        // 1. Kotlin element (KtClassOrObject, KtFile)
        val kotlinCategory = KotlinTypeClassifier.classify(element)
        if (kotlinCategory != null) return kotlinCategory

        // 2. Java PsiClass
        if (element is PsiClass) {
            return JavaTypeClassifier.classify(element)
        }

        return null
    }

    private fun resolveCategoryForFile(psiFile: PsiFile): JavaTypeCategory {
        // 1. Kotlin file (KtFile)
        val kotlinCategory = KotlinTypeClassifier.classify(psiFile)
        if (kotlinCategory != null) return kotlinCategory

        // 2. Java file with classes
        if (psiFile is PsiClassOwner && psiFile.classes.isNotEmpty()) {
            return JavaTypeClassifier.classify(psiFile.classes[0])
        }

        // 3. Resource / config / other file (e.g. .gradle.kts, .json, .properties, .sql, .xml, .yml)
        return JavaTypeCategory.OTHER_FILE
    }
}
