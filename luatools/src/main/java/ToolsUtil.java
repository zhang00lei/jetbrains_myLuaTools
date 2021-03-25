import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsUtil {
    public static void reformatJavaFile(PsiElement theElement, Project project) {
        if (theElement == null) {
            return;
        }
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            codeStyleManager.reformat(theElement);
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "error", Messages.getInformationIcon());
        }
    }

    public static Map<String, String> getFuncInfo(List<String> fileContent, String packageName) {
        Map<String, String> funcMap = new LinkedHashMap<>();
        for (String lineInfo : fileContent) {
            String luaInfo = lineInfo.trim();
            String funcInfo = StrUtil.format("function {}:", packageName);
            if (luaInfo.startsWith(funcInfo)) {
                String funcName = luaInfo.replace(funcInfo, "").split("\\(")[0];
                String param = luaInfo.split("\\(")[1];
                param = param.replace(")", "").trim();
                funcMap.put(funcName, param);
                continue;
            }

            String funcPrefix = "local function";
            if (luaInfo.startsWith(funcPrefix)) {
                String funcName = luaInfo.replace(funcPrefix, "").split("\\(")[0];
                String param = luaInfo.split("\\(")[1];
                param = param.replace(")", "").trim();
                funcMap.put(funcName, param);
            }
        }
        return funcMap;
    }

    public static boolean isPrivateFunc(String funcName, List<String> fileContent, String packageName) {
        for (String lineInfo : fileContent) {
            if (lineInfo.startsWith("--")) {
                continue;
            }
            if (isContain(lineInfo, funcName)) {
                if (lineInfo.startsWith(StrUtil.format("{}.{} = {}", packageName, funcName, funcName))) {
                    continue;
                }
                if (lineInfo.startsWith("local function " + funcName)) {
                    continue;
                }
                if (lineInfo.startsWith(StrUtil.format("function {}:{}", packageName, funcName))) {
                    continue;
                }

                if (isContain(lineInfo, "self." + funcName)) {
                    continue;
                }
                if (isContain(lineInfo, "self:" + funcName)) {
                    continue;
                }
                String funcNameInfo = funcName + "(";
                if (lineInfo.trim().startsWith(funcNameInfo)) {
                    return true;
                }
                if (!lineInfo.trim().contains("." + funcNameInfo)
                        && !lineInfo.trim().contains(":" + funcNameInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isContain(String source, String subItem) {
        String pattern = StrUtil.format("\\b{}\\b", subItem);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    public static String getPackageName(List<String> fileContent) {
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
}
