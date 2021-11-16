import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DevicesServer {
    private Socket socketOut;
    OutputStream os;
    InputStream is;
    ObjectOutputStream oo;
    ObjectInputStream oi;

    public void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(2222);
            socketOut = ss.accept();
            System.out.println("Connected");
            os = socketOut.getOutputStream();
            is = socketOut.getInputStream();
            oo = new ObjectOutputStream(os);
            oi = new ObjectInputStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getStatus(ArrayList<String> commands) throws IOException, ClassNotFoundException {
        System.out.println("Vi är i get status metoden.");
        ArrayList<String> statuses;
        oo.writeObject(commands);
        statuses = (ArrayList<String>) oi.readObject();
        System.out.println(statuses.get(0));
        return statuses;
    }

}



