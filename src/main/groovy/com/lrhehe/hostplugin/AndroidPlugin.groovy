package com.lrhehe.hostplugin

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultSelfResolvingDependency;

/**
 * @Author ray
 * @Date 5/23/16.
 */
public class AndroidPlugin extends BasePlugin {

    public static final String FD_INTERMEDIATES = "intermediates"
    public static final String FD_EXPLODED_AAR = "exploded-aar"

    public void apply(Project project) {
        super.apply(project)
    }
}
