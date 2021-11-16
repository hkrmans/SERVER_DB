import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class RunServer {
    ServerSocketChannel serverSocket = null;
    SocketChannel client = null;

    public static void main(String[] args) throws IOException {
        RunSocketServer rss  = new RunSocketServer();
        rss.start();

        while(true){
            if (rss.getSs() != null){
                socketServer ss = rss.getSs();
                ss.sendToClient("Aiids");
            }
        }
    }
}