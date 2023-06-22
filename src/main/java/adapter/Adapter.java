package adapter;

import database.MongoDB.SQLQuery;
import org.bson.conversions.Bson;

import java.util.LinkedHashMap;
import java.util.List;

public interface Adapter {
    SQLQuery convertParameters();
    List<Bson> mapQuery(SQLQuery mongoQuery);
    SQLQuery adaptQuery(SQLQuery sqlQuery);
    List<Bson> getAggregationPipline();
}
