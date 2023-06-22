package database;

import database.MongoDB.SQLQuery;
import org.bson.Document;
import org.bson.conversions.Bson;
import resource.DBNode;
import resource.data.Row;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Repository {
//    DBNode getSchema();
//    DatabaseMetaData getMetaData();
//
//    List<Row> get(String from);
//
//    List<Row> fetchDataFromDatabase(String sql);
//
//    void runCsvCode(List<String[]> sql, String selektovanaTabela);
//
//    boolean checkColumns(String tableName, List<String[]> csvColumns);
    void mongo();
    void findFunction(List<Bson> aggregationPipeline, String tabela);
    void joinFunction(List<Bson> aggregationPipeline, String tabela, String joinTabela);
    ArrayList<String> getColumns(String tabela);
}
