package io.github.rob__.kahootbot;

public class CustomListeners {

    public interface KahootListeners {
        void floodStarted();
        void floodStopped();
    }

    public interface LoggerListener {
        void addLog(String message);
    }

    public interface GameListener {
        void gameExists(boolean exists);
    }

}
