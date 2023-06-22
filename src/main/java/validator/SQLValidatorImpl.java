package validator;

import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;

import javax.swing.*;

public class SQLValidatorImpl implements SQLValidator{

    SQLQuery currQuery;
    @Override
    public boolean validate(SQLQuery sqlQuery) {
        currQuery = sqlQuery;
        if(checkNecessary()){
            JOptionPane.showMessageDialog(null, "Upit mora sadrzati select i from, kao i njihove argumente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(checkWhereAggregation()){
            JOptionPane.showMessageDialog(null, "Where ne sme da ima agregaciju.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(checkSelectedGroupBy()){
            JOptionPane.showMessageDialog(null, "Sve što je selektovano a nije pod funkcijom agregacije, mora ući u GROUP BY!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(checkJoinParameter()){
            JOptionPane.showMessageDialog(null, "Spajanje tabela mora imati uslov za spajanje (USING ili ON).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private boolean checkNecessary(){
        Clause select = null;
        for(Clause clause : currQuery.getClauses())
            if(clause.getClauseName().equals("select")) {
                select = clause;
                break;
            }
        if(select == null || (select.getArguments().isEmpty() && select.getAggerations().isEmpty()))
            return true;

        Clause from = null;
        for(Clause clause2 : currQuery.getClauses())
            if(clause2.getClauseName().equals("from")) {
                from = clause2;
                break;
            }
        if(from == null || from.getArguments().size() == 0)
            return true;

        return false;
    }
    private boolean checkJoinParameter(){
        Clause join = null;
        for(Clause clause : currQuery.getClauses())
            if(clause.getClauseName().contains("join"))
                join = clause;
        if(join == null)
            return false;
        Clause onOrUsing = null;
        for(Clause clause : currQuery.getClauses())
            if(clause.getClauseName().equals("using") || clause.getClauseName().equals("on") && clause.getPositionInQuery() == join.getPositionInQuery()+1)
                onOrUsing = clause;

        if(onOrUsing == null)
            return true;

        return false;
    }
    private boolean checkWhereAggregation(){
        for(Clause clause : currQuery.getClauses()){
            if(clause.getClauseName().equals("where") && !clause.getAggerations().isEmpty())
                return true;
        }
        return false;
    }
    private boolean checkSelectedGroupBy(){
        Clause groupBy = null;
        Clause select = currQuery.getClauses().get(0);
        for(Clause clause : currQuery.getClauses())
            if(clause.getClauseName().equals("group by")){
                groupBy = clause;
                break;
            }
        if(groupBy == null)
            return false;

        for(String currParam: select.getArguments())
            if(!groupBy.getArguments().contains(currParam))
                return true;

        return false;
    }
}
