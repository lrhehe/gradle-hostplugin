package com.lrhehe.hostplugin

import org.gradle.api.Project

/**
 * @Author ray
 * @Date 5/23/16.
 */
public class HostPlugin extends BaseHPPlugin {

    @Override
    public void apply(Project project) {
        super.apply(project)
        if (!HPBUILD) {
            println "use (assemble{flavor}{buildtype}) to prepare libs"
            println "use (build{Flavor}Plugin -P$BUILD_FLAG)  to use hostplugin build plugin apk"
            println "use (assemble{flavor}{buildtype} -P$BUILD_FLAG)  to use hostplugin build host apk"
        }

        project.afterEvaluate {
            if (HPBUILD) {
                resolveDependencies()
            }

            project.android.applicationVariants.all { variant ->
                // While release variant created, everything of `Android Plugin' should be ready
                // and then we can do some extensions with it
                configureVariant(variant)
            }
        }
    }

    protected void configureVariant(Object variant) {
        if (!HPBUILD) {
            variant.assemble.doLast {
                prepareLibs()
            }
        }
    }

    void prepareLibs() {
        Log.header("prepareLibs:")
        project.configurations.compile.dependencies.all {
            def depFiles = project.configurations.compile.files(it)
            depFiles.each {
                println it
                def fromFile = it
                if (it.name.endsWith(".jar")) {
                    project.copy {
                        from fromFile
                        into prepareLibsDir
                    }
                }
            }
        }
    }

    @Override
    protected boolean shouldAddToProvidedLibs(it) {
        return it == plugin || isInPluginLibs(it)
    }
}
