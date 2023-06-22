package parser;

import database.MongoDB.SQLQuery;

import java.util.HashMap;
import java.util.Map;

public interface SQLParser {
    SQLQuery parseSQL(String string);

}
