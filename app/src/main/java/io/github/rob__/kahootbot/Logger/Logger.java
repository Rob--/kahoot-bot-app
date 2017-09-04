package io.github.rob__.kahootbot.Logger;

public class Logger {

    private static String logs = "";

    public static void addLog(String message) {
        logs += message + "\n";
    }

    public static String getLogs() {
        return logs;
    }

}
