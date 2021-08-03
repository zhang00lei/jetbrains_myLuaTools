package com.johnrey.luatools.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIJumpCheck extends JDialog {
    public static final String JUMP_CHECK_KEY = "JumpCheck";
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    private JPanel panel1;
    private JTextArea taJumpCheck;
    private JButton btnCancel;
    private JButton btnConfirm;
    private Project project;

    public UIJumpCheck() {
        setContentPane(panel1);
        setModal(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveConfig();
                dispose();
            }
        });
    }

    public void setEnable(Project project, boolean enable) {
        String info = propertiesComponent.getValue(JUMP_CHECK_KEY, "");
        taJumpCheck.setText(info);
        this.project = project;
        setVisible(enable);
    }

    private void saveConfig() {
        propertiesComponent.setValue(JUMP_CHECK_KEY, taJumpCheck.getText());
        Messages.showMessageDialog(project, "保存成功", "提示", Messages.getInformationIcon());
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "输入跳过检测行的标志", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        taJumpCheck = new JTextArea();
        taJumpCheck.setMargin(new Insets(5, 5, 5, 5));
        panel1.add(taJumpCheck, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(420, 400), null, 0, false));
        btnCancel = new JButton();
        btnCancel.setText("取消");
        panel1.add(btnCancel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnConfirm = new JButton();
        btnConfirm.setText("确定");
        panel1.add(btnConfirm, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}