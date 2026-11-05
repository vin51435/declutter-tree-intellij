package com.plugin.projectview.bottomfiles

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * Persistent application-level settings for Project View file sorting.
 *
 * Persisted in the IDE configuration directory under `options/projectViewSortingSettings.xml`.
 * Settings are globally preserved across IDE restarts and apply across all projects.
 */
@State(
    name = "ProjectViewSortingSettings",
    storages = [Storage("projectViewSortingSettings.xml")]
)
class ProjectViewSortingSettings : PersistentStateComponent<ProjectViewSortingSettings.State> {

    data class State(
        var pinnedBottomFiles: String = "package-info.java",
        var pinnedTopFiles: String = "",
        var enableTypeGrouping: Boolean = true,
        var groupResourcesByExtension: Boolean = true
    )

    private var myState = State()

    // Pre-parsed in-memory sets for O(1) lock-free lookup during tree rendering
    @Volatile
    private var cachedBottomSet: Set<String> = parseNames(myState.pinnedBottomFiles)

    @Volatile
    private var cachedTopSet: Set<String> = parseNames(myState.pinnedTopFiles)

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
        updateCaches()
    }

    var pinnedBottomFiles: String
        get() = myState.pinnedBottomFiles
        set(value) {
            myState.pinnedBottomFiles = value
            updateCaches()
        }

    var pinnedTopFiles: String
        get() = myState.pinnedTopFiles
        set(value) {
            myState.pinnedTopFiles = value
            updateCaches()
        }

    var enableTypeGrouping: Boolean
        get() = myState.enableTypeGrouping
        set(value) {
            myState.enableTypeGrouping = value
        }

    var groupResourcesByExtension: Boolean
        get() = myState.groupResourcesByExtension
        set(value) {
            myState.groupResourcesByExtension = value
        }

    fun isPinnedBottom(fileName: String?): Boolean {
        if (fileName == null) return false
        return cachedBottomSet.contains(fileName)
    }

    fun isPinnedTop(fileName: String?): Boolean {
        if (fileName == null) return false
        return cachedTopSet.contains(fileName)
    }

    private fun updateCaches() {
        cachedBottomSet = parseNames(myState.pinnedBottomFiles)
        cachedTopSet = parseNames(myState.pinnedTopFiles)
    }

    private fun parseNames(raw: String): Set<String> {
        if (raw.isBlank()) return emptySet()
        return raw.split(',', ';', '\n', '\r')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toHashSet()
    }

    companion object {
        @JvmStatic
        fun getInstance(): ProjectViewSortingSettings {
            return ApplicationManager.getApplication().getService(ProjectViewSortingSettings::class.java)
        }
    }
}
