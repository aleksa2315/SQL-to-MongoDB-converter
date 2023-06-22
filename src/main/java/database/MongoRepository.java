package database;

import com.mongodb.AggregationOptions;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import database.settings.Settings;
import gui.view.MainFrame;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;


import javax.print.Doc;
import java.sql.*;
import java.util.*;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static java.util.Arrays.asList;

public class MongoRepository implements Repository {
    private Settings settings;
    private MongoClient connection;
    private DatabaseMetaData metaData;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private ArrayList<String> listaTabela = new ArrayList<>();

    public MongoRepository(Settings settings) {
        this.settings = settings;
    }

    private void initConnection(){
        String user = "writer"; // the user name
        String db = "bp_tim6"; // the name of the database in which the user is defined
        String password = "V48nMGJx1mwcRIXB";

        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        connection = new MongoClient(new ServerAddress("134.209.239.154", 27017), asList(credential));
        database = connection.getDatabase("bp_tim6");
    }

    private void closeConnection(){
        connection.close();
        connection = null;
    }


    public void mongo(){
            this.initConnection();

            MongoCursor<Document> cursor = database.getCollection("regions").find().iterator();
//            while (cursor.hasNext()){
//                Document d = cursor.next();
//                MainFrame.setJTable(cursor);
//                System.out.println(d.toJson());
//            }

            MainFrame.getInstance().getAppCore().getPackager().displayTable(cursor);

            closeConnection();


    }

    public void findFunction(List<Bson> aggregationPipeline, String tabela){
        this.initConnection();

        MongoCollection<Document> lista= database.getCollection(tabela);
        MongoCollection<Document> join = database.getCollection("departments");
        String brojevi = "60,70,80";
        String[] list = brojevi.split(",");
        ArrayList<Bson> ag = new ArrayList<>();
        ag.add(match(and(gt("department_id",50),lt("department_id",80))));
        ag.add(project(fields(include("employee_id","department_id"))));
        ag.add(group(null,push("employee_id","$employee_id"),push("department_id","$department_id")));

//        AggregateIterable<Document> doc = lista.aggregate(Arrays.asList(
//                project(fields(include("employee_id","department_id")))
//        ));

        AggregateIterable<Document> doc = lista.aggregate(aggregationPipeline);
        MainFrame.getInstance().getAppCore().getPackager().displayTable(doc.cursor());

        this.closeConnection();
    }

    public void joinFunction(List<Bson> aggregationPipeline, String tabela, String joinTabela){
        this.initConnection();

        MongoCollection<Document> lista= database.getCollection(tabela);
        MongoCollection<Document> join = database.getCollection(tabela);


//        a.add(project(fields(include("employee_id"))));
//        AggregateIterable<Document> doc = lista.aggregate(asList(
//                lookup("departments","department_id", "department_id","departments"),
//                unwind("$departments"),
//                project(fields(
//                        computed("department_name",  "$departments.department_name"),
//                        include("employee_id")))
//        ));
        AggregateIterable<Document> doc = lista.aggregate(aggregationPipeline);
        MainFrame.getInstance().getAppCore().getPackager().displayTable(doc.cursor());

        this.closeConnection();
    }

    public ArrayList<String> getColumns(String tabela){
        this.initConnection();
        MongoCollection<Document> join = database.getCollection(tabela);
        Document document = join.find().first();
        ArrayList<String> kolone = getColumnsFromTable(document);
        this.closeConnection();
        return kolone;
    }
    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public ArrayList<String> getListaTabela(){
        return listaTabela;
    }

    public void setListaTabela(ArrayList<String> listaTabela) {
        this.listaTabela = listaTabela;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(DatabaseMetaData metaData) {
        this.metaData = metaData;
    }

    private ArrayList<String> getColumnsFromTable(Document document){
        ArrayList<String> lista = new ArrayList<>();

        for (String key : document.keySet()){
            lista.add(key);
        }
        return lista;
    }

}
