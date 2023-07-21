package org.jetbrains

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class KotlinPluginVersionAction : AnAction() {

    private val kotlinPluginId = "org.jetbrains.kotlin"
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        //Спросить, Что если я удалю уже установленный котлин плагин, выполнится ли update снова?
        e.presentation.isEnabled = PluginManagerCore.isPluginInstalled(PluginId.getId(kotlinPluginId))
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!

        //At this point we are sure that kotlin plugin is installed
        val kotlinPluginVersion: String = PluginManagerCore.getPlugin(PluginId.getId(kotlinPluginId))!!.version

        Messages.showMessageDialog(
            currentProject,
            "Version of installed Kotlin plugin: $kotlinPluginVersion",
            "Task 1",
            Messages.getInformationIcon()
        )
    }
}