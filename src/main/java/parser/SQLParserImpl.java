package parser;

import com.sun.tools.javac.Main;
import database.Database;
import database.DatabaseImpl;
import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import gui.view.MainFrame;
import utils.SQLKeyWords;

import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.SQLKeyWords.getNames;

public class SQLParserImpl implements SQLParser{

    @Override
    public SQLQuery parseSQL(String string) {
        SQLQuery sqlQuery = new SQLQuery();
        sqlQuery.setSqlQuery(splitByClause(string));
        fillClauses(sqlQuery);
        for(Clause clause : sqlQuery.getClauses()){
            System.out.print("Position: " + clause.getPositionInQuery() +" " + clause.getClauseName() +clause.getClauseNumber() + " Parameters: " + clause.getArguments() +"Aggregations: " + clause.getAggerations());
            System.out.println();
        }
        return  sqlQuery;
    }

    public Map<String,String> splitByClause(String query){
        int counter = 0;
        String[] keywords = {"FROM", "SELECT", "WHERE", "GROUP BY", "ORDER BY", "JOIN", "IS NULL", "IN", "LIKE","ON","AND","OR","ASC","DESC","USING","LEFT","RIGHT","FULL","IS NOT NULL"};
        Map<String,String> parsed = new LinkedHashMap<>();

        String regex = "\\b(" + String.join("|", keywords) + ")\\b";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
//SELECT column1, column2 FROM table1 WHERE column3 = 'value' ORDER BY column1 JOIN table2 ON table1.column_name = table2.column_name;
        int start = 0;
        while (matcher.find(start)){
            String keyword = matcher.group();

            int clauseBegin = matcher.end();
            int clauseEnd = query.length();
            if (matcher.find()){
                clauseEnd = matcher.start();
            }

            String clause = query.substring(clauseBegin,clauseEnd).trim();
            keyword.toLowerCase();
            clause.toLowerCase();
            parsed.put(keyword.concat(String.valueOf(counter++)),clause);

//            if (parsed.containsKey(keyword)) {
//                parsed.put(keyword.concat(String.valueOf(counter)),clause);
//                counter++;
//            }else parsed.put(keyword,clause);
            start = clauseEnd;

        }

        return parsed;
    }
    public void fillClauses(SQLQuery sqlQuery){
        sqlQuery.setClauses(new ArrayList<>());
        for (Map.Entry<String, String> entry : sqlQuery.getSqlQuery().entrySet()) {
            String key = new String(entry.getKey());
            char intstr = key.charAt(key.length() - 1);
            int intValue = Character.getNumericValue(intstr);
            key = key.substring(0,key.length()-1);
            Clause newClause = new Clause(key,intValue);
            newClause.checkIfExists(sqlQuery.getClauses());
            for(String parameter : entry.getValue().split(",")){
                if(parameter.equals(""))
                    continue;
                parameter = parameter.trim();
                if(!parameter.contains("(") || key.equals("using") || key.equals("on") || key.equals("in")) {
                    newClause.getArguments().add(parameter);
                }else{
                    String aggregationType = new String(parameter.substring(0,parameter.indexOf("(")));
                    String aggParam =  new String(parameter.substring(parameter.indexOf("(")+1,parameter.indexOf(")")));
                    newClause.getAggerations().add(aggregationType.trim()+"-"+aggParam.trim());
                }
            }
            sqlQuery.getClauses().add(newClause);
        }
    }

    public static boolean containsKeyIgnoreCase(Map<String, String> map, String key) {
        for (String mapKey : map.keySet())
            if (mapKey.equalsIgnoreCase(key))
                return true;
        return false;
    }

}
