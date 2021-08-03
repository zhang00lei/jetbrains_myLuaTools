package com.johnrey.codechecker.entity;

import java.util.List;

/**
 * 单个文件统计信息
 */
public class StatisticInfo {
    private int totalLines;
    private int commentLines;
    private int blankLines;
    private List<MethodInfo> methodInfoList;

    public int getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getCommentLines() {
        return commentLines;
    }

    public void setCommentLines(int commentLines) {
        this.commentLines = commentLines;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public int getBlankLines() {
        return blankLines;
    }

    public void setBlankLines(int blankLines) {
        this.blankLines = blankLines;
    }
}
