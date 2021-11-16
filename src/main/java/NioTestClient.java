import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;

public class NioTestClient {

    SocketChannel server;

    public static void main(String[] args) throws IOException {
        NioTestClient app = new NioTestClient();
        app.run();
    }

    public void run() throws IOException {
        setConnect();
        w();
        //r();
    }

    public void setConnect() throws IOException {
        server = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress("85.197.159.131", 2222);
        server.connect(socketAddr);
    }

    public void w() throws IOException {
        ArrayList<String> companyDetails = new ArrayList<String>();

// create a ArrayList with companyName list
        companyDetails.add("Facebook\n");
        companyDetails.add("Facebook2\n");
        companyDetails.add("Twitter\n");
        companyDetails.add("IBM\n");
        companyDetails.add("Google\n");
        companyDetails.add("Crunchify\n");
        companyDetails.add("koooooodd\n");

        for (String companyName : companyDetails) {
            byte[] message = companyName.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            server.write(buffer);
            buffer.clear();
        }
        server.close();
    }

    public void r() throws IOException {
        while (true) {
            ByteBuffer buffer3 = ByteBuffer.allocate(1024);
            while (server.read(buffer3) != -1) {
                Path path = Paths.get("temp2.txt");
                FileChannel fileChannel = FileChannel.open(path,
                        EnumSet.of(StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING,
                                StandardOpenOption.WRITE)
                );

                ByteBuffer buffer2 = ByteBuffer.allocate(1024);
                while (server.read(buffer2) > 0) {
                    buffer2.flip();
                    fileChannel.write(buffer2);
                    buffer2.clear();
                }
                System.out.println("File Received");
            }
            server.close();
        }
    }


}