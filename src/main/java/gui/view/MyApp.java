package gui.view;

import core.Gui;

public class MyApp implements Gui {
    private MainFrame instance;

    public MyApp() {
    }

    @Override
    public void start() {
        instance = MainFrame.getInstance();
        instance.initGui();
        instance.setVisible(true);
    }
}
