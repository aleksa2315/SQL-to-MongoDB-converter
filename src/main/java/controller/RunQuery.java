package controller;

import adapter.Adapter;
import adapter.AdapterImpl;
import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import gui.view.MainFrame;
import lombok.SneakyThrows;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.List;

public class RunQuery extends AbstractAction{
    public RunQuery(){
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.ALT_MASK));
        putValue(SMALL_ICON, loadIcon("ss"));
        putValue(NAME, "Run Query");
        putValue(SHORT_DESCRIPTION, "Run Query");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String query = MainFrame.getInstance().getTextPane().getText();
        SQLQuery sqlQuery = MainFrame.getInstance().getAppCore().getSqlParser().parseSQL(query);
//        System.out.println();
        if(!MainFrame.getInstance().getAppCore().getSqlValidator().validate(sqlQuery))
            return;

//        for(Clause clause : sqlQuery.getClauses()){
//            System.out.print("Position: " + clause.getPositionInQuery() +" " + clause.getClauseName() +clause.getClauseNumber() + " Parameters: " + clause.getArguments() +"Aggregations: " + clause.getAggerations());
//            System.out.println();
//        }
        SQLQuery mongoQuery = MainFrame.getInstance().getAppCore().getAdapter().adaptQuery(sqlQuery);
        List<Bson> aggregationPipline = MainFrame.getInstance().getAppCore().getAdapter().getAggregationPipline();

        MainFrame.getInstance().getAppCore().getExecutor().executeQuery(mongoQuery,aggregationPipline);
        //MainFrame.getInstance().getAppCore().getDatabase().mongo();
    }
}
