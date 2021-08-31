package com.johnrey.codechecker.util;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.project.Project;
import com.johnrey.codechecker.StatisticWindowFactory;

public class CheckerUtil {
    public static String getSaveKey() {
        return "IgnorePath";
    }

    public static String getCheckerPath(Project project) {
        String path = StatisticWindowFactory.project.getBasePath();
        path = StrUtil.replace(path, "/", "\\");
        path += "\\";
        return path;
    }
}
