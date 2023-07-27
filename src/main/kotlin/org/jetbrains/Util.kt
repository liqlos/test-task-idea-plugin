package org.jetbrains

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsContexts

object Util {

    @JvmStatic
    fun showErrorMessage(project: Project, @NlsContexts.DialogMessage message: String) {
        Messages.showMessageDialog(
            project, message, "Error", Messages.getErrorIcon()
        )
    }
}