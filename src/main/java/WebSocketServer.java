import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServer {
    DBHandler dbHandler = new DBHandler();

    private static final int OPCODE_STATUS_DEVICE = 10;
    private static final int OPCODE_STATUS_ALL_DEVICES = 11;
    private static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    private static final int OPCODE_CHANGE_DEVICE_HOUSEHOLD = 21;
    private static final int OPCODE_CHANGE_DEVICE_NOTOK = 40;

    private DevicesServer ds;

    private static final int CONNECTION_IDLE_TIMEOUT = 3600000;


    private static Map<WsContext, User> userUsernameMap = new ConcurrentHashMap<>();

    private static Gson gson;


    static User demoUser;

    public WebSocketServer(DevicesServer ds) {
        this.ds = ds;
    }

    public void run() {
        gson = new Gson();

        demoUser = new User(0, "user@mail.se", "Namn Namn", 1, "123"); //token to be


        Javalin app = Javalin.create(config -> {
            config.wsFactoryConfig(wsFactory -> {
                wsFactory.getPolicy().setIdleTimeout(CONNECTION_IDLE_TIMEOUT);
            });
        }).start(1337);

        app.ws("/house", ws -> {
            ws.onConnect(ctx -> {
                //Token received from URL (example: ws://localhost:7071/house?token=123)
                String token = ctx.queryParam("token");

                //TODO: get user from database, store as "user"
                //Using demo user for now
                User user = demoUser;

                //On new connection, check if valid token
                if (token == null || !token.matches(demoUser.getToken())) {
                    System.out.println("incorrect token, closing");
                    ctx.session.close();
                    return;
                }

                //Store connection in map
                userUsernameMap.put(ctx, demoUser);
                System.out.println(demoUser.getUsername() + " connected");

                //Send all device status to new connection
                sendAllDevice(ctx);
            });
            ws.onClose(ctx -> {
                User disconnectedUser = userUsernameMap.remove(ctx);
                if (disconnectedUser != null) {
                    System.out.println(disconnectedUser.getUsername() + " disconnected");
                }
                System.out.println("Current number of connected users: " + userUsernameMap.size());
            });
            ws.onMessage(ctx -> {
                System.out.println("Received message");

                //Message arrives as JSON
                JSONObject msgFromClient = new JSONObject(ctx.message());

                //Get opcode to determine message action
                int opcode = msgFromClient.getInt("opcode");
                System.out.println(" - Message Opcode: " + opcode);
                switch (opcode) {
                    case OPCODE_STATUS_ALL_DEVICES: // get status on all devices
                        break;

                    case OPCODE_CHANGE_DEVICE_STATUS:
                        System.out.println(" - Change device status");
                        changeDeviceStatus(ctx, msgFromClient);
                        break;
                    case OPCODE_CHANGE_DEVICE_HOUSEHOLD: // change household id to be able to change devices on different household
                        break;
                }
            });
        });
    }

    public void changeDeviceStatus(WsContext senderCtx, JSONObject msgFromClient) {

        ArrayList<String> commands = new ArrayList<>();
        Device device = gson.fromJson(String.valueOf(msgFromClient.get("data")), Device.class); //json array kolla på sendAll love jonte !!!

        DeviceDB deviceDB = dbHandler.getDeviceDB(device.getDeviceId());

        if (device.getValue() == 1) {
            commands.add(deviceDB.getCommandOn());
        } else {
            commands.add(deviceDB.getCommandOff());
        }

        ArrayList<String> commandResponses = new ArrayList<>();

        try {
            commandResponses = ds.getStatus(commands);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (commandResponses .get(0).equalsIgnoreCase("ok")) {
            deviceDB.setValue(device.getValue());
            System.out.println(deviceDB.getValue());
            dbHandler.updateDeviceValue(deviceDB);
            userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
                session.send(
                        new JSONObject()
                                .put("opcode", OPCODE_STATUS_DEVICE)
                                .put("data", gson.toJson(device))
                                .toString()
                );
            });
        } else{
            senderCtx.send(
                    new JSONObject()
                            .put("opcode", OPCODE_CHANGE_DEVICE_NOTOK)
                            .put("data", gson.toJson(device))
                            .toString()
            );
        }
    }

    private void sendAllDevice(WsContext ctx) {
        int householdId = userUsernameMap.get(ctx).getHouseholdId();

        JSONArray jsonArray = new JSONArray();

        ArrayList<Device> devices = dbHandler.householdDevices(householdId);

        for (Device d : devices) {
            jsonArray.put(gson.toJson(d));
        }

        //Send all devices to one connection
        ctx.send(
                new JSONObject()
                        .put("opcode", OPCODE_STATUS_ALL_DEVICES)
                        .put("data", jsonArray)
                        .toString()
        );
    }
}