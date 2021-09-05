import javax.swing.*;

public class AsyncTaskThread extends Thread {

    private MainLogicSolution mainLogicSolution = null;
    private JLabel labelDurationChecking = null;

    public AsyncTaskThread() {
    }

    public AsyncTaskThread(MainLogicSolution mainLogicSolution, JLabel labelDurationChecking) {
        this.mainLogicSolution = mainLogicSolution;
        this.labelDurationChecking = labelDurationChecking;
    }

    @Override
    public void run() {
        mainLogicSolution.checkAllHostsConnection();
        labelDurationChecking.setText("The duration of the audit was " + mainLogicSolution.getLastTimeCheckingDurationInMs() / 1000 + " seconds");
    }
}
