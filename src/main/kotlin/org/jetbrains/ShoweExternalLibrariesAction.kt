package org.jetbrains

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NotNull

class ShoweExternalLibrariesAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = getProjectLibrariesNames(e.project!!).isNotEmpty()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!

        Messages.showMessageDialog(
            currentProject,
            "External libraries:\n${getProjectLibrariesNames(currentProject).joinToString("\n")}",
            "Task 2",
            Messages.getInformationIcon()
        )
    }

    private fun getProjectLibrariesNames(@NotNull project: Project): List<@NlsSafe String?> {
        val projectLibraries = LibraryTablesRegistrar.getInstance().getLibraryTable(project).libraries
        return projectLibraries.map { it.name }
    }
}