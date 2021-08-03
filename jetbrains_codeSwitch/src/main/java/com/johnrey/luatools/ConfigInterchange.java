package com.johnrey.luatools;

import cn.hutool.core.io.FileUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.johnrey.luatools.ui.UIPathSetting;

public class ConfigInterchange extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String configPath = propertiesComponent.getValue(UIPathSetting.PATH_CONFIG_KEY, "");
        boolean toDot = propertiesComponent.getBoolean("ToDot", false);
        String[] dirPath = configPath.split("\n");
        for (String path : dirPath) {
            if (!FileUtil.exist(path)) {
                continue;
            }
            ToolsUtil.dirFormatFunc(path, toDot);
        }
        propertiesComponent.setValue("ToDot", !toDot);
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ProjectUtil.guessProjectDir(project).refresh(false, true);
    }
}
