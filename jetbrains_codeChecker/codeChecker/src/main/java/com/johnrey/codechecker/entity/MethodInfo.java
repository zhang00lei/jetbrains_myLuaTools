package com.johnrey.codechecker.entity;

import java.util.List;

/**
 * 方法体信息
 */
public class MethodInfo {
    private String methodName;
    private List<String> methodParam;
    private String methodBody;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(List<String> methodParam) {
        this.methodParam = methodParam;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }
}
