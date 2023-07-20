package org.jetbrains

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class KotlinPluginVersionAction : AnAction() {
    private val KOTLIN_PLUGIN_ID = "org.jetbrains.kotlin"

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        //Спросить, Что если я удалю уже установленный котлин плагин, выполнится ли update снова?
        e.presentation.isEnabled = PluginManagerCore.isPluginInstalled(PluginId.getId(KOTLIN_PLUGIN_ID))
    }

    override fun actionPerformed(e: AnActionEvent) {
        //Using the event, implement an action. For example, create and show a dialog.
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