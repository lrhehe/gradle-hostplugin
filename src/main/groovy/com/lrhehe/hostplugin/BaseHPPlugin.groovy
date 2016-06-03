package com.lrhehe.hostplugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.dependencies.DefaultSelfResolvingDependency

/**
 * @Author ray
 * @Date 5/23/16.
 */
public class BaseHPPlugin extends AndroidPlugin {

    static String BUILD_FLAG = "hpbuild"
    // use hostplugin to build flag
    def HPBUILD = false

    static String PROVIDEDDIR = "providedLibs"
    static String PREPAREDIR = "prepareLibs"


    String host = "";
    String plugin = "";
    String[] pluginLibs;

    File prepareLibsDir
    File providedLibsDir

    def providedLibs = []


    Project hostProject
    Project pluginProject


    def interDir
    def explodedAarDir

    void apply(Project project) {
        super.apply(project)
        HPBUILD = project.hasProperty(BUILD_FLAG)

        if(project == project.rootProject) {
            return
        }
        host = project.rootProject.host
        plugin = project.rootProject.plugin
        pluginLibs = project.rootProject.pluginLibs
        interDir = new File(project.buildDir, FD_INTERMEDIATES)
        explodedAarDir =  new File(interDir, FD_EXPLODED_AAR)
        hostProject = project.rootProject.project(host)
        prepareLibsDir = new File(hostProject.buildDir, PREPAREDIR)
        providedLibsDir = new File(project.projectDir, PROVIDEDDIR)
        if (!prepareLibsDir.exists()) {
            prepareLibsDir.mkdirs()
        }

        project.task("clean").doLast {
            clean()
        }
    }

    public boolean isInPluginLibs(it) {
        return pluginLibs.contains(it)
    }

    protected void resolveDependencies() {

        initProvidedLibs()

        prepareProvidedLibs()

        removeDependents()

        addProvideLibs()
    }

    protected void initProvidedLibs() {
        println project.name + ":initProvidedLibs"
        project.configurations.compile.dependencies.forEach({
            if (it instanceof DefaultSelfResolvingDependency) {
                for (def file : it.resolve()) {
                    if (shouldAddToProvidedLibs(file.name)) {
                        println "add:" + file
                        providedLibs.add(file)
                    }
                }
            } else if (shouldAddToProvidedLibs(it.name)) {
                println "add:" + it.name
                providedLibs.add(it)
            }
        })
    }

    protected void prepareProvidedLibs() {
        println providedLibs
        providedLibs.forEach({
            def jarFile = getDenpendencyJarFile(it)
            def destFileName
            if (it instanceof File) {
                destFileName = it.name
            } else {
                destFileName = getJarName(it)
            }
            println jarFile

            if (jarFile.exists()) {
                println "copy:$jarFile to $providedLibsDir"
                project.copy {
                    from jarFile
                    into providedLibsDir
                    rename (jarFile.name, destFileName)
                }
            }
        })
    }

    protected void removeDependents() {
        println "remove dependencies:"
        providedLibs.forEach({ println it })
        project.configurations.compile.dependencies.removeAll(providedLibs)
    }

    protected void addProvideLibs() {
        println project.name + ":addProvideLibs"
        def baseJars = project.fileTree(dir: providedLibsDir, include: ['*.jar'])
        println baseJars
        project.dependencies.add('provided', baseJars)
    }

    static String getJarName(it) {
        if (it instanceof File) {
            return it.name
        }
        return "${it.name}-${it.version}.jar"
    }

    File getDenpendencyJarFile(it) {
        def jarFile
        if (it instanceof Project || it instanceof Dependency) {
            jarFile = new File(explodedAarDir, "$it.group/$it.name/$it.version/jars/classes.jar")
        }
        if (jarFile == null || !jarFile.exists()) {
            // for external dependency
            jarFile = new File(prepareLibsDir, getJarName(it))
            println jarFile
        }
        return jarFile
    }

    protected boolean shouldAddToProvidedLibs(it) {
        return false
    }

    void clean() {
        if (providedLibsDir.exists()) {
            providedLibsDir.deleteDir()
        }
        if (prepareLibsDir.exists()) {
            prepareLibsDir.deleteDir()
        }
    }
}
