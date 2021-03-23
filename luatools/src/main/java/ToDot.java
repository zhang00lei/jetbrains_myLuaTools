import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

public class ToDot extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        //获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //获取当前类文件的路径
        String classPath = psiFile.getVirtualFile().getPath();

        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        ToolsUtil.reformatJavaFile(psiElement, project);
        ToDotFunc(classPath, project);
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

    private void ToDotFunc(String luaFilePath, Project project) {
        List<String> fileContent = FileUtil.readUtf8Lines(luaFilePath);
        String packageName = GetPackageName(fileContent);
        if (StrUtil.isEmpty(packageName)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        List<String> luaFuncList = new ArrayList<>();
        for (String lineInfo : fileContent) {
            if (lineInfo.startsWith(StrUtil.format("function {}:", packageName))) {
                String luaInfo = lineInfo.trim();
                String funcName = luaInfo.replace(StrUtil.format("function {}:", packageName), "")
                        .split("\\(")[0];
                String param = luaInfo.split("\\(")[1];
                param = param.replace(")", "").trim();
                param = StrUtil.isEmpty(param) ? "self" : "self, " + param;
                sb.append(StrUtil.format("local function {}({})\n", funcName, param));
                luaFuncList.add(StrUtil.format("{}.{} = {}", packageName, funcName, funcName));
            } else {
                if (StrUtil.isEmpty(lineInfo) || !luaFuncList.contains(lineInfo)) {
                    sb.append(lineInfo + "\n");
                }
            }
        }
        for (String funcName : luaFuncList) {
            sb.append(funcName + "\n");
        }
        String content = sb.toString();
        String returnStr = StrUtil.format("return {}", packageName);
        content = content.replace("\n" + returnStr, "");
        content += returnStr;
        FileWriter write = new FileWriter(luaFilePath);
        write.write(content);
    }
}
