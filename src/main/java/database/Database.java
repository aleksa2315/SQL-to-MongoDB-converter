package database;

import database.MongoDB.SQLQuery;
import org.bson.Document;
import org.bson.conversions.Bson;
import resource.DBNode;
import resource.data.Row;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Database {

    void mongo();
    void findFunction(List<Bson> aggregationPipeline, String tabela);
    void joinFunction(List<Bson> aggregationPipeline, String tabela, String joinTabela);
    ArrayList<String> getColumns(String tabela);
}
