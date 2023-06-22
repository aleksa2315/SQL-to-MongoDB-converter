package database.executor;

import database.MongoDB.SQLQuery;

import java.util.List;

public interface Executor {
    void executeQuery(SQLQuery mongoQuery, List aggregationPipeline);
}
