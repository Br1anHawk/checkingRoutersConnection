package com.github.br1anhawk;

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
        try {
            mainLogicSolution.checkAllHostsConnection();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            labelDurationChecking.setText("The duration of the audit was " + mainLogicSolution.getLastTimeCheckingDurationInMs() / 1000 + " seconds");
            buttonCheckAllHostsConnection.setEnabled(true);
        }
    }
}
