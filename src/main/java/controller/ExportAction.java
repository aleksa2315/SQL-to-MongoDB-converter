package controller;

import gui.view.MainFrame;
import resource.data.Row;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExportAction extends AbstractAction{
    private List<String> resultSetArray=new ArrayList<>();

    public ExportAction(){
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        putValue(SMALL_ICON, loadIcon("ss"));
        putValue(NAME, "Export");
        putValue(SHORT_DESCRIPTION, "Result Set Export");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            File csvOutputFile = new File("temp.csv");

            FileWriter fileWriter = new FileWriter(csvOutputFile, false);


            for (Row row : MainFrame.getInstance().getAppCore().getRowList()) {
                fileWriter.write(row.getName() + row.getFields() + ";\n");
            }

            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("DONE");

    }

}
