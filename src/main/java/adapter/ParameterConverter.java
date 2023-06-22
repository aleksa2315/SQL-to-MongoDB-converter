package adapter;

import com.google.protobuf.MapEntry;
import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParameterConverter {
    SQLQuery sqlQuery;
    SQLQuery mongoQuery;

    public ParameterConverter(SQLQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
        mongoQuery = new SQLQuery();
    }

    public SQLQuery convertParameters() {
        int counter = -1;
        for (Clause clause : sqlQuery.getClauses()) {
            counter++;
            switch (clause.getClauseName()) {
                case "select" -> getSelectedClause(clause, counter);
                case "from" -> getfrom(clause, counter);
                case "asc", "desc", "order by" -> getAscendingDescendingClause(clause, counter);
                case "like" -> getLikeClause(clause, counter);
                case "using" -> getJoinUsingClause(clause, counter);
                case "on" -> getJoinOnClause(clause, counter);
                case "is null", "is not null" -> getnullClause(clause, counter);
                case "where" -> getWhereClause(clause, counter);
                case "group by" -> getGroupByClause(clause, counter);
                case "or","and" -> getInOrClause(clause, counter);
                case "in"->getInClause(clause,counter);
            }

        }

        return mongoQuery;
    }
    public void getInClause(Clause clause, int counter){
        Clause newMongoClause = new Clause(clause.getClauseName(), counter);
        Clause lastWhere = sqlQuery.getClauses().get(clause.getPositionInQuery()-1);
        String parameter = lastWhere.getArguments().get(0);
        StringBuilder inParams = new StringBuilder();
        for(String string : clause.getArguments()){
            inParams.append(string+",");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parameter+":"+inParams.substring(1,inParams.length()-2));
//        System.out.println("|"+stringBuilder.toString()+"|");
        mongoQuery.getClauses().add(newMongoClause);
        newMongoClause.getArguments().add(stringBuilder.toString());
    }
    public void getInOrClause(Clause clause, int counter){

        Clause newMongoClause = new Clause(clause.getClauseName(), counter);
        Clause lastWhere = sqlQuery.getClauses().get(clause.getPositionInQuery()-1);
        String paramsLeft = lastWhere.getArguments().get(0);
        String paramsRight = clause.getArguments().get(0);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        newMongoClause.getArguments().add(paramsLeft);
        newMongoClause.getArguments().add(paramsRight);
        mongoQuery.getClauses().add(newMongoClause);
    }
    public void getWhereClause(Clause clause, int counter) {
        String param = clause.getArguments().get(0);
        StringBuilder stringBuilder = new StringBuilder();
        int signpos = 0;
        for (int i = 0; i < param.length() - 1; i++) {
            if (param.charAt(i) > 59 && param.charAt(i) < 63) {
                stringBuilder.append(param.charAt(i));
                if (param.charAt(i + 1) > 59 && param.charAt(i + 1) < 63)
                    stringBuilder.append(param.charAt(i + 1));
                break;
            }
            signpos++;

        }
        if (stringBuilder.length() == 0)
            return;
        String arg = null;

        switch (stringBuilder.toString()) {
            case ">" -> arg = new String("&gt");
            case "<" -> arg = new String("&lt");
            case ">=" -> arg = new String("&gte");
            case "<=" -> arg = new String("&lte");
            case "=" -> arg = new String("&eq");
        }
        Clause newMongoClause = new Clause("find", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        String param1 = clause.getArguments().get(0).substring(0, signpos).trim();
        if (arg.length() != 1)
            signpos += 2;
        String param2 = clause.getArguments().get(0).substring(signpos).trim();
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(param1 + ":" + arg + ":" + param2);
        newMongoClause.getArguments().add(stringBuilder1.toString());
        mongoQuery.getClauses().add(newMongoClause);

    }
    //select * from employees where department_id = 1 or department_id = 2;
    public void getnullClause(Clause clause, int counter) {
        Clause newMongoClause = new Clause("null", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        String param = sqlQuery.getClauses().get(newMongoClause.getPositionInQuery() - 1).getArguments().get(0);
        StringBuilder stringBuilder = new StringBuilder();
        if (clause.getClauseName().contains("not"))
            stringBuilder.append(param + ": " + "{ $ne : null }");
        else
            stringBuilder.append(param + ": null");
        newMongoClause.getArguments().add(stringBuilder.toString());
        mongoQuery.getClauses().add(newMongoClause);
    }

    private void getfrom(Clause clause, int counter) {
        Clause newMongoClause = new Clause("database", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        newMongoClause.getArguments().add(clause.getArguments().get(0));
        mongoQuery.getClauses().add(newMongoClause);
    }

    private void getSelectedClause(Clause currClause, int counter) {
        Clause newMongoClause = new Clause("projection", counter);
        newMongoClause.setClauseNumber(currClause.getClauseNumber());
        if (!currClause.getAggerations().isEmpty()) {
            String newAggretaion = currClause.getAggerations().get(0);
            String aggType = newAggretaion.substring(0, newAggretaion.indexOf("-"));
            String arg = newAggretaion.substring(newAggretaion.indexOf("-") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("$" + aggType + ":" + arg);
            newMongoClause.getAggerations().add(stringBuilder.toString());
        }
        for (String argument : currClause.getArguments()) {

            String newArg = new String(argument + ":1");
            newMongoClause.getArguments().add(newArg);
        }
        mongoQuery.getClauses().add(newMongoClause);
    }


    private void getAscendingDescendingClause(Clause clause, int counter) throws IndexOutOfBoundsException {
        String sign;
        Clause newMongoClause = new Clause("sort", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        Clause lastorderby = sqlQuery.getClauses().get(newMongoClause.getPositionInQuery() - 1);
        if (clause.getClauseName().equals("order by") && !sqlQuery.containsascdesc()) {
            sign = new String("+1");
            lastorderby = clause;
        } else if (clause.getClauseName().equals("asc"))
            sign = new String("+1");
        else
            sign = new String("-1");

        StringBuilder stringBuilder = new StringBuilder();
        if (lastorderby.getArguments().isEmpty()) {
            String newAggretaion = lastorderby.getAggerations().get(0);
            String aggType = newAggretaion.substring(0, newAggretaion.indexOf("-"));
            String arg = newAggretaion.substring(newAggretaion.indexOf("-") + 1);
            stringBuilder.append("$" + aggType + ":" + arg + ":" + sign);
            newMongoClause.getAggerations().add(stringBuilder.toString());
        } else {
            stringBuilder.append(lastorderby.getArguments().get(0) + ":" + sign);
            newMongoClause.getArguments().add(stringBuilder.toString());
        }
        mongoQuery.getClauses().add(newMongoClause);
    }

    private void getLikeClause(Clause clause, int counter) {
        Clause newMongoClause = new Clause("like", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        Clause lastBeforeLike = sqlQuery.getClauses().get(newMongoClause.getPositionInQuery() - 1);
        String whereAndParam = lastBeforeLike.getArguments().get(0);
        String likeParam = clause.getArguments().get(0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(whereAndParam + ":");
        if (likeParam.charAt(1) == '%' && likeParam.charAt(likeParam.length() - 2) == '%')
            stringBuilder.append(likeParam.substring(2, likeParam.length() - 2));
        else if (likeParam.contains("%") && likeParam.charAt(1) == '%') {
            String temp = likeParam.substring(2, likeParam.length() - 1);
            stringBuilder.append("^" + temp);
        } else if (likeParam.charAt(likeParam.length() - 2) == '%') {
            String temp = likeParam.substring(1, likeParam.length() - 2);
            stringBuilder.append(temp + "$");
        } else
            stringBuilder.append("$eq:" + likeParam.substring(1, likeParam.length() - 1));
        System.out.println(stringBuilder);
        newMongoClause.getArguments().add(stringBuilder.toString());
        mongoQuery.getClauses().add(newMongoClause);
    }

    private void getJoinUsingClause(Clause clause, int counter) {
        Clause newMongoClause = new Clause("lookup", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        Clause lastJoin = sqlQuery.getClauses().get(newMongoClause.getPositionInQuery() - 1);
        String joinParameter = lastJoin.getArguments().get(0);
        String usingParameter = clause.getArguments().get(0).substring(1, clause.getArguments().get(0).length() - 1);
        // String usingParameter = clause.getArguments().get(0);
        newMongoClause.getArguments().add(joinParameter);
        newMongoClause.getArguments().add(usingParameter);
        newMongoClause.getArguments().add(usingParameter);
        newMongoClause.getArguments().add("joined_" + usingParameter);
        mongoQuery.getClauses().add(newMongoClause);

    }

    private void getJoinOnClause(Clause clause, int counter) {
        Clause newMongoClause = new Clause("lookup", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        String param = clause.getArguments().get(0).substring(1, clause.getArguments().get(0).length() - 1);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < param.length() - 1; i++)
            if (param.charAt(i) > 59 && param.charAt(i) < 63)
                stringBuilder.append(param.charAt(i));

        String params[] = param.split(stringBuilder.toString());
        String arg1params[] = params[0].split("\\.");
        String arg1 = arg1params[arg1params.length-1];
        arg1 = arg1.substring(0,arg1.length()-1);
        Clause lastJoin = sqlQuery.getClauses().get(newMongoClause.getPositionInQuery() - 1);
        String joinParameter = lastJoin.getArguments().get(0);
        if(stringBuilder.toString().equals("=")){
            newMongoClause.getArguments().add(joinParameter);
            newMongoClause.getArguments().add(arg1);
            newMongoClause.getArguments().add(arg1);
            newMongoClause.getArguments().add("joined_" + arg1);
            mongoQuery.getClauses().add(newMongoClause);

        }
//select last_name,department_name from hr.employees join hr.departments on (hr.employees.department_id = hr.departments.department_id)
    }

    // group skuplja svoje agregacije i parametre, dodaje agregacije iz selecta
    private void getGroupByClause(Clause clause, int counter) {
        Clause newMongoClause = new Clause("group", counter);
        newMongoClause.setClauseNumber(clause.getClauseNumber());
        for(String string : clause.getArguments())
            newMongoClause.getArguments().add(string.trim());

        for(String string : clause.getAggerations())
            newMongoClause.getAggerations().add(string.trim());

        Clause select = sqlQuery.getClauses().get(0);
        for(String string : select.getAggerations())
            newMongoClause.getAggerations().add(string.trim());
        System.out.println(newMongoClause.getArguments());
        System.out.println(newMongoClause.getAggerations());
        mongoQuery.getClauses().add(newMongoClause);
    }
}
