package controller;

import gui.view.MainFrame;

import java.rmi.server.ExportException;

public class ActionManager {
    private ExitAction exitAction;
    private ExportAction exportAction;
    public ActionManager() {
        exitAction = new ExitAction(MainFrame.getInstance());
        exportAction = new ExportAction();
    }

    public ExportAction getExportAction() {
        return exportAction;
    }

    public void setExportAction(ExportAction exportAction) {
        this.exportAction = exportAction;
    }

    public ExitAction getExitAction() {
        return exitAction;
    }

    public void setExitAction(ExitAction exitAction) {
        this.exitAction = exitAction;
    }
}
