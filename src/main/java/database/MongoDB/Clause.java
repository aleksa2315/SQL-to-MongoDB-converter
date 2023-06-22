package database.MongoDB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Clause {
    String clauseName;
    List<String> arguments;
    int clauseNumber;
    List<String> aggerations;
    int positionInQuery;

    public Clause(String clause,int positionInQuery) {
        this.clauseName = clause;
        arguments = new ArrayList<>();
        aggerations = new ArrayList<>();
        this.positionInQuery =positionInQuery;
    }
    public void checkIfExists(List<Clause> clauses){
        Clause last = null;
        for(Clause currClause : clauses){
            if(currClause.clauseName.equals(clauseName) && currClause != this){
                last = currClause;
            }
        }if(last != null)
            this.clauseNumber = last.getClauseNumber() + 1;
           else
        clauseNumber = 0;
    }

    public String getClauseName() {
        return clauseName;
    }

    public void setClauseName(String clauseName) {
        this.clauseName = clauseName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public int getClauseNumber() {
        return clauseNumber;
    }

    public void setClauseNumber(int clauseNumber) {
        this.clauseNumber = clauseNumber;
    }


    public List<String> getAggerations() {
        return aggerations;
    }

    public void setAggerations(List<String> aggerations) {
        this.aggerations = aggerations;
    }

    public int getPositionInQuery() {
        return positionInQuery;
    }

    public void setPositionInQuery(int positionInQuery) {
        this.positionInQuery = positionInQuery;
    }
}
