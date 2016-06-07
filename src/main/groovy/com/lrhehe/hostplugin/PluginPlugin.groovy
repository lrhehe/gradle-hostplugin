package com.lrhehe.hostplugin

import org.gradle.api.Project

/**
 * @Author ray
 * @Date 5/23/16.
 */
public class PluginPlugin extends BaseHPPlugin {

    def mBakBuildFile
    def hostLibs
    def pluginsDir


    @Override
    public void apply(Project project) {
        super.apply(project)

        if (!HPBUILD) {
            return
        }

        hostLibs = new File(hostProject.projectDir, "providedLibs")

        mBakBuildFile = new File(project.buildFile.parentFile, "${project.buildFile.name}~")

        if (!hostLibs.exists()) {
            hostLibs.mkdir()
        }

        project.beforeEvaluate {
            changeBuildFile()
        }

        project.afterEvaluate {
            restoreBuildFile()

            resolveDependencies()

            applySignConfig()

            project.android.applicationVariants.all { variant ->
                // While release variant created, everything of `Android Plugin' should be ready
                // and then we can do some extensions with it
                configureVariant(variant)
            }
        }
    }

    private void applySignConfig() {
        hostProject.android.buildTypes.each {
            def bt = project.android.buildTypes[it.name]
            bt.signingConfig = it.signingConfig
        }
    }

    private void changeBuildFile() {
        if (mBakBuildFile.exists()) {
            throw new Exception("Conflict buildFile, please delete file $mBakBuildFile or " +
                    "${project.buildFile}")
        }

        def text = project.buildFile.text.replaceAll(
                'com\\.android\\.library', 'com.android.application')
        project.buildFile.renameTo(mBakBuildFile)
        project.buildFile.write(text)
    }

    private void restoreBuildFile() {
        if (mBakBuildFile.exists()) {
            project.buildFile.delete()
            mBakBuildFile.renameTo(project.buildFile)
        }
    }

    protected void configureVariant(Object variant) {
        def flavor = variant.flavorName
        flavor = flavor == null ? "" : flavor.capitalize()
        def taskname = "build${flavor}Plugin"
        if (!project.hasProperty(taskname)) {
            println "addTask: $taskname"
            project.task(taskname, dependsOn: "assemble${flavor}Release")
        }
    }

    @Override
    protected boolean shouldAddToProvidedLibs(it) {
        return !isInPluginLibs(it)
    }
}
