package adapter;

import database.MongoDB.Clause;
import database.MongoDB.SQLQuery;
import gui.view.MainFrame;
import org.bson.conversions.Bson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdapterImpl implements Adapter{

    private SQLQuery sqlQuery;
    private LinkedHashMap<String,String> sqlHashMap;
    private SQLQuery mongoQuery;
    ParameterConverter parameterConverter;
    private Mapper mapper;
    private List<Bson> aggregationPipeline;

    public SQLQuery adaptQuery(SQLQuery sqlQuery){
        this.sqlQuery = sqlQuery;
        mongoQuery = convertParameters();
//        for(Clause clause : mongoQuery.getClauses()){
//            System.out.print("Position: " + clause.getPositionInQuery() +" " + clause.getClauseName() +clause.getClauseNumber() + " Parameters: " + clause.getArguments() +"Aggregations: " + clause.getAggerations());
//            System.out.println();
//        }
        aggregationPipeline = mapQuery(mongoQuery);
        return mongoQuery;
    }

    @Override
    public SQLQuery convertParameters() {
        parameterConverter = new ParameterConverter(sqlQuery);
        return parameterConverter.convertParameters();
    }

    @Override
    public List<Bson> mapQuery(SQLQuery mongoQuery) {
        mapper = new Mapper(mongoQuery);
        return mapper.doAggregation();
    }

    public List<Bson> getAggregationPipline() {
        return aggregationPipeline;
    }
}
