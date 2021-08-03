package com.johnrey.codechecker.window;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigWindow implements Configurable {
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Code Checker";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
