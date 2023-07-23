package org.jetbrains

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsContexts


private const val MAIN_KT_FILE_PATH = "src/main/kotlin/Main.kt"
private const val TOOLTIP_REGEX = "<html><body><p>(.*?)</p>"

class ShowLineMarkersAction : AnAction() {


    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    //in javadoc of AnAction class it says that working with filesystem in update function may be too slow,
    //so we won't check if the Main.kt file exists in the update function
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!

        val mainKtFile =
            currentProject.guessProjectDir()?.findFileByRelativePath(MAIN_KT_FILE_PATH) ?: return showErrorMessage(
                currentProject,
                "Main.kt file not found"
            )

        val descriptor = OpenFileDescriptor(currentProject, mainKtFile)

        val editor =
            FileEditorManager.getInstance(currentProject).openTextEditor(descriptor, true) ?: return showErrorMessage(
                currentProject, "Can't open code editor"
            )

        Messages.showMessageDialog(
            currentProject,
            getLineMarkersForMessage(editor.document, currentProject),
            "Task 3",
            Messages.getInformationIcon()
        )
    }

    private fun getLineMarkersForMessage(document: Document, project: Project): String {
        val lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, project)

        return if (lineMarkers.isEmpty()) "There is no line markers in file"

        else lineMarkers.joinToString("\n") { lineMarkerInfo ->
            "${lineMarkerInfo.startOffset} ${lineMarkerInfo.endOffset} ${
                lineMarkerInfo.lineMarkerTooltip?.let { tooltip ->
                    Regex(TOOLTIP_REGEX).find(
                        tooltip
                    )?.groupValues?.get(1)
                } ?: "No tooltip"
            }"
        }
    }

    private fun showErrorMessage(project: Project, @NlsContexts.DialogMessage message: String) {
        Messages.showMessageDialog(
            project, message, "Task 3", Messages.getWarningIcon()
        )
    }
}
