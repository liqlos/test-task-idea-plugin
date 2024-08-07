package org.jetbrains

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.Util.showErrorMessage
import org.jetbrains.annotations.NotNull


private const val KOTLIN_FILE_NAME = "Main.kt"

private class ShowLineMarkersAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    //in javadoc of AnAction class it says that working with filesystem in update function may be too slow,
    //so we won't check if the Main.kt file exists in the update function
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject: Project = e.project!!

        val mainKtFile =
            FilenameIndex.getVirtualFilesByName(KOTLIN_FILE_NAME, GlobalSearchScope.projectScope(currentProject))
                .firstOrNull()
                ?: return showErrorMessage(
                    currentProject,
                    "$KOTLIN_FILE_NAME is not found"
                )

        val fileDescriptor = OpenFileDescriptor(currentProject, mainKtFile)
        val editor =
            FileEditorManager.getInstance(currentProject).openTextEditor(fileDescriptor, true)
                ?: return showErrorMessage(
                    currentProject, "Can't open code editor"
                )

        Messages.showMessageDialog(
            currentProject,
            formatLineMarkersForMessage(getLineMarkers(editor.document, currentProject)),
            "Task 3",
            Messages.getInformationIcon()
        )
    }

    private fun getLineMarkers(document: Document, project: Project): List<LineMarkerInfo<*>> {
        return DaemonCodeAnalyzerImpl.getLineMarkers(document, project)
    }

    private fun formatLineMarkersForMessage(@NotNull lineMarkers: List<LineMarkerInfo<*>>): String {
        return if (lineMarkers.isEmpty()) "There is no line markers in file"
        else lineMarkers.joinToString("\n") { lineMarkerInfo ->
            "${lineMarkerInfo.startOffset} ${lineMarkerInfo.endOffset} ${
                lineMarkerInfo.lineMarkerTooltip?.let { tooltip ->
                    Regex("<html><body><p>(.*?)</p>").find(
                        tooltip
                    )?.groupValues?.get(1)
                } ?: "No tooltip"
            }"
        }
    }
}
