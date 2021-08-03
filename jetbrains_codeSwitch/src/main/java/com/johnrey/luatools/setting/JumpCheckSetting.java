package com.johnrey.luatools.setting;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.johnrey.luatools.ui.UIJumpCheck;
import org.jetbrains.annotations.NotNull;

public class JumpCheckSetting extends AnAction {
    private UIJumpCheck uiJumpCheck;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (uiJumpCheck == null) {
            uiJumpCheck = new UIJumpCheck();
        }
        uiJumpCheck.setEnable(project, true);
    }
}
