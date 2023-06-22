package core;

import adapter.Adapter;
import adapter.AdapterImpl;
import database.*;
import database.executor.Executor;
import database.executor.ExecutorImpl;
import gui.view.packager.Packager;
import gui.view.packager.PackagerImpl;
import database.settings.Settings;
import database.settings.SettingsImpl;
import gui.view.MainFrame;
import gui.view.MyApp;
import parser.SQLParser;
import parser.SQLParserImpl;
import resource.data.Row;
import utils.Constants;
import validator.SQLValidator;
import validator.SQLValidatorImpl;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

public class AppCore extends AppFramework {
    private Database database;
    private Settings settings;
    private DefaultTableModel tableModel;
    private DefaultTreeModel defaultTreeModel;
    private static AppCore instance;

    private SQLParser sqlParser;

    private SQLValidator sqlValidator;
    private Adapter adapter;
    private Packager packager;
    private Executor executor;

    private List<Row> rowList = new ArrayList<>();

    public List<Row> getRowList() {
        return rowList;
    }

    private AppCore(){
        this.settings = initSettings();
        this.database = new DatabaseImpl(new MongoRepository(this.settings));
        this.sqlParser = new SQLParserImpl();
        this.adapter = new AdapterImpl();
        this.packager = new PackagerImpl();
        this.executor = new ExecutorImpl();
        this.sqlValidator = new SQLValidatorImpl();
    }

    @Override
    public void run() {
        this.gui.start();
    }

    public static AppCore getInstance() {
        if (instance == null){
            instance = new AppCore();
        }
        return instance;
    }

    public static void main(String[] args) {
        Gui gui = new MyApp();
        AppCore appCore = AppCore.getInstance();
        appCore.init(gui);
        MainFrame.getInstance().setAppCore(appCore);
        appCore.run();

    }


    private Settings initSettings() {
        Settings settingsImplementation = new SettingsImpl();
        settingsImplementation.addParameter("mysql_ip", Constants.MYSQL_IP);
        settingsImplementation.addParameter("mysql_database", Constants.MYSQL_DATABASE);
        settingsImplementation.addParameter("mysql_username", Constants.MYSQL_USERNAME);
        settingsImplementation.addParameter("mysql_password", Constants.MYSQL_PASSWORD);
        return settingsImplementation;
    }
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public DefaultTreeModel getDefaultTreeModel() {
        return defaultTreeModel;
    }

    public void setDefaultTreeModel(DefaultTreeModel defaultTreeModel) {
        this.defaultTreeModel = defaultTreeModel;
    }


    public static void setInstance(AppCore instance) {
        AppCore.instance = instance;
    }

    public SQLParser getSqlParser() {
        return sqlParser;
    }

    public void setSqlParser(SQLParser sqlParser) {
        this.sqlParser = sqlParser;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public Packager getPackager() {
        return packager;
    }

    public void setPackager(Packager packager) {
        this.packager = packager;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public SQLValidator getSqlValidator() {
        return sqlValidator;
    }

    public void setSqlValidator(SQLValidator sqlValidator) {
        this.sqlValidator = sqlValidator;
    }
}
