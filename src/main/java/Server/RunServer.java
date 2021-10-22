package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class RunServer {

    private int port = 1337;

    public static void main(String[] args) {
        RunServer runServer = new RunServer();
        runServer.run();
    }

    private void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                try {
                    Socket client = server.accept();
                    Server handler = new Server(client);
                    handler.start();
                } catch (Exception exception) {
                    System.out.println("[ERROR] Failed to accept new connection");
                    exception.printStackTrace();
                }

            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to establish server socket");
            e.printStackTrace();
        }
    }
}
