package com.johnrey.luatools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.johnrey.luatools.ui.UIJumpCheck;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsUtil {
    private static List<String> jumpCheckInfoArray = new ArrayList<>();

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
        for (String lineInfoTemp : fileContent) {
            String lineInfo = lineInfoTemp.trim();
            if(strInJumpCheckInfo(lineInfo)){
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

    public static void toDotFunc(String luaFilePath) {
        List<String> fileContent = FileUtil.readUtf8Lines(luaFilePath);
        String packageName = ToolsUtil.getPackageName(fileContent);
        if (StrUtil.isEmpty(packageName)) {
            return;
        }
        Map<String, String> funcInfoMap = ToolsUtil.getFuncInfo(fileContent, packageName);
        if (funcInfoMap.size() == 0) {
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
                boolean isFind = strInJumpCheckInfo(lineInfo);
                if (isFind || StrUtil.isEmpty(lineInfo) || !luaFuncList.contains(lineInfo)) {
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

    public static void toColonFunc(String luaFilePath) {
        List<String> fileContent = FileUtil.readUtf8Lines(luaFilePath);
        String packageName = getPackageName(fileContent);
        if (StrUtil.isEmpty(packageName)) {
            return;
        }
        Map<String, String> funcInfoMap = ToolsUtil.getFuncInfo(fileContent, packageName);
        if (funcInfoMap.size() == 0) {
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
                    param = param.replace(")", "").replace(" ", "");
                    String[] paramArray = param.split(",");
                    if (paramArray.length >= 1 && paramArray[0].contains("self")) {
                        param = "";
                        for (int i = 1; i < paramArray.length; i++) {
                            param += paramArray[i];
                            if (i != paramArray.length - 1) {
                                param += ", ";
                            }
                        }
                        sb.append(StrUtil.format("function {}:{}({})\n", packageName, funcName, param));
                        luaFuncList.add(StrUtil.format("{}.{} = {}", packageName, funcName, funcName));
                    } else {
                        sb.append(luaInfo + "\n");
                    }
                }
            } else {
                boolean isFind = strInJumpCheckInfo(lineInfo);
                if (isFind || StrUtil.isEmpty(lineInfo) || !luaFuncList.contains(lineInfo)) {
                    sb.append(lineInfo + "\n");
                }
            }
        }
        FileWriter write = new FileWriter(luaFilePath);
        write.write(sb.toString());
    }

    public static void dirFormatFunc(String dirPath, boolean toDotFunc) {
        FileFilter filter = file -> file.getName().endsWith(".lua");
        List<File> fileList = FileUtil.loopFiles(dirPath, filter);
        for (File file : fileList) {
            if (toDotFunc) {
                ToolsUtil.toDotFunc(file.getAbsolutePath());
            } else {
                ToolsUtil.toColonFunc(file.getAbsolutePath());
            }
        }
    }


    private static List<String> getJumpCheckInfo() {
        if (jumpCheckInfoArray.isEmpty()) {
            String info = PropertiesComponent.getInstance().getValue(UIJumpCheck.JUMP_CHECK_KEY, "");
            String[] tempArray = info.split("\n");
            for (String temp : tempArray) {
                if (StrUtil.isNotEmpty(temp)) {
                    jumpCheckInfoArray.add(temp);
                }
            }
        }
        return jumpCheckInfoArray;
    }

    private static boolean strInJumpCheckInfo(String lineInfo) {
        if (jumpCheckInfoArray.isEmpty()) {
            String info = PropertiesComponent.getInstance().getValue(UIJumpCheck.JUMP_CHECK_KEY, "");
            String[] tempArray = info.split("\n");
            for (String temp : tempArray) {
                if (StrUtil.isNotEmpty(temp)) {
                    jumpCheckInfoArray.add(temp);
                }
            }
        }
        for (String infoTemp : jumpCheckInfoArray) {
            if (lineInfo.startsWith(infoTemp)) {
                return true;
            }
        }
        return false;
    }

}
