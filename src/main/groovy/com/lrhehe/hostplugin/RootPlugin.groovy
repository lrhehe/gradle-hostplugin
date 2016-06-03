package com.lrhehe.hostplugin;

import org.gradle.api.Project;

/**
 * @Author ray
 * @Date 5/23/16.
 */
public class RootPlugin extends BaseHPPlugin {

    @Override
    public void apply(Project project) {
        super.apply(project)
        readConfigFile()
        applyPlugins()
    }

    private void applyPlugins() {
        project.subprojects {
            if (it.name.equals(host)) {
                it.apply plugin: HostPlugin
            } else if (it.name.equals(plugin)) {
                it.apply plugin: PluginPlugin
            }
        }
    }

    void readConfigFile() {
        def rootP = project.rootProject
        File propFile = new File(rootP.projectDir, 'hostplugin.config');
        if (propFile.exists()) {
            println propFile
            def Properties props = new Properties()
            props.load(new FileInputStream(propFile))
            rootP.ext.host = props.containsKey('host') ? props['host'] : ""
            rootP.ext.plugin = props.containsKey('plugin') ? props['plugin'] : ""
            String pluginLibsString = props.containsKey('pluginLibs') ? props['pluginLibs'] : ""
            rootP.ext.pluginLibs = pluginLibsString.split(",")
        }
        host = rootP.host
        plugin = rootP.plugin
        pluginLibs = rootP.pluginLibs
        println "host:$host"
        println "plugin:$plugin"
        println "pluginLibs:$pluginLibs"
    }
}
