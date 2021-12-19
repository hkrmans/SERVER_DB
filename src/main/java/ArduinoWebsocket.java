import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;

public class ArduinoWebsocket {
    private static final int CONNECTION_IDLE_TIMEOUT = 0;
    private static ArrayList<String> responseList;
    private static Gson gson;
    private static WsContext connection;

    private static ArduinoWebsocket aw;

    public static ArduinoWebsocket getInstance(){
        if(aw == null){
            aw = new ArduinoWebsocket();
        }
        return aw;
    }

    public void run(){
        gson = new Gson();

        Javalin app = Javalin.create(config  -> {
            config.wsFactoryConfig( wsFactory -> {
                wsFactory.getPolicy().setIdleTimeout(CONNECTION_IDLE_TIMEOUT);
            });
        }).start(1338);

        app.ws("/arduino",ws-> {
            ws.onConnect(ctx -> {
                connection =ctx;
                System.out.println("Connected");

            });

            ws.onMessage(ctx -> {
                responseList = (ArrayList<String>) gson.fromJson(ctx.message(),ArrayList.class);
                WebSocketServer wss = WebSocketServer.getInstance();
                wss.deviceUpdate(responseList);
            });
        });

    }

    public void send(ArrayList<String> info){
        String text = gson.toJson(info);
        connection.send(text);
    }
}
