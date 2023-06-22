package validator;

import database.MongoDB.SQLQuery;

public interface SQLValidator {
     boolean validate(SQLQuery sqlQuery);
}
