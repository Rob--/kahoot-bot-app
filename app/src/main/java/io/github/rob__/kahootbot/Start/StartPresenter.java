package io.github.rob__.kahootbot.Start;

import android.content.Context;

import io.github.rob__.kahootbot.Kahoot;

public class StartPresenter {

    private StartView view;

    public StartPresenter(StartView view) {
        this.view = view;
    }

    public void startFlood(String gameCode) {
        for (int i = 0; i < 20; i++) {
            Kahoot kahoot = new Kahoot("thisisatest" + String.valueOf(Math.floor(Math.random() * 100)), gameCode);
            kahoot.join();
        }
    }

    public void startFloodClicked() {
        view.startFloodClicked();
    }

    public void stopFloodClicked() {
        view.stopFloodClicked();
    }

}
