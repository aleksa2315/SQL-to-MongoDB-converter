package gui.view.packager;

import com.mongodb.Mongo;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import gui.view.MainFrame;
import org.bson.Document;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Arrays;

public class PackagerImpl implements Packager{
    private MongoCursor<Document> cursor;

    @Override
    public void displayTable(MongoCursor<Document> cursor) {
        setJTable(createModel(cursor));
    }

    public DefaultTableModel createModel(MongoCursor<Document> cursor){
        DefaultTableModel model = new DefaultTableModel();

        while (cursor.hasNext()) {
            Document document = cursor.next();

            for (String key : document.keySet()) {
                if (!(Arrays.asList(getColumnNames(model)).contains(key))) {
                    model.addColumn(key);
                }
            }

            Object[] row = new Object[model.getColumnCount()];

            for (int i = 0; i < model.getColumnCount(); i++) {
                String columnName = model.getColumnName(i);
                Object value = document.get(columnName);
                row[i] = value;
            }

            model.addRow(row);

        }
        return model;
    }
    public static TableModel setJTable(DefaultTableModel model){
        MainFrame.getInstance().getAppCore().setTableModel(model);
        MainFrame.getInstance().setTable();
        return model;
    }

    public static String[] getColumnNames(TableModel tableModel) {
        int size = tableModel.getColumnCount();
        String[] columnNames = new String[size];
        for (int i = 0; i < size; i++) {
            columnNames[i] = tableModel.getColumnName(i);
        }
        return columnNames;
    }

    public MongoCursor<Document> getCursor() {
        return cursor;
    }

    public void setCursor(MongoCursor<Document> cursor) {
        this.cursor = cursor;
    }


}
