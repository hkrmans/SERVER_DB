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
    private static final int OPCODE_CHANGE_DEVICE_INFO = 21;
    private static final int OPCODE_CHANGE_DEVICE_NOTOK = 40;
    private static final int OPCODE_CHANGE_DEVICE_INFO_NOTOK = 41;

    private static final int OPCODE_CREATE_USER = 23;
    private static final int OPCODE_CREATE_USER_OK = 13;
    private static final int OPCODE_CREATE_USER_NOTOK = 43;

    private static final int OPCODE_NEW_TOKEN = 12;
    private static final int OPCODE_REQUEST_TOKEN = 22;
    private static final int OPCODE_NEW_TOKEN_NOTOK = 42;
    private static final int OPCODE_INVALID_TOKEN = 49;
    private static final int OPCODE_FORGOT_PASSWORD = 99;

    private static final int OPCODE_CREATE_HOUSEHOLD_OK = 14;
    private static final int OPCODE_CREATE_HOUSEHOLD = 24;
    private static final int OPCODE_USER_CHANGE_HOUSEHOLD = 25;
    private static final int OPCODE_USER_CHANGE_HOUSEHOLD_NOTOK = 45;

    private static final int CONNECTION_IDLE_TIMEOUT = 3600000;


    private static Map<WsContext, User> userUsernameMap = new ConcurrentHashMap<>();
    private static Map<WsContext, User> authUsernameMap = new ConcurrentHashMap<>();

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
                String[] splitToken = token.split("\\$");
                String username = splitToken[0];
                System.out.println("House: username: " + username);
                String password = splitToken[1];

                //TODO: get user from database, store as "user"
                //Using demo user for now
                UserDB userDB = dbHandler.getUserUsingUsername(username);

                //On new connection, check if valid token
                if (token == null || !token.matches(userDB.getUsername() + "\\$" + userDB.getPassword())) {
                    System.out.println("incorrect token, closing");
                    sendInvalidTokenResponse(ctx);
                    ctx.session.close(1000, "Invalid user");
                    return;
                }

                User user = new User(userDB.getUserId(), userDB.getUsername(), userDB.getName(), userDB.getHouseholdId(), null);
                //Store connection in map
                userUsernameMap.put(ctx, user);
                System.out.println(user.getUsername() + " connected");

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
                    case OPCODE_CHANGE_DEVICE_INFO:
                        System.out.println(" - Change device info");
                        changeDeviceInfo(ctx, msgFromClient);
                        break;
                    case OPCODE_USER_CHANGE_HOUSEHOLD:
                        changeUserHousehold(ctx, msgFromClient);
                        break;
                    case OPCODE_CREATE_HOUSEHOLD:
                        createHousehold(ctx, msgFromClient);
                        break;
                }
            });
        });

        // ---- House Auth websocket ----
        app.ws("/houseauth", ws -> {
            ws.onConnect(ctx -> {
                //TODO: get user from database, store as "user"
                //Using demo user for now
                User user = demoUser;
                // jonte får tänka om han behöver något här inne annars skickar vi det åt helvete

                //Store connection in map
                authUsernameMap.put(ctx, demoUser);
                System.out.println("# Houseauth: new user connected to houseauth");
            });
            ws.onClose(ctx -> {
                User disconnectedUser = authUsernameMap.remove(ctx);
                if (disconnectedUser != null) {
                    System.out.println(disconnectedUser.getUsername() + " disconnected. Reason: (" +
                            ctx.status() + ")" + ctx.reason());
                }
                System.out.println("Houseauth: Current number of connected users: " + authUsernameMap.size());
            });
            ws.onMessage(ctx -> {
                System.out.println("# Houseauth: Received message");
                System.out.println(ctx.message());

                //Message arrives as JSON
                JSONObject msgFromClient = new JSONObject(ctx.message());

                //Get opcode to determine message action
                int opcode = msgFromClient.getInt("opcode");
                System.out.println("Houseauth:  - Message Opcode: " + opcode);
                switch (opcode) {
                    case OPCODE_REQUEST_TOKEN:
                        System.out.println(" - Request Token");
                        requestToken(ctx, msgFromClient);
                        break;
                    case OPCODE_CREATE_USER:
                        System.out.println(" - Create User");
                        createUser(ctx, msgFromClient);
                        break;
                    case OPCODE_CREATE_HOUSEHOLD:
                        System.out.println(" - Create Household");
                        createHousehold(ctx, msgFromClient);
                        break;
                    case OPCODE_FORGOT_PASSWORD:
                        forgotPassword(ctx, msgFromClient);
                }
            });
        });
    }

    public void changeDeviceStatus(WsContext senderCtx, JSONObject msgFromClient) {
        ArrayList<String> commandsTry = new ArrayList<>();
        Device device = gson.fromJson(String.valueOf(msgFromClient.get("data")), Device.class);
        DeviceDB deviceDB = dbHandler.getDeviceDB(device.getDeviceId());

        if(device.getTimer() != 0){
            deviceDB.setTimer(device.getTimer());
            dbHandler.updateDeviceValue(deviceDB);
            String message = deviceDB.getDeviceId() + "-" + deviceDB.getCommandOn();
            commandsTry.add(message);
        }else if (device.getType().equalsIgnoreCase("fan")){
            String message = deviceDB.getDeviceId() + "-" + deviceDB.getCommandOn() + (int)(device.getValue()*2.55);
            commandsTry.add(message);
        }else if (device.getType().equalsIgnoreCase("autosettings")){
            String message = deviceDB.getDeviceId() + "-" + deviceDB.getCommandOn() + (int)(device.getValue());
            commandsTry.add(message);
        }else if (device.getValue() == 1) {
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

        HouseholdDB householdDB = dbHandler.getHousehold(householdId);

        Household household = new Household(householdDB.getHouseholdId(), householdDB.getName());


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
                        .put("household", gson.toJson(household))
                        .put("nameOfUser", userUsernameMap.get(ctx).getName())
                        .toString()
        );
    }

    public void deviceUpdate(ArrayList<String> info) {
        String messageResponse = info.get(0);
        String[] response = messageResponse.split("-");
        int deviceUsed;
        String responseDetails = null;
        String command = null;
        DeviceDB deviceDB = null;
        System.out.println(info.get(0));

        // när jag får av jonte gör vi sånt här trams
        if (response.length > 2) {
            deviceUsed = Integer.parseInt(response[0]);
            command = response[1];
            responseDetails = response[2];

            deviceDB = dbHandler.getDeviceDB(deviceUsed);
            System.out.println(deviceDB.getValue() + " " + deviceUsed);
            if(deviceDB.getType().equalsIgnoreCase("fan")){
                int trams = (int)(Integer.valueOf(response[2])/2.55);
                deviceDB.setValue(trams);
            }else if (deviceDB.getType().equalsIgnoreCase("autosettings")){
                int trams = (int)(Integer.valueOf(response[2]));
                deviceDB.setValue(trams);
            }

        } else { // när jag får av simpiss squad så gör jag sånt här trams
            command = response[0];
            responseDetails = response[1];


            if (messageResponse.contains("alarm") || messageResponse.contains("leakage")) {
                deviceDB = dbHandler.getDeviceDBFromAlarm(command);
                deviceDB.setValue(2);
            } else {
                deviceDB = dbHandler.getDeviceUsingCommands(command);
            }
        }

        if(deviceDB.getType().equals("thermometer") || deviceDB.getType().equals("powersensor")){
            deviceDB.setValue(Integer.valueOf(responseDetails));
        }

        if (responseDetails != null && responseDetails.equalsIgnoreCase("ok")) {
            if (deviceDB.getCommandOn().equalsIgnoreCase(command)) {
                deviceDB.setValue(1);
                System.out.println(deviceDB.getName());

            } else {
                deviceDB.setValue(0);
                deviceDB.setTimer(0);
            }

        } else if(responseDetails != null && responseDetails.equalsIgnoreCase("notok")){
            Device device = new Device(deviceDB.getDeviceId(), deviceDB.getName(), deviceDB.getType(), deviceDB.getValue(), deviceDB.getHouseholdId(),deviceDB.getTimer());

            userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen() &
                            userUsernameMap.get(ctx).getHouseholdId() == device.getHouseholdId())
                    .forEach(session ->  {
                session.send(
                        new JSONObject()
                                .put("opcode", OPCODE_CHANGE_DEVICE_NOTOK)
                                .put("data", gson.toJson(device))
                                .toString()
                );
            });

        }
        dbHandler.updateDeviceValue(deviceDB);
        Device device = new Device(deviceDB.getDeviceId(), deviceDB.getName(), deviceDB.getType(), deviceDB.getValue(), deviceDB.getHouseholdId(),deviceDB.getTimer());

        userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen() &
                        userUsernameMap.get(ctx).getHouseholdId() == device.getHouseholdId())
                .forEach(session -> {
            session.send(
                    new JSONObject()
                            .put("opcode", OPCODE_STATUS_DEVICE)
                            .put("data", gson.toJson(device))
                            .toString()
            );
        });
    }

    ////////////// House auth methods ///////////////////////////////////////////
    private static void createHousehold(WsContext ctx, JSONObject msgFromClient) {
        //Get new household object from jsonObject
        Household receivedHousehold = gson.fromJson(String.valueOf(msgFromClient.get("data")), Household.class);

        //Create household in database
        HouseholdDB householdDB = dbHandler.createHousehold(receivedHousehold);

        Household newHousehold = new Household(householdDB.getHouseholdId(), householdDB.getName());

        //Return new household
        System.out.println("Houseauth: Created new Household: (" + newHousehold.getHouseholdId() + ")" + newHousehold.getName());
        ctx.send(
                new JSONObject()
                        .put("opcode", OPCODE_CREATE_HOUSEHOLD_OK)
                        .put("data", gson.toJson(newHousehold))
                        .toString()
        );
        System.out.println("Houseauth: Sent household");
    }

    private static void requestToken(WsContext ctx, JSONObject msgFromClient) {
        String token = "";
        //Get user from JsonObject
        User receivedUser = gson.fromJson(String.valueOf(msgFromClient.get("data")), User.class);
        System.out.println("HouseAuth: Message: " + msgFromClient);

        //Find user in database
        //If invalid username/password, send error code

        UserDB userDB = dbHandler.getUserUsingUsername(receivedUser.getUsername());
        System.out.println(userDB == null);
        System.out.println(userDB.getPassword());

        if (userDB == null || !userDB.getPassword().equals(receivedUser.getPassword())) {
            System.out.println("Houseauth: requestToken: invalid: " + receivedUser.getUsername());
            ctx.send(
                    new JSONObject()
                            .put("opcode", OPCODE_NEW_TOKEN_NOTOK)
                            .put("data", "Invalid credentials")    //Room for error message
                            .toString()
            );
            return;
        }
        //Generate token
        token = receivedUser.getUsername() + "$" + receivedUser.getPassword();
        System.out.println("Houseauth: requestToken: Sending: " + token);

        //Send token
        ctx.send(
                new JSONObject()
                        .put("opcode", OPCODE_NEW_TOKEN)
                        .put("data", token)
                        .toString()
        );
    }

    private static void sendInvalidTokenResponse(WsContext ctx) {
        //Send all devices to one connection
        ctx.send(
                new JSONObject()
                        .put("opcode", OPCODE_INVALID_TOKEN)
                        .put("data", "Invalid token")
                        .toString()
        );
    }

    private static void createUser(WsContext ctx, JSONObject msgFromClient) {
        //Get new user object from jsonObject
        User receivedUser = gson.fromJson(String.valueOf(msgFromClient.get("data")), User.class);

        //Check User in Database
        UserDB userdb = dbHandler.getUserUsingUsername(receivedUser.getUsername());

        HouseholdDB householdDB = dbHandler.getHousehold(receivedUser.getHouseholdId());


        //  If error creating user (such as existing username), send error code
        if (userdb != null || householdDB == null) {
            System.out.println("Houseauth: failed creating user");
            ctx.send(
                    new JSONObject()
                            .put("opcode", OPCODE_CREATE_USER_NOTOK)
                            .put("data", "")    //Room for error message
                            .toString()
            );
            return;
        }

        //Create user in database
        dbHandler.createUser(receivedUser);


        //Return new user
        System.out.println("Houseauth: Created new user: (" + receivedUser.getUsername() + ")");
        ctx.send(
                new JSONObject()
                        .put("opcode", OPCODE_CREATE_USER_OK)
                        .toString()
        );
    }

    private void changeUserHousehold(WsContext senderCtx, JSONObject msgFromClient) {
        int newHouseholdId = msgFromClient.getInt("data");

        //Check if household exists in database
        HouseholdDB householdDB = dbHandler.getHousehold(newHouseholdId);

        if (householdDB == null) {
            System.out.println("Could  not join household");
            //If not exist, send error
            senderCtx.send(
                    new JSONObject()
                            .put("opcode", OPCODE_USER_CHANGE_HOUSEHOLD_NOTOK)
                            .put("data", "Household could not be changed")
                            .toString()
            );
            sendAllDevice(senderCtx);
            return;
        }

        //Change for user in active session
        User user = userUsernameMap.get(senderCtx);
        user.setHouseholdId(newHouseholdId);

        //Change in database
        dbHandler.updateUserHouseholdValue(user);

        //Send all devices to user
        sendAllDevice(senderCtx);
        System.out.println("update finished");
    }

    private void changeDeviceInfo(WsContext senderCtx, JSONObject msgFromClient) {
        Device device = gson.fromJson(String.valueOf(msgFromClient.get("data")), Device.class);

        User user = userUsernameMap.get(senderCtx);

        // hämta device i databas, få command
        DeviceDB deviceDB = dbHandler.getDeviceDB(device.getDeviceId());
        if (deviceDB.getHouseholdId() != null && !(user.getHouseholdId() == deviceDB.getHouseholdId())) {

            senderCtx.send(
                    new JSONObject()
                            .put("opcode", OPCODE_CHANGE_DEVICE_INFO_NOTOK)
                            .put("data", "Could not change device")
                            .toString()
            );
            return;
        }

        int oldHouseHoldId = deviceDB.getHouseholdId();

        if (device.getHouseholdId() == 0) {
            deviceDB.setHouseholdId(null);
        } else {
            deviceDB.setHouseholdId(device.getHouseholdId());
        }

        deviceDB.setType(device.getType());
        deviceDB.setName(device.getName());

        // uppdatera databas
        dbHandler.updateDeviceValue(deviceDB);
        //
        System.out.println(deviceDB.getHouseholdId());

        //Broadcast device update to all connections
        userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen() &
                userUsernameMap.get(ctx).getHouseholdId() == deviceDB.getHouseholdId())
                .forEach(session -> {

            session.send(
                    new JSONObject()
                            .put("opcode", OPCODE_STATUS_DEVICE)
                            .put("data", gson.toJson(device))
                            .toString()
            );
        });
    }


    private static void forgotPassword(WsContext senderCtx, JSONObject msgFromClient) {
        String username = msgFromClient.get("data").toString();
        User user = null;

        UserDB userDB = dbHandler.getUserUsingUsername(username);
        if (userDB == null) {
            System.out.println("[ForgotPassword] User not found: " + username);
            return;
        }

        // Send email & update password in database
        EmailService emailService = new EmailService();
        emailService.forgotPassword(userDB);
    }
}
