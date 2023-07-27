package org.jetbrains

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.application.ApplicationConfigurationType
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.Util.showErrorMessage


private const val MAIN_KT_FILE_PATH = "src/main/kotlin/Main.kt"
private const val MAIN_CLASS_NAME = "MainKt"

class RunKotlinFileAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val currentProject = e.project!!
        val runManager = RunManager.getInstance(currentProject)

        val factory = ApplicationConfigurationType.getInstance()
            .configurationFactories.first()


        val mainKtFile =
            currentProject.guessProjectDir()?.findFileByRelativePath(MAIN_KT_FILE_PATH) ?: return showErrorMessage(
                currentProject,
                "Main.kt file not found"
            )

        val runConfiguration = createRunConfiguration(
            currentProject, mainKtFile, factory
        )

        val runnerAndConfigurationSettings = runManager.createConfiguration(
            runConfiguration, factory
        )


        val executionEnvironment = ExecutionEnvironmentBuilder.create(
            DefaultRunExecutor.getRunExecutorInstance(),
            runnerAndConfigurationSettings
        ).build()

        ProgramRunnerUtil.executeConfigurationAsync(
            executionEnvironment,
            true,
            true
        ) { runContentDescriptor -> showExitCodeNotification(runContentDescriptor, currentProject) }
    }

    private fun createRunConfiguration(
        project: Project,
        mainKtFile: VirtualFile,
        factory: ConfigurationFactory
    ): ApplicationConfiguration {
        val configuration: ApplicationConfiguration =
            factory.createTemplateConfiguration(project) as ApplicationConfiguration

        configuration.mainClassName = MAIN_CLASS_NAME

        val modules = ModuleManager.getInstance(project).modules
        if (modules.isNotEmpty()) {
            configuration.setModule(ModuleUtilCore.findModuleForFile(mainKtFile, project))
        }

        return configuration
    }

    private fun showExitCodeNotification(contentDescriptor: RunContentDescriptor?, project: Project) {
        val processHandler = contentDescriptor?.processHandler

        processHandler?.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                val exitCode = event.exitCode

                Notifications.Bus.notify(
                    Notification(
                        "executionResultNotification",
                        "Main.kt file execution result",
                        "The exit code is $exitCode",
                        NotificationType.INFORMATION
                    ),
                    project
                )
            }
        })
    }
}
