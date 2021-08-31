package com.johnrey.codechecker.window;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.johnrey.codechecker.CodeFileChecker;
import com.johnrey.codechecker.entity.StatisticInfo;
import com.johnrey.codechecker.util.CheckerUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class StatisticWindow {
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    private final Object[] columnNames = {"File Path", "Total Lines", "Comment Lines",
            "Blank Lines", "Max Params Count", " Max Method Body Lines"};
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable tbContent;
    private JLabel labCount;
    private JButton btnRefresh;
    private JButton btnSetting;
    private JToolBar toolBar;
    private JPopupMenu popUpMenu;
    private Project project;

    public StatisticWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        refreshInfo();
        setListeners();
        createPopupMenu();
    }

    private void refreshInfo() {
        String path = CheckerUtil.getCheckerPath(project);
        FileFilter fileFilter = pathname -> pathname.getName().endsWith(".lua");
        List<File> files = FileUtil.loopFiles(path, fileFilter);
        files = CollectionUtil.filter(files, fileInfo -> !isIgnorePath(fileInfo.getAbsolutePath()));
        labCount.setText("Count:" + files.size());
        Object[][] data = new Object[files.size()][];
        int idx = 0;
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            if (isIgnorePath(filePath)) {
                continue;
            }
            StatisticInfo statisticInfo = CodeFileChecker.checkerLuaFile(filePath);
            List<Object> rowInfo = new ArrayList<>();
            rowInfo.add(StrUtil.replace(filePath, path, ""));
            rowInfo.add(statisticInfo.getTotalLines());
            rowInfo.add(statisticInfo.getCommentLines());
            rowInfo.add(statisticInfo.getBlankLines());
            rowInfo.add(CodeFileChecker.GetMaxParamMethodName(statisticInfo.getMethodInfoList()));
            rowInfo.add(CodeFileChecker.GetMaxMethodBodyLines(statisticInfo.getMethodInfoList()));
            data[idx] = rowInfo.toArray();
            idx++;
        }
        TableModel dataModel = getTableModel(data);
        tbContent.setModel(dataModel);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(dataModel);
        tbContent.setRowSorter(sorter);
    }

    private void saveIgnorePathInfo(String pathInfo) {
        String saveKey = CheckerUtil.getSaveKey();
        String saveInfo = propertiesComponent.getValue(saveKey, "");
        if (StrUtil.isEmpty(saveInfo)) {
            saveInfo = pathInfo;
        } else {
            saveInfo += ";" + pathInfo;
        }
        propertiesComponent.setValue(saveKey, saveInfo);
    }

    private boolean isIgnorePath(String pathInfo) {
        String saveKey = CheckerUtil.getSaveKey();
        String saveInfo = propertiesComponent.getValue(saveKey, "");
        String[] pathArray = StrUtil.splitToArray(saveInfo, ";");
        for (String pathTemp : pathArray) {
            if (StrUtil.startWith(pathInfo, pathTemp)) {
                return true;
            }
        }
        return false;
    }

    private TableModel getTableModel(Object[][] data) {
        TableModel dataModel = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public Class getColumnClass(int column) {
                Class returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        return dataModel;
    }

    private void createPopupMenu() {
        popUpMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem();
        item.setText("open");
        item.addActionListener(e -> {
            openFile();
        });
        popUpMenu.add(item);

        item = new JMenuItem();
        item.setText("ignore");
        item.addActionListener(e -> {
            int selectedRow = tbContent.getSelectedRow();
            String fileInfo = (String) tbContent.getValueAt(selectedRow, 0);
            String projectPath = CheckerUtil.getCheckerPath(project);
            saveIgnorePathInfo(projectPath + fileInfo);
            refreshInfo();
        });
        popUpMenu.add(item);

        item = new JMenuItem();
        item.setText("ignore the parent");
        item.addActionListener(e -> {
            int selectRow = tbContent.getSelectedRow();
            String fileInfo = (String) tbContent.getValueAt(selectRow, 0);
            String savePath = CheckerUtil.getCheckerPath(project) + fileInfo;
            File tempFile = new File(savePath);
            saveIgnorePathInfo(tempFile.getParent());
            refreshInfo();
        });
        popUpMenu.add(item);
    }

    private void setListeners() {
        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                refreshInfo();
            }
        });
        btnSetting.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        tbContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int focusedRowIndex = tbContent.rowAtPoint(e.getPoint());
                    if (focusedRowIndex == -1) {
                        return;
                    }
                    tbContent.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
                    popUpMenu.show(tbContent, e.getX(), e.getY());
                } else if (e.getClickCount() == 2) {
                    openFile();
                }
            }
        });
    }

    private void openFile() {
        int selectedRow = tbContent.getSelectedRow();
        String fileInfo = (String) tbContent.getValueAt(selectedRow, 0);
        String projectPath = CheckerUtil.getCheckerPath(project);
        String filePath = projectPath + fileInfo;
        File file = new File(filePath);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile != null) {
            new OpenFileDescriptor(project, virtualFile).navigate(true);
        }
    }

    public JComponent getContent() {
        return panel1;
    }
}
