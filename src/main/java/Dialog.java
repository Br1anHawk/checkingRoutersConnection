import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.util.Arrays;

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

    private JFileChooser fileChooser;
    private DefaultTableModel tableModel;

    private MainLogicSolution mainLogicSolution = new MainLogicSolution();

    private static final int DEFAULT_CHECKING_POOL_SIZE = 25;

    public Dialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        panelOKCancelButtons.setVisible(false);
        initModelForJTable();

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
                AsyncTaskThread asyncTaskThread = new AsyncTaskThread(mainLogicSolution, labelDurationChecking);
                asyncTaskThread.start();
            }
        });

        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panelSettings.isVisible()) {
                    panelSettings.setVisible(false);
                    pack();
                } else {
                    panelSettings.setVisible(true);
                    pack();
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
