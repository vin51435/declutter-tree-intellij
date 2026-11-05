package com.plugin.projectview.bottomfiles

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Settings UI page in IntelliJ IDEA / Android Studio under:
 * Settings -> Appearance & Behavior -> Project View File Ordering
 */
class ProjectViewSortingConfigurable : SearchableConfigurable {

    private val pinnedBottomField = JBTextField()
    private val pinnedTopField = JBTextField()
    private val enableTypeGroupingBox = JBCheckBox("Group Java & Kotlin files by type (Interfaces, Classes, Objects, Enums, Annotations)")
    private val groupResourcesByExtensionBox = JBCheckBox("Group non-code files by extension (.gradle.kts, .json, .properties, .sql, .xml, .yml)")

    private var myMainPanel: JPanel? = null

    override fun getId(): String = "com.plugin.projectview.bottomfiles.settings"

    override fun getDisplayName(): String = "Project View File Ordering"

    override fun createComponent(): JComponent {
        pinnedTopField.emptyText.text = "e.g. README.md, module-info.java (comma-separated)"
        pinnedBottomField.emptyText.text = "e.g. package-info.java (comma-separated)"

        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Pinned Top Files (beneath folders):", pinnedTopField, 1, false)
            .addTooltip("Comma- or semicolon-separated filenames forced to the top of the file list.")
            .addVerticalGap(8)
            .addLabeledComponent("Pinned Bottom Files (at the very bottom):", pinnedBottomField, 1, false)
            .addTooltip("Comma- or semicolon-separated filenames forced to the very bottom beneath all files.")
            .addVerticalGap(12)
            .addComponent(enableTypeGroupingBox)
            .addComponent(groupResourcesByExtensionBox)
            .addVerticalGap(12)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        myMainPanel = panel
        return panel
    }

    override fun isModified(): Boolean {
        val settings = ProjectViewSortingSettings.getInstance()
        return pinnedBottomField.text.trim() != settings.pinnedBottomFiles ||
                pinnedTopField.text.trim() != settings.pinnedTopFiles ||
                enableTypeGroupingBox.isSelected != settings.enableTypeGrouping ||
                groupResourcesByExtensionBox.isSelected != settings.groupResourcesByExtension
    }

    override fun apply() {
        val settings = ProjectViewSortingSettings.getInstance()
        settings.pinnedBottomFiles = pinnedBottomField.text.trim()
        settings.pinnedTopFiles = pinnedTopField.text.trim()
        settings.enableTypeGrouping = enableTypeGroupingBox.isSelected
        settings.groupResourcesByExtension = groupResourcesByExtensionBox.isSelected

        // Immediately refresh Project View across all open projects without requiring IDE restart
        for (project in ProjectManager.getInstance().openProjects) {
            ProjectView.getInstance(project)?.refresh()
        }
    }

    override fun reset() {
        val settings = ProjectViewSortingSettings.getInstance()
        pinnedBottomField.text = settings.pinnedBottomFiles
        pinnedTopField.text = settings.pinnedTopFiles
        enableTypeGroupingBox.isSelected = settings.enableTypeGrouping
        groupResourcesByExtensionBox.isSelected = settings.groupResourcesByExtension
    }

    override fun disposeUIResources() {
        myMainPanel = null
    }
}
