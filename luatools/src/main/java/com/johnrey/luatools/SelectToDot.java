package com.johnrey.luatools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class SelectToDot extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object[] psiDirectories = e.getData(PlatformDataKeys.SELECTED_ITEMS);
        for (Object selectDir : psiDirectories) {
            PsiDirectory psiDirectory = (PsiDirectory) selectDir;
            if (psiDirectory.isDirectory()) {
                String path = psiDirectory.getVirtualFile().getPath();
                ToolsUtil.dirFormatFunc(path,true);
            }
        }
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ProjectUtil.guessProjectDir(project).refresh(false, true);
    }
}
