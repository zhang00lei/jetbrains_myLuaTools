import cn.hutool.core.io.FileUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "LuaToolsSetting",
        storages = {@Storage(value = "LuaToolsSetting.xml")}
)
public class OpenSettingDialog extends AnAction {

    public static final String PATH_CONFIG_KEY = "PathConfig";
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String pathTemp = propertiesComponent.getValue(PATH_CONFIG_KEY, "");
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String pathInfo = Messages.showInputDialog("请输入目录路径，分号(;)间隔",
                "请输入目录路径", Messages.getInformationIcon(), pathTemp, null);
        String[] dirPath = pathInfo.split(";");
        for (String path : dirPath) {
            if (!FileUtil.exist(path)) {
                Messages.showMessageDialog(project, "路径输入有误，请检查", "提示", Messages.getInformationIcon());
                return;
            }
        }
        propertiesComponent.setValue(PATH_CONFIG_KEY, pathInfo);
    }
}
