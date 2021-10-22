package Server;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Server extends Thread {

    private final Socket client;

    Server(Socket client) {
        this.client = client;
    }

    public void run() {
        InetAddress inetAddress = null;

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

            inetAddress = client.getLocalAddress();
            System.out.println("[successfulL] Client connected: " + inetAddress);

            String test = reader.readLine();
            System.out.println(test);
            writer.println("Server response: " + test);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception caught: client disconnected.");

        }
        System.out.println("[successfulL] Client disconnected: " + inetAddress);
    }

}
