package utils;

import java.util.Arrays;

public enum SQLKeyWords {
    SELECT, FROM, WHERE, GROUPBY, ORDERBY, JOIN, ISNULL, IN, LIKE;

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
