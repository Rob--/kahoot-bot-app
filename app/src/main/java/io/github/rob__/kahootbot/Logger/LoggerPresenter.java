package io.github.rob__.kahootbot.Logger;

public class LoggerPresenter {

    private LoggerView view;

    public LoggerPresenter(LoggerView view) {
        this.view = view;
    }

    public void addLog(String message) {
        view.addLog(message);
    }
}
