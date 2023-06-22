package adapter;

import com.mongodb.client.model.*;
import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import gui.view.MainFrame;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

public class Mapper {
    private SQLQuery mongoQuery;
    private List<Bson> aggregation;
    private String tabela;
    private ArrayList<String> polja;
    private String join;
    private String joinPolje;
    private ArrayList<String> poljaTabele;
    private int andor;


    public Mapper(SQLQuery mongoQuery) {
        this.mongoQuery = mongoQuery;
        join = null;
    }

    public List<Bson> doAggregation(){
        andor = 0;
        tabela = returnDBName(mongoQuery);
        poljaTabele = MainFrame.getInstance().getAppCore().getDatabase().getColumns(tabela);

        for(Clause clause : mongoQuery.getClauses()){
            System.out.print("Position: " + clause.getPositionInQuery() +" " + clause.getClauseName() +clause.getClauseNumber() + " Parameters: " + clause.getArguments() +"Aggregations: " + clause.getAggerations());
            System.out.println();
        }
//        System.out.println(tabela);

        aggregation = new ArrayList();

        for (Clause clause : mongoQuery.getClauses()){
            if (clause.getClauseName().contains("lookup")){
                String from = clause.getArguments().get(0);
                join = from;
                if(from.contains(".")){
                    from = from.substring(from.indexOf('.') + 1);
                }

                aggregation.add(lookup(from,clause.getArguments().get(1), clause.getArguments().get(1),from));
                aggregation.add(unwind("$"+from));
            }
        }

        for (Clause clause : mongoQuery.getClauses()){
            String clauseName = clause.getClauseName();

//            if (clauseName.contains("and")){
//                andMapping(clause);
//                andor = 1;
//
//            }else if (clauseName.contains("or")){
//                orMapping(clause);
//                andor = 1;
//            }else if (clauseName.contains("in")){
//                inMapping(clause);
//                andor =1;
//            }
//            System.out.println(clauseName);
            switch (clauseName){
                case "sort":
                    sortMapping(clause);
                    break;
                case "projection":
                    projectionMapping(clause);
                    break;
                case "find":
                    filterMapping(clause);
                    break;
                case "like":
                    matchMapping(clause);
                    break;
                case "group":
                    groupMapping(clause);
                    break;
                case "null":
                    nullMapping(clause);
                    break;
                case "and":
                    andMapping(clause);
                    andor=1;
                    break;
                case "or":
                    orMapping(clause);
                    andor = 1;
                    break;
                case "in":
                    inMapping(clause);
                    andor = 1;
                    break;
            }

        }
        return aggregation;

    }
    private void matchMapping(Clause clause) {
        String[] match = clause.getArguments().toArray(new String[0]);
        System.out.println(Arrays.toString(match));
        String expresion = match[0];
        String[] split = expresion.split(":");
        String polje = "";
        String type = "";
        String regex = "";
        if (split.length == 3) {
            polje = split[0];
            type = split[1];
            regex = split[2];
        } else {
            polje = split[0];
            type = split[1];
        }
        if (type.contains("eq")) {
            aggregation.add(match(eq(polje, regex)));
        } else if (type.contains("^")) {
            Pattern pattern = Pattern.compile(".*" + type.substring(1), Pattern.CASE_INSENSITIVE);
            aggregation.add(match(regex(polje, pattern)));
        } else if (type.contains("$")) {
            Pattern pattern = Pattern.compile("^" + type.substring(0, type.length() - 1), Pattern.CASE_INSENSITIVE);
            aggregation.add(match(regex(polje, pattern)));
        }else{
            Pattern pattern = Pattern.compile(type);
            aggregation.add(match(regex(polje,pattern)));
        }
    }

    private void filterMapping(Clause clause){
        String[] filter = clause.getArguments().toArray(new String[0]);
        for (String string : filter){
            String[] args = string.split(":");
            String polje = args[0];
            String type = args[1].substring(args[1].indexOf("&")+1);
            int by = Integer.parseInt(args[2]);

            switch (type){
                case "eq":
                    aggregation.add(match(eq(polje,by)));
                    break;
                case "gt":
                    aggregation.add(match(gt(polje,by)));
                    break;
                case "gte":
                    aggregation.add(match(gte(polje,by)));
                    break;
                case "lt":
                    aggregation.add(match(lt(polje,by)));
                    break;
                case "lte":
                    aggregation.add(match(lte(polje,by)));
                    break;
            }
        }

    }

    private void projectionMapping(Clause clause){
        String[] args = clause.getArguments().toArray(new String[0]);
        String[] agregats = clause.getAggerations().toArray(new String[0]);
        polja = new ArrayList<>();

        if (!Arrays.asList(args).isEmpty()) {
            if (args[0].charAt(0) == '*') {
                aggregation.add(project(fields(include(), excludeId())));
            }else if(andor != 0){
                StringBuilder sb = new StringBuilder();
                int b = 0;
                polja = new ArrayList<>();

                for (String string : args) {
                    String splitovan = string.substring(0, string.indexOf(":"));
                    splitovan.replaceAll(" ", "");
                    polja.add(splitovan);
                    if (b == 0) {
                        sb.append(splitovan);
                        b++;
                    } else {
                        sb.append("," + splitovan);
                    }
                }
                aggregation.add(project(fields(include(polja.get(0),polja.get(1)))));
                aggregation.add(group(null,push(polja.get(0),"$"+polja.get(0)),push(polja.get(1),"$"+polja.get(1).trim())));

            }else{
                StringBuilder sb = new StringBuilder();
                int b = 0;
                polja = new ArrayList<>();
//        polja.add("employee_id");
//        polja.add("first_name");
                for (String string : args) {
                    String splitovan = string.substring(0, string.indexOf(":"));
                    splitovan.replaceAll(" ", "");
                    polja.add(splitovan);
                    if (b == 0) {
                        sb.append(splitovan);
                        b++;
                    } else {
                        sb.append("," + splitovan);
                    }
                }
                joinPolje = findForeign(polja);
                System.out.println(joinPolje);

//        System.out.println(polja);
                if (join == null) {
                    //aggregation.add(project(fields(include(polja))));
                    aggregation.add(project(fields(include(polja), excludeId())));
                } else {
                    StringBuilder expression = new StringBuilder();
                    expression.append("$" + join.substring(join.indexOf('.') + 1) + "." + joinPolje);
                    System.out.println(expression);
                    String exp = expression.toString();
                    polja.remove(joinPolje);
                    // aggregation.add(project(fields(computed(joinPolje,  exp), include(polja))));
                    aggregation.add(project(fields(computed(joinPolje, exp), include(polja), excludeId())));
                }
            }
        }

        if (!Arrays.asList(agregats).isEmpty()){
            String agregation = agregats[0];
            String type = agregation.substring(0,agregation.indexOf(":"));
            String polje = agregation.substring(agregation.indexOf(":") + 1,agregation.length());

            switch (type){
                case "$min":
                    aggregation.add(group(null, Accumulators.min("min" + polje, "$" + polje)));
                    aggregation.add(project(fields(include("min" + polje), excludeId())));
                    break;
                case "$count":
                    aggregation.add(group(null, Accumulators.sum("count" + polje, 1)));
                    aggregation.add(project(fields(include("count" + polje), excludeId())));
                    break;
                case "$max":
                    aggregation.add(group(null, Accumulators.max("max" + polje, "$" + polje)));
                    aggregation.add(project(fields(include("max" + polje), excludeId())));
                    break;
                case "$avg":
                    aggregation.add(group(null, Accumulators.avg("avg" + polje, "$" + polje)));
                    aggregation.add(project(fields(include("avg" + polje), excludeId())));
                    break;
                case "$sum":
                    aggregation.add(group(null, Accumulators.sum("sum" + polje, "$" + polje)));
                    aggregation.add(project(fields(include("sum" + polje), excludeId())));
                    break;
            }
        }
    }

    private void sortMapping(Clause clause){

            String[] split = (clause.getArguments().get(0)).split(":");
            String polje = split[0];
            Integer order = Integer.parseInt(split[1]);
            switch (order){
                case +1:
                    aggregation.add(sort(ascending(polje)));
                    break;
                case -1:
                    aggregation.add(sort(descending(polje)));
                    break;
            }

    }

    private void groupMapping(Clause clause){
        String[] polja = clause.getArguments().toArray(new String[0]);
        String[] agregations = clause.getAggerations().toArray(new String[0]);
        ArrayList<String> $polja = new ArrayList<>();
        for(String string : polja){
            $polja.add("$"+string);
        }
        String agregacija = agregations[0].replace(":","");

        $polja.add(agregacija);
        aggregation.add(group(null,first("avgsalary","$avgsalary"),addToSet("department_id","$department_id")));

    }
    private void nullMapping(Clause clause){
        String[] param = clause.getArguments().toArray(new String[0]);
        String[] args = param[0].split(":");
        String polje = args[0];
        String type = args[1];
        if (type.equals("$ne")){
            aggregation.add(not(eq(polje,null)));
        }else aggregation.add(exists(polje,false));
    }

    private void andMapping(Clause clause){
        String[] args = clause.getArguments().toArray(new String[0]);
        String levo = args[0];
        String desno = args[1];


        String znakLevo = znak(levo);
        String levo1 = levo.substring(0,levo.indexOf(znakLevo)).trim();
        String desno1 = levo.substring(levo.indexOf(znakLevo)+1).trim();
        int desno1Int = Integer.parseInt(desno1);

        System.out.println(levo1 + znakLevo + desno1);

        String znakDesno = znak(desno);
        String levo2 = desno.substring(0,desno.indexOf(znakDesno)).trim();
        String desno2 = desno.substring(desno.indexOf(znakDesno)+1).trim();
        int desno2Int = Integer.parseInt(desno2);

        aggregation.add(match(and(logicalMatch(desno1Int,levo1,znakLevo),logicalMatch(desno2Int,levo2,znakDesno))));
    }

    private void orMapping(Clause clause){
        String[] args = clause.getArguments().toArray(new String[0]);
        String levo = args[0];
        String desno = args[1];

        String znakLevo = znak(levo);
        String levo1 = levo.substring(0,levo.indexOf(znakLevo)).trim();
        String desno1 = levo.substring(levo.indexOf(znakLevo)+1).trim();
        int desno1Int = Integer.parseInt(desno1);

        String znakDesno = znak(desno);
        String levo2 = desno.substring(0,desno.indexOf(znakDesno)).trim();
        String desno2 = desno.substring(desno.indexOf(znakDesno)+1).trim();
        int desno2Int = Integer.parseInt(desno2);

        aggregation.add(match(or(logicalMatch(desno1Int,levo1,znakLevo),logicalMatch(desno2Int,levo2,znakDesno))));

    }

    private void inMapping(Clause clause){
        String[] args = clause.getArguments().toArray(new String[0]);

        String[] parametri = args[0].split(":");
        String polje = parametri[0];
        List<Integer> brojevi = Arrays.stream(parametri[1].split(",")).map(Integer::parseInt).collect(Collectors.toList());

        aggregation.add(match(in(polje, brojevi)));
    }

    private String znak(String string){
        Pattern pattern = Pattern.compile(">=|<=|>|<|=");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()){
            return matcher.group();
        }
        return null;
    }

    private <TItem>Bson logicalMatch(Integer by, String field, String type){

        switch (type){
            case "=":
                return eq(field,by);
            case ">":
                return gt(field,by);
            case ">=":
                return gte(field,by);
            case "<":
                return lt(field,by);
            case "<=":
                return lte(field,by);
        }
        return null;
    }

    private void joinMapping(Clause clause){
        System.out.println("sad tu");
        System.out.println(clause.getArguments());
        join = "department_name";
//        AggregateIterable<Document> doc = lista.aggregate(asList(
//                lookup("departments","department_id", "department_id","departments"),
//                unwind("$departments"),
//                project(fields(
//                        computed("department_name",  "$departments.department_name"),
//                        include("employee_id")))
//        ));

//        aggregation.add(lookup(clause.getArguments().get(0),"department_id","department_id","departments"));
//        aggregation.add(unwind("$"+clause.getArguments().get(0)));
//        aggregation.add(project(fields(computed("department_name",  "$departments.department_name"))));

//        aggregation.add(lookup(clause.getArguments().get(0),clause.getArguments().get(1),clause.getArguments().get(2),clause.getArguments().get(3)));
        //lookup("from","local","foreign","ime");
    }

    private String returnDBName(SQLQuery query){
        String name = "";
        for (Clause clause : query.getClauses()){
            if (clause.getClauseName().contains("database")){
                name = clause.getArguments().get(0);
            }
        }

        if (name.contains(".")){
            String[] split = name.split("\\.");
            name = split[1];
        }

        return name;
    }

    private String findForeign(ArrayList<String> polja){
        String join = "";
        for (String string : polja){
            if (!poljaTabele.contains(string)){
                join = string;
            }
        }
        return join;
    }

    public String getTabela() {
        return tabela;
    }
}
