package com.johnrey.codechecker;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.johnrey.codechecker.window.StatisticWindow;
import org.jetbrains.annotations.NotNull;

public class StatisticWindowFactory implements ToolWindowFactory {
    public static Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        StatisticWindow statisticWindow = new StatisticWindow(toolWindow, project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content luaContent = contentFactory.createContent(statisticWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(luaContent);
    }
}
