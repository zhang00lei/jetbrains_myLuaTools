import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToColon extends AnAction {
    private String packageName;
    private Map<String, String> funcInfoMap = new LinkedHashMap<>();

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        //获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //获取当前类文件的路径
        String classPath = psiFile.getVirtualFile().getPath();
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        ToolsUtil.reformatJavaFile(psiElement, project);
        ToColonFunc(classPath, project);
    }

    private String GetPackageName(List<String> fileContent) {
        for (int i = fileContent.size() - 1; i >= 0; i--) {
            String lineInfo = fileContent.get(i);
            if (lineInfo.contains("{") || lineInfo.contains("(")) {
                continue;
            }

            if (lineInfo.startsWith("return")) {
                return lineInfo.replace("return", "").trim();
            }
        }

        return "";
    }

    private void ToColonFunc(String luaFilePath, Project project) {
        List<String> fileContent = FileUtil.readUtf8Lines(luaFilePath);
        packageName = GetPackageName(fileContent);
        if (StrUtil.isEmpty(packageName)) {
            return;
        }
        this.funcInfoMap = ToolsUtil.getFuncInfo(fileContent, packageName);
        if (this.funcInfoMap.size() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        List<String> luaFuncList = new ArrayList<>();
        for (String lineInfo : fileContent) {
            if (lineInfo.startsWith("local function ")) {
                String luaInfo = lineInfo.trim();
                String funcName = lineInfo.replace("local function ", "").split("\\(")[0];
                boolean isPrivateFunc = ToolsUtil.isPrivateFunc(funcName, fileContent, packageName);
                if (isPrivateFunc) {
                    sb.append(luaInfo + "\n");
                } else {
                    String param = luaInfo.split("\\(")[1];
                    if (param.contains("self")) {
                        param = param
                                .replace("self,", "")
                                .replace("self ,", "")
                                .replace("self", "")
                                .replace(")", "")
                                .trim();
                        sb.append(StrUtil.format("function {}:{}({})\n", packageName, funcName, param));
                        luaFuncList.add(StrUtil.format("{}.{} = {}", packageName, funcName, funcName));
                    } else {
                        sb.append(luaInfo + "\n");
                    }
                }
            } else {
                if (StrUtil.isEmpty(lineInfo) || !luaFuncList.contains(lineInfo)) {
                    sb.append(lineInfo + "\n");
                }
            }
        }
        FileWriter write = new FileWriter(luaFilePath);
        write.write(sb.toString());
    }
}
