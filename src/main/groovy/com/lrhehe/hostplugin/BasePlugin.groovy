package com.lrhehe.hostplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.logging.StyledTextOutput
import org.gradle.logging.StyledTextOutputFactory

public class BasePlugin implements Plugin<Project> {

    Project project

    public void apply(Project project) {
        if (Log.out == null) {
            Log.out = project.gradle.services.get(StyledTextOutputFactory).create('')
        }

        this.project = project
    }

    public static final class Log {

        protected static StyledTextOutput out

        public static void header(String text) {
            out.style(StyledTextOutput.Style.UserInput)
            out.withStyle(StyledTextOutput.Style.Info).text('[HostPlugin] ')
            out.println(text)
        }

        public static void success(String text) {
            out.style(StyledTextOutput.Style.Normal).format('\t%-64s', text)
            out.withStyle(StyledTextOutput.Style.Identifier).text('[  OK  ]')
            out.println()
        }

        public static void footer(String text) {
            out.style(StyledTextOutput.Style.UserInput).format('\t%s', text).println()
        }
    }
}