package gui.view.packager;

import com.mongodb.Mongo;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

public interface Packager {
    void displayTable(MongoCursor<Document> cursor);
}
