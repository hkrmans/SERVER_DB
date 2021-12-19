import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServer {
    static DBHandler dbHandler = new DBHandler();

    private static final int OPCODE_STATUS_DEVICE = 10;
    private static final int OPCODE_STATUS_ALL_DEVICES = 11;
    private static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    private static final int OPCODE_CHANGE_DEVICE_HOUSEHOLD = 21;
    private static final int OPCODE_CHANGE_DEVICE_NOTOK = 40; // använd för att felsöka

    private static final int CONNECTION_IDLE_TIMEOUT = 3600000;


    private static Map<WsContext, User> userUsernameMap = new ConcurrentHashMap<>();

    private static Gson gson;


    private static WebSocketServer ws;

    public static WebSocketServer getInstance() {
        if (ws == null) {
            ws = new WebSocketServer();
        }
        return ws;
    }


    static User demoUser;


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

        ArrayList<String> commandsTry = new ArrayList<>();
        Device device = gson.fromJson(String.valueOf(msgFromClient.get("data")), Device.class);
        DeviceDB deviceDB = dbHandler.getDeviceDB(device.getDeviceId());

        if (device.getValue() == 1) {
            String message = deviceDB.getDeviceId() + "-" + deviceDB.getCommandOn();
            commandsTry.add(message);
        } else {
            String message = deviceDB.getDeviceId() + "-" + deviceDB.getCommandOff();
            commandsTry.add(message);
        }

        ArduinoWebsocket aw = ArduinoWebsocket.getInstance();
        aw.send(commandsTry);
    }

    private void sendAllDevice(WsContext ctx) {
        int householdId = userUsernameMap.get(ctx).getHouseholdId();

        // returna om det inte får något meddelande.

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

    public void deviceUpdate(ArrayList<String> info) {
        String messageResponse = info.get(0);
        String[] response = messageResponse.split("-");
        int deviceUsed;
        String responseDetails = null;
        DeviceDB deviceDB = null;


        if(response.length > 2){
            deviceUsed = Integer.parseInt(response[0]);
            responseDetails = response[2];

            deviceDB = dbHandler.getDeviceDB(deviceUsed);
        }else{
            String command =  response[0];
            responseDetails = response[1];
            deviceDB = dbHandler.getDeviceUsingCommands(command);
        }

        if (responseDetails != null && responseDetails.equalsIgnoreCase("ok")) {
            if (deviceDB.getValue() == 0) {
                deviceDB.setValue(1);

            } else {
                deviceDB.setValue(0);
            }
        }
        dbHandler.updateDeviceValue(deviceDB);
        Device device = new Device(deviceDB.getDeviceId(), deviceDB.getName(), deviceDB.getType(), deviceDB.getValue(), deviceDB.getHouseholdId());

        userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
            session.send(
                    new JSONObject()
                            .put("opcode", OPCODE_STATUS_DEVICE)
                            .put("data", gson.toJson(device))
                            .toString()
            );
        });
    }
}
