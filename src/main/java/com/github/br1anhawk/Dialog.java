package com.github.br1anhawk;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Dialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panelOKCancelButtons;
    private JTable tableMainHostsInfo;
    private JButton buttonLoadHosts;
    private JButton buttonCheckAllHostsConnection;
    private JProgressBar progressBar;
    private JLabel labelDurationChecking;
    private JButton buttonSettings;
    private JTextField textFieldSettingForPoolSize;
    private JPanel panelSettings;
    private JButton buttonClearData;
    private JButton buttonSaveResult;
    private JComboBox comboBoxOS;

    private JFileChooser fileChooser;
    private DefaultTableModel tableModel;

    private MainLogicSolution mainLogicSolution = new MainLogicSolution();

    private static final int DEFAULT_CHECKING_POOL_SIZE = UtilsKt.DEFAULT_CHECKING_POOL_SIZE;

    public Dialog() {
        super((Window) null);
        setContentPane(contentPane);
        setModal(true);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        getRootPane().setDefaultButton(buttonOK);

        panelOKCancelButtons.setVisible(false);
        initModelForJTable();

        comboBoxOS.addItem(UtilsKt.OS_WINDOWS_NAME);
        comboBoxOS.addItem(UtilsKt.OS_LINUX_NAME);

        textFieldSettingForPoolSize.setText(String.valueOf(DEFAULT_CHECKING_POOL_SIZE));

        buttonLoadHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx"));
                int isFileSelectedInt = fileChooser.showOpenDialog(contentPane);
                if (isFileSelectedInt == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    mainLogicSolution.loadInfo(selectedFile);
                    buttonLoadHosts.setEnabled(false);
                }
            }
        });

        buttonCheckAllHostsConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mainLogicSolution.checkAllHostsConnection();
                int poolSize = DEFAULT_CHECKING_POOL_SIZE;
                try {
                    poolSize = Integer.parseInt(textFieldSettingForPoolSize.getText());
                } catch (NumberFormatException exception) {
                    exception.printStackTrace();
                } finally {
                    mainLogicSolution.setMaxPoolSizeRoutersConnection(poolSize);
                }
                progressBar.setValue(0);
                AsyncTaskThread asyncTaskThread = new AsyncTaskThread(mainLogicSolution, labelDurationChecking, buttonCheckAllHostsConnection);
                asyncTaskThread.start();
            }
        });

        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panelSettings.isVisible()) {
                    panelSettings.setVisible(false);
                } else {
                    panelSettings.setVisible(true);
                }
                pack();
            }
        });

        buttonClearData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainLogicSolution = new MainLogicSolution();
                buttonLoadHosts.setEnabled(true);
                initModelForJTable();
            }
        });

        buttonSaveResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainLogicSolution.saveMainHostsInfoToFile();
            }
        });

        comboBoxOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (comboBoxOS.getSelectedItem().toString()) {
                    case UtilsKt.OS_WINDOWS_NAME:
                        mainLogicSolution.setOS(OperatingSystem.WINDOWS);
                        break;
                    case UtilsKt.OS_LINUX_NAME:
                        mainLogicSolution.setOS(OperatingSystem.LINUX);
                        break;
                }
            }
        });
    }

    private void initModelForJTable() {
        tableModel = new DefaultTableModel();
        tableMainHostsInfo.setModel(tableModel);
        mainLogicSolution.setTableModel(tableModel);
        mainLogicSolution.setProgressBar(progressBar);
    }

    private void updateTableModel() {
        while(tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        String[][] mainHostsInfo = mainLogicSolution.getHostsInfo();
        for (String[] row : mainHostsInfo) {
            Object[] dataRowForTable = new Object[mainHostsInfo[0].length];
            for (int i = 0; i < row.length; i++) {
                dataRowForTable[i] = row[i];
            }
            tableModel.addRow(dataRowForTable);
        }
    }

    public static void main(String[] args) {
        Dialog dialog = new Dialog();
        dialog.pack();
        dialog.setVisible(true);

        System.exit(0);
    }
}
