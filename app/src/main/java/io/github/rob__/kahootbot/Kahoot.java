package io.github.rob__.kahootbot;

import android.text.TextUtils;
import android.util.Base64;

import com.squareup.duktape.Duktape;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;

import java.util.HashMap;

import io.github.rob__.kahootbot.API.KahootAPI;
import io.github.rob__.kahootbot.API.KahootCallback;
import io.github.rob__.kahootbot.API.KahootResponse;
import io.github.rob__.kahootbot.Logger.Logger;
import retrofit2.Response;

public class Kahoot {

    // Following interface and method is to run and decode the challenge,
    // we define `console.log` to return the offset and log it via the logger.
    interface console {
        void log(Object ...message);
    }

    static private console c = new console() {
        @Override
        public void log(Object ...message) {
            Logger.addLog(TextUtils.join(" ", message));
        }
    };

    private String username;
    private String gameCode;
    private KahootAPI api;

    public Kahoot(String username, String gameCode) {
        this.username = username;
        this.gameCode = gameCode;
        this.api = new KahootAPI();
    }

    public void join() {
        reserveSession();
    }

    public static void gameExists(final String gameCode, final CustomListeners.GameListener listener) {
        KahootAPI api = new KahootAPI();
        api.reserveSession(gameCode, new KahootCallback() {
            @Override
            public void onResponse(Response<KahootResponse> response) {
                listener.gameExists(response.code() != 404);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void reserveSession() {
        api.reserveSession(this.gameCode, new KahootCallback() {
            @Override
            public void onResponse(Response<KahootResponse> response) {
                if (response.code() == 404) {
                    Logger.addLog("Game with code #" + gameCode + " no longer exists, unable to join.");
                    return;
                }

                if (response.code() != 200) {
                    Logger.addLog("An error occured trying to join game with code #" + gameCode);
                    return;
                }

                String decoded = Kahoot.runChallenge(response.body().getChallenge());
                String token = response.headers().get("x-kahoot-session-token");

                if (token.isEmpty()) {
                    Logger.addLog("Unable to reserve session, no session token received.");
                    return;
                }

                String cometToken = Kahoot.getToken(token, decoded);
                setupComet(cometToken);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setupComet(String cometToken) {
        String url = "https://kahoot.it/cometd/" + this.gameCode + "/" + cometToken;

        final BayeuxClient client = new BayeuxClient(url, LongPollingTransport.create(null));

        client.handshake(new ClientSessionChannel.MessageListener() {
            @Override
            public void onMessage(ClientSessionChannel channel, Message message) {
                Logger.addLog(channel.toString() + ": " + message.toString());
                if (message.isSuccessful()) {
                    Logger.addLog("Successful handshake, attempting to join game...");
                    client.getChannel("/service/controller").publish(getLoginData());
                } else {
                    Logger.addLog("Unsuccessful handshake, unable to join game.");
                }
            }
        });
    }

    private HashMap<String, Object> getLoginData() {
        HashMap<String, Object> data = new HashMap();
        data.put("type", "login");
        data.put("gameid", this.gameCode);
        data.put("host", "kahoot.it");
        data.put("name", this.username);

        return data;
    }

    private static String runChallenge(String challenge) {
        Duktape duktape = Duktape.create();

        String angularFunctions = ""
                + "angular = {"
                    + "isArray: function(){ return true; },"
                    + "isDate: function(){ return true; },"
                    + "isObject: function(){ return true; },"
                    + "isString: function(){ return true; },"
                + "};";

        String lodashFunction = ""
            + "_ = {"
                + "replace: function() {"
                    + "var string = \"\" + (arguments.length <= 0 ? undefined : arguments[0]);"
                    + "return arguments.length < 3 ? string : string.replace(arguments.length <= 1 ? undefined : arguments[1], arguments.length <= 2 ? undefined : arguments[2]);"
                + "}"
            + "};";

//        String consoleFunction = "";
//                + "console = {"
//                    + "log: function() {} "
//                + "};";

        duktape.set("console", console.class, c);

        String answer = duktape.evaluate(angularFunctions + lodashFunction + challenge).toString();
        duktape.close();

        Logger.addLog("Challenge decoded: " + answer);
        return answer;
    }

    private static String getToken(String sessionToken, String decodedChallenge) {
        byte[] decodedToken = Base64.decode(sessionToken, Base64.DEFAULT);
        byte[] challenge = decodedChallenge.getBytes();

        for (int i = 0; i < decodedToken.length; i++) {
            decodedToken[i] ^= challenge[i % challenge.length];
        }

        String token = new String(decodedToken);
        Logger.addLog("Decoded token: " + token);
        return token;

//        String decodedToken = new String(Base64.decode(sessionToken, Base64.DEFAULT));
//        Duktape duktape = Duktape.create();
//        String decodedToken = duktape.evaluate("atob('" + sessionToken + "')").toString();
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.DEFAULT)));
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.CRLF)));
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.NO_CLOSE)));
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.NO_PADDING)));
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.NO_WRAP)));
//        Log.d("Kahoot", "decodedToken: " + new String(Base64.decode(sessionToken, Base64.URL_SAFE)));
//
//
//        String token = "";
//        for (int i = 0; i < decodedToken.length(); i++) {
//            int e = Character.getNumericValue(decodedToken.charAt(i));
//            int d = Character.getNumericValue(decodedChallenge.charAt(i % decodedChallenge.length()));
//            token += Character.toString((char) (e ^ d));
//        }
//
//        Log.d("Kahoot", "Decoded cometd token: " + token);
//        return token;
    }

}
