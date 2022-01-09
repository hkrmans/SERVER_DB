import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;

public class RunServer {
    ServerSocketChannel serverSocket = null;
    SocketChannel client = null;
    public static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        WebSocketServer ws = new WebSocketServer();
        ws.run();
        ArduinoWebsocket aw = new ArduinoWebsocket();
        aw.run();

        DBHandler dbHandler = new DBHandler();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            long currenttime = cal.getTimeInMillis();
            ArrayList<DeviceDB> deviceDBS;
            deviceDBS = dbHandler.getAllDevices();
            for(DeviceDB deviceDB: deviceDBS){
                if(deviceDB.getTimer() < currenttime && deviceDB.getTimer() != 0){
                    System.out.println("found device, deleting timer now.");
                    deviceDB.setValue(0);
                    deviceDB.setTimer(0);
                    Device device = new Device(deviceDB.getDeviceId(),deviceDB.getName(),deviceDB.getType(),deviceDB.getValue(),deviceDB.getHouseholdId(),deviceDB.getTimer());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("opcode", OPCODE_CHANGE_DEVICE_STATUS);
                        jsonObject.put("data", gson.toJson(device));
                        System.out.println("Sending: " + jsonObject);
                        ws.changeDeviceStatus(null,jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("process started." + jsonObject);

                }


            }

        }

    }
}