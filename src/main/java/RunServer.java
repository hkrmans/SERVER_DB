import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class RunServer {
    ServerSocketChannel serverSocket = null;
    SocketChannel client = null;

    public static void main(String[] args) throws IOException {


        WebSocketServer ws = new WebSocketServer();
        ws.run();
        ArduinoWebsocket aw = new ArduinoWebsocket();
        aw.run();


    }
}