import javax.swing.*;

public class AsyncTaskThread extends Thread {

    private MainLogicSolution mainLogicSolution = null;
    private JLabel labelDurationChecking = null;
    private JButton buttonCheckAllHostsConnection = null;

    public AsyncTaskThread() {
    }

    public AsyncTaskThread(MainLogicSolution mainLogicSolution, JLabel labelDurationChecking, JButton buttonCheckAllHostsConnection) {
        this.mainLogicSolution = mainLogicSolution;
        this.labelDurationChecking = labelDurationChecking;
        this.buttonCheckAllHostsConnection = buttonCheckAllHostsConnection;
    }

    @Override
    public void run() {
        buttonCheckAllHostsConnection.setEnabled(false);
        mainLogicSolution.checkAllHostsConnection();
        labelDurationChecking.setText("The duration of the audit was " + mainLogicSolution.getLastTimeCheckingDurationInMs() / 1000 + " seconds");
        buttonCheckAllHostsConnection.setEnabled(true);
    }
}
