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

    private JFileChooser fileChooser;
    private DefaultTableModel tableModel;

    private MainLogicSolution mainLogicSolution = new MainLogicSolution();

    public Dialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        panelOKCancelButtons.setVisible(false);
        initModelForJTable();


        buttonLoadHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx"));
                int isFileSelectedInt = fileChooser.showOpenDialog(contentPane);
                if (isFileSelectedInt == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    mainLogicSolution.loadInfo(selectedFile);
                    updateTableModel();
                }
            }
        });

        buttonCheckAllHostsConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainLogicSolution.checkAllHostsConnection();
                updateTableModel();
            }
        });
    }

    private void initModelForJTable() {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Hosts");
        tableModel.addColumn("Status");
        tableMainHostsInfo.setModel(tableModel);
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
