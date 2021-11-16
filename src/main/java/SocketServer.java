import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;

public class SocketServer extends Thread {

    private SocketChannel client;

    public SocketServer(SocketChannel client) {
        this.client = client;
    }

    public void run() {
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);
        System.out.println(2);

        try{
            while (client.read(buffer2) != -1) {

                System.out.println(3);

                System.out.println("Connection Set:  " + client.getRemoteAddress());
                Path path = Paths.get("temp1.txt");
                FileChannel fileChannel = FileChannel.open(path,
                        EnumSet.of(StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING,
                                StandardOpenOption.WRITE)

                );

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer2.flip();
                fileChannel.write(buffer2);
                buffer2.clear();
                while (client.read(buffer) > 0) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                    System.out.println("ssss");
                }
                System.out.println("File Received");


            }
            client.close();

        }catch(IOException E) {
            E.printStackTrace();
        }
    }

    public void sendToClient(String string) throws IOException {
        ArrayList<String> companyDetails = new ArrayList<String>();

// create a ArrayList with companyName list
        companyDetails.add("Facebook\n");
        companyDetails.add("Facebook2\n");
        companyDetails.add("Twitter\n");
        companyDetails.add("IBM\n");
        companyDetails.add("Google\n");
        companyDetails.add("Crunchify\n");

        for (String companyName : companyDetails) {
            byte[] message = companyName.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);
            buffer.clear();
        }

    }
}

