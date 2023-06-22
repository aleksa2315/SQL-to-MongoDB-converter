package database.executor;

import com.mongodb.client.MongoCursor;
import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import gui.view.MainFrame;
import org.bson.Document;

import java.util.List;

public class ExecutorImpl implements Executor{

    private MongoCursor<Document> cursor;

    public void executeQuery(SQLQuery mongoQuery, List aggregationPipline){
//        System.out.println("tu sam");
//        for(Clause clause : mongoQuery.getClauses()){
//            System.out.print("Position: " + clause.getPositionInQuery() +" " + clause.getClauseName() +clause.getClauseNumber() + " Parameters: " + clause.getArguments() +"Aggregations: " + clause.getAggerations());
//            System.out.println();
//        }

        String jointabela = null;
        for (Clause clause : mongoQuery.getClauses()){
            if (clause.getClauseName().contains("lookup")){
                jointabela = clause.getArguments().get(0);
                if (jointabela.contains(".")){
                    jointabela = jointabela.substring(jointabela.indexOf(".")+1);
                }
            }
        }

//        System.out.println(jointabela);
        String tabela = returnDBName(mongoQuery);
//        System.out.println(tabela);

        if (jointabela == null) {
            MainFrame.getInstance().getAppCore().getDatabase().findFunction(aggregationPipline, tabela);
        }else {
            MainFrame.getInstance().getAppCore().getDatabase().joinFunction(aggregationPipline,tabela,jointabela);
        }
    }

    public String returnDBName(SQLQuery query){
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

    public MongoCursor<Document> getCursor() {
        return cursor;
    }

    public void setCursor(MongoCursor<Document> cursor) {
        this.cursor = cursor;
    }
}
