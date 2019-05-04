package assignment2;

import assignment2.view.MainView;
import assignment2.view.SelectorListener;

import java.util.List;

public abstract class BaseController implements SelectorListener {

    protected Cron crono = new Cron();
    protected MainView view;

    public BaseController(MainView view) {
        this.view = view;
        this.view.setListener(this);
    }

    @Override
    public void startPressed(List<String> paths) {
        crono.start();
        view.clearComputationTime();
    }

    @Override
    public void stopPressed() {
        crono.stop();
        view.setComputationTime(crono.getTime());
    }

}
