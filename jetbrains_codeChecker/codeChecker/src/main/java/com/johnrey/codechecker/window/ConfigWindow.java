package com.johnrey.codechecker.window;

import cn.hutool.core.util.StrUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.johnrey.codechecker.util.CheckerUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfigWindow implements Configurable {
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    private JList listIgnore;
    private JLabel labCount;
    private JPanel panelMain;
    private JButton btnRemoveAll;
    private JButton btnRemoveSelect;
    private Project project;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Code Checker";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        String value = propertiesComponent.getValue(CheckerUtil.getSaveKey(), "");
        String[] infos = StrUtil.splitToArray(value, ";");
        DefaultListModel jListModel = new DefaultListModel();
        for (String info : infos) {
            if (StrUtil.isNotEmpty(info)) {
                jListModel.addElement(info);
            }
        }
        listIgnore.setModel(jListModel);
        labCount.setText("Count:" + listIgnore.getModel().getSize());
        setBtnListener();

        return panelMain;
    }

    private void setBtnListener() {
        btnRemoveAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                listIgnore.setModel(new DefaultListModel());
            }
        });
        btnRemoveSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = listIgnore.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                DefaultListModel jListModel = (DefaultListModel) listIgnore.getModel();
                jListModel.remove(index);
                listIgnore.setModel(jListModel);
            }
        });
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        int size = listIgnore.getModel().getSize();
        String saveInfo = "";
        for (int i = 0; i < size; i++) {
            String info = (String) listIgnore.getModel().getElementAt(i);
            saveInfo += info;
            if (i != size - 1) {
                saveInfo += ";";
            }
        }

        propertiesComponent.setValue(CheckerUtil.getSaveKey(), saveInfo);
    }
}
