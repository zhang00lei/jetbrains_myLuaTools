package com.johnrey.luatools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.johnrey.luatools.ui.*;

public class ToolsSetting extends AnAction {
    private UIPathSetting pathSetting;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (pathSetting == null) {
            pathSetting = new UIPathSetting();
        }
        pathSetting.setEnable(project, true);
    }
}
