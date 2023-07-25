package org.jetbrains

import com.intellij.execution.RunManager
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.application.ApplicationConfigurationType
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir


private const val MAIN_KT_FILE_PATH = "src/main/kotlin/Main.kt"

class RunKotlinFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val currentProject = e.project!!
        val runManager = RunManager.getInstance(currentProject)

        val configurationType = ApplicationConfigurationType.getInstance()
        val factory = configurationType.configurationFactories?.get(0)

        val mainClassName = "MainKt"

        val runConfiguration = createRunConfiguration(
            currentProject, factory!!, mainClassName
        )

        val runnerAndConfigurationSettings = runManager.createConfiguration(
            runConfiguration, factory
        )
        runManager.addConfiguration(runnerAndConfigurationSettings)

        ExecutionUtil.runConfiguration(
            runnerAndConfigurationSettings,
            DefaultRunExecutor.getRunExecutorInstance()
        )
    }
    private fun createRunConfiguration(
        project: Project,
        factory: ConfigurationFactory,
        mainClassName: String
    ): ApplicationConfiguration {
        val configuration: ApplicationConfiguration = factory.createTemplateConfiguration(project) as ApplicationConfiguration
        configuration.name = "Main"
        configuration.mainClassName = mainClassName

        val modules = ModuleManager.getInstance(project).modules
        val mainKtFile =
            project.guessProjectDir()?.findFileByRelativePath(MAIN_KT_FILE_PATH)

        if (modules.isNotEmpty()) {
            configuration.setModule(ModuleUtilCore.findModuleForFile(mainKtFile!!, project))
        }

        return configuration
    }
}
