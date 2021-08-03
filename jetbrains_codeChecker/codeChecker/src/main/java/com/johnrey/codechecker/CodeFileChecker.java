package com.johnrey.codechecker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.johnrey.codechecker.entity.MethodInfo;
import com.johnrey.codechecker.entity.StatisticInfo;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFileChecker {

    public static StatisticInfo checkerLuaFile(String filePath) {
        if (StrUtil.endWith(filePath, ".lua")) {
            StatisticInfo statisticInfo = new StatisticInfo();
            List<String> lines = FileUtil.readLines(filePath, Charset.defaultCharset());
            statisticInfo.setTotalLines(lines.size());
            String allInfo = FileUtil.readString(filePath, Charset.defaultCharset());
            List<String> patternStr = new ArrayList<>();

            String regularStr = "\\blocal function\\b\\s*\\w+\\s*\\(\\s*[\\w,]*\\s*\\w*\\s*\\)";
            List<String> temp = getRegularMatchStr(allInfo, regularStr);
            patternStr.addAll(temp);

            regularStr = "\\bfunction\\b\\s*\\w+\\s*:\\s*\\w+\\s*\\(\\s*[\\w,]*\\s*\\w*\\s*\\)";
            temp = getRegularMatchStr(allInfo, regularStr);
            patternStr.addAll(temp);

            regularStr = "\\bfunction\\b\\s*\\w+\\s*\\.\\s*\\w+\\s*\\(\\s*[\\w,]*\\s*\\w*\\s*\\)";
            temp = getRegularMatchStr(allInfo, regularStr);
            patternStr.addAll(temp);

            regularStr = "\\bfunction\\b\\s*\\w+\\s*\\(\\s*[\\w,]*\\s*\\w*\\s*\\)";
            temp = getRegularMatchStr(allInfo, regularStr);
            patternStr.addAll(temp);

            List<MethodInfo> methodInfoList = new ArrayList<>();
            for (String methodLine : patternStr) {
                MethodInfo methodInfo = getLuaMethodInfo(methodLine, lines);
                methodInfoList.add(methodInfo);
            }
            statisticInfo.setMethodInfoList(methodInfoList);
            int blankLine = 0;
            int commentLine = 0;
            for (String line : lines) {
                if (StrUtil.isBlank(line)) {
                    blankLine++;
                }
                String tempLine = StrUtil.trimStart(line);
                if (StrUtil.startWith(tempLine, "--")) {
                    commentLine++;
                }
            }
            statisticInfo.setBlankLines(blankLine);
            statisticInfo.setCommentLines(commentLine);
            return statisticInfo;
        }
        return null;
    }

    private static MethodInfo getLuaMethodInfo(String methodLine, List<String> fileLines) {
        MethodInfo result = new MethodInfo();
        List<String> paramList = new ArrayList<>();
        //分析参数
        List<String> params = StrUtil.split(methodLine, "(");
        String temp = params.get(params.size() - 1);
        temp = StrUtil.replace(temp, " ", "");
        temp = StrUtil.replace(temp, ")", "");
        params = StrUtil.split(temp, ",");
        for (String param : params) {
            if (StrUtil.isNotEmpty(param)) {
                paramList.add(param);
            }
        }
        result.setMethodParam(paramList);
        //分析方法体
        String methodBody = "";
        for (int i = 0; i < fileLines.size(); i++) {
            if (StrUtil.startWith(fileLines.get(i), methodLine) && StrUtil.endWith(fileLines.get(i), " end")) {
                methodBody += fileLines.get(i);
            } else if (StrUtil.startWith(fileLines.get(i), methodLine)) {
                for (int j = i; j < fileLines.size(); j++) {
                    if (!StrUtil.startWith(fileLines.get(j), "end")) {
                        methodBody += fileLines.get(j) + "\n";
                    } else {
                        methodBody += fileLines.get(j);
                        break;
                    }
                }
            }
        }
        result.setMethodBody(methodBody);

        //分析方法名
        methodLine = StrUtil.replace(methodLine, "function ", "");
        methodLine = StrUtil.replace(methodLine, "local function ", "");
        String methodName = StrUtil.split(methodLine, "(").get(0);
        result.setMethodName(methodName);
        return result;
    }

    private static List<String> getRegularMatchStr(String content, String pattern) {
        List<String> result = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    /**
     * 获取脚本中方法参数最大的数量
     *
     * @param methodInfoList 方法信息列表
     * @return 最大数量
     */
    public static int GetMaxParamMethodName(List<MethodInfo> methodInfoList) {
        int result = 0;
        for (MethodInfo info : methodInfoList) {
            result = result > info.getMethodParam().size() ? result : info.getMethodParam().size();
        }
        return result;
    }

    /**
     * 获取方法体最大行数
     *
     * @param methodInfoList
     * @return
     */
    public static int GetMaxMethodBodyLines(List<MethodInfo> methodInfoList) {
        int result = 0;
        for (MethodInfo info : methodInfoList) {
            int lines = StrUtil.count(info.getMethodBody(), "\n");
            result = result > lines ? result : lines;
        }
        return result == 0 ? 0 : result + 1;
    }
}
