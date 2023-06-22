package database.MongoDB;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLQuery {

    private Map<String, String> sqlQuery;
    private List<Clause> clauses;

    public SQLQuery(Map<String, String> sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public void setSqlQuery(Map<String, String> sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public void setClauses(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public Map<String, String> getSqlQuery() {
        return sqlQuery;
    }

    public SQLQuery() {
        clauses = new ArrayList<>();
    }


    public List<Clause> getClauses() {
        return clauses;
    }
    public boolean containsascdesc(){
        for(Clause clause : this.clauses){
            if(clause.getClauseName().equals("asc") ||clause.getClauseName().equals("desc"))
                return true;
        }
        return false;
    }
}
