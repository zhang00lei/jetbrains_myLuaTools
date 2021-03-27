import cn.hutool.core.io.FileUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class AllToColon extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object[] psiDirectories = e.getData(PlatformDataKeys.SELECTED_ITEMS);
        for (Object selectDir : psiDirectories) {
            PsiDirectory psiDirectory = (PsiDirectory) selectDir;
            if (psiDirectory.isDirectory()) {
                String path = psiDirectory.getVirtualFile().getPath();
                ToolsUtil.dirFormatFunc(path, false);
            }
        }
    }
}
