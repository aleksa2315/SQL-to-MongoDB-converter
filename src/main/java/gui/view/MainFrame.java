package gui.view;

import com.mongodb.client.MongoCursor;
import com.sun.tools.javac.Main;
import controller.ActionManager;
import controller.ExitAction;
import controller.RunQuery;
import core.AppCore;
import org.bson.Document;

import javax.print.Doc;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.*;

import java.awt.*;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private AppCore appCore;
    private static MainFrame instance;
    private JToolBar toolBar;
    private JPanel desktop;
    private JScrollPane scrollPane;
    private JTable jTable;
    private ActionManager actionManager;


    private JTextPane textPane;

    public void initGui() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;

        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolBar = new Toolbar();
        add(toolBar, BorderLayout.NORTH);

        this.desktop = new JPanel();
        this.desktop.setLayout(new BorderLayout());

        jTable = new JTable();
        jTable.setFillsViewportHeight(true);
        jTable.setEnabled(false);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(200, 200));
        JButton dugme = new JButton("Run Query");
        dugme.addActionListener(new RunQuery());
        dugme.setSize(100, 50);
        desktop.add(textPane, BorderLayout.CENTER);
        desktop.add(dugme, BorderLayout.SOUTH);

        JSplitPane tabelaSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, desktop, new JScrollPane(jTable));

        JSplitPane glavniSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, tabelaSplit);

        this.add(tabelaSplit, BorderLayout.CENTER);
        this.pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public MainFrame() {
        this.addWindowListener(new ExitAction(this));
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
            instance.initialise();
        }
        return instance;
    }


    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
    }

    public void setTable() {
        this.jTable.setModel(appCore.getTableModel());
    }

    public AppCore getAppCore() {
        return appCore;
    }


    public void initialise() {
        actionManager = new ActionManager();
    }

    public static void setInstance(MainFrame instance) {
        MainFrame.instance = instance;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public JPanel getDesktop() {
        return desktop;
    }

    public void setDesktop(JPanel desktop) {
        this.desktop = desktop;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public JTable getjTable() {
        return jTable;
    }

    public void setjTable(JTable jTable) {
        this.jTable = jTable;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setTextPane(String text) {
        DefaultStyledDocument document = new DefaultStyledDocument();
        textPane.setDocument(document);
        StyleContext context = new StyleContext();
        Style style = context.addStyle("blue", null);
        StyleConstants.setForeground(style, Color.BLUE);
        Style style0 = context.addStyle("blue", null);
        StyleConstants.setForeground(style0, Color.BLACK);

        try {
            int z = text.length();
            for (int y = 0; y < z; y++) {
                if (Character.isUpperCase(text.charAt(y))) {
                    document.insertString(y, String.valueOf(text.charAt(y)), style);

                } else {
                    document.insertString(y, String.valueOf(text.charAt(y)), style0);
                }
            }
            document.insertString(z, " ", style0);

        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

//    public static TableModel setJTable(MongoCursor<Document> cursor){
//        DefaultTableModel model = new DefaultTableModel();
//        while (cursor.hasNext()) {
//            Document document = cursor.next();
//
//            for (String key : document.keySet()) {
//                if (!(Arrays.asList(getColumnNames(model)).contains(key))) {
//                    model.addColumn(key);
//                }
//            }
//
//            Object[] row = new Object[model.getColumnCount()];
//
//            // Iterate over columns
//            for (int i = 0; i < model.getColumnCount(); i++) {
//                String columnName = model.getColumnName(i);
//                Object value = document.get(columnName);
//                row[i] = value;
//            }
//
//            model.addRow(row);
//
//        }
//        MainFrame.getInstance().getAppCore().setTableModel(model);
//        MainFrame.getInstance().setTable();
//        return model;
//    }
}
