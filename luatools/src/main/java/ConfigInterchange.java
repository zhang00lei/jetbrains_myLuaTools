import cn.hutool.core.io.FileUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class ConfigInterchange extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String configPath = propertiesComponent.getValue(OpenSettingDialog.PATH_CONFIG_KEY, "");
        boolean toDot = propertiesComponent.getBoolean("ToDot", false);
        String[] dirPath = configPath.split(";");
        for (String path : dirPath) {
            if (!FileUtil.exist(path)) {
                continue;
            }
            ToolsUtil.dirFormatFunc(path, toDot);
        }
        propertiesComponent.setValue("ToDot", !toDot);
    }
}
