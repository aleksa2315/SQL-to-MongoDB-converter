package database;

import database.MongoDB.SQLQuery;
import org.bson.Document;
import org.bson.conversions.Bson;
import resource.DBNode;
import resource.data.Row;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImpl implements Database{

    private Repository repository;

    public DatabaseImpl(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void mongo(){
        this.repository.mongo();
    }

    public void findFunction(List<Bson> aggregationPipeline, String tabela){
        this.repository.findFunction(aggregationPipeline,tabela);
    }
    public void joinFunction(List<Bson> aggregationPipeline, String tabela, String joinTabela){
        this.repository.joinFunction(aggregationPipeline,tabela,joinTabela);
    }

    public ArrayList<String> getColumns(String tabela){
        return this.repository.getColumns(tabela);

    }
}
