package org.jetbrains

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages


private const val KOTLIN_PLUGIN_ID = "org.jetbrains.kotlin"

class KotlinPluginVersionAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT


    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && PluginManagerCore.isPluginInstalled(PluginId.getId(
            KOTLIN_PLUGIN_ID
        ))
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!

        //At this point we are sure that kotlin plugin is installed
        val kotlinPluginVersion: String = PluginManagerCore.getPlugin(PluginId.getId(KOTLIN_PLUGIN_ID))!!.version

        Messages.showMessageDialog(
            currentProject,
            "Version of installed Kotlin plugin: $kotlinPluginVersion",
            "Task 1",
            Messages.getInformationIcon()
        )
    }
}