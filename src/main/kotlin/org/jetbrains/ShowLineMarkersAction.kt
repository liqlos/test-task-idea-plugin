package org.jetbrains

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsContexts

class ShowLineMarkersAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    //in javadoc in AnAction class it says that working with filesystem in update function may be too slow,
    // so we won't check if the Main.kt file is exists here
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!
        //TODO: find another way to open file because this one is deprecated

        val mainKtFile = currentProject.baseDir.findFileByRelativePath("src/main/kotlin/Main.kt")
            ?: return showErrorMessage(currentProject, "Main.kt file not found")


        val descriptor = OpenFileDescriptor(currentProject, mainKtFile)

        val editor =
            FileEditorManager.getInstance(currentProject).openTextEditor(descriptor, true)
                ?: return showErrorMessage(
                    currentProject,
                    "Can't open code editor"
                )

        val lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, currentProject)


        Messages.showMessageDialog(
            currentProject,
            "LineMarkers of Main.kt file: $lineMarkers",
            "Task 3",
            Messages.getInformationIcon()
        )
    }

    private fun showErrorMessage(project: Project, @NlsContexts.DialogMessage message: String) {
        Messages.showMessageDialog(
            project,
            message,
            "Task 3",
            Messages.getWarningIcon()
        )
    }
}
