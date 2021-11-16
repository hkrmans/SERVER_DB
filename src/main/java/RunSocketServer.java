import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class RunSocketServer extends Thread{
    ServerSocketChannel serverSocket = null;
    SocketChannel client = null;

    socketServer ss ;

    public RunSocketServer() {
    }

    public void run(){
        try{
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(2222));

            while(true){
                client = serverSocket.accept();
                ss = new socketServer(client);
                ss.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public socketServer getSs() {
        return ss;
    }
}
