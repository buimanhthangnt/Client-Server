import java.io.IOException;
import java.net.*;

public class Client {

    public static void main(String[] args) throws IOException {
        InetAddress clientAddr = InetAddress.getByName("127.12.1.10");
        InetAddress serverAddr = InetAddress.getByName(args[0]);
        MySocket client = new MySocket(clientAddr, 3200);
        if (args[2].length() > 80) {
            System.out.println("Maximum message length is 80 characters");
            return;
        }
        client.send(args[2], serverAddr, Integer.parseInt(args[1]));
        System.out.println("Response from server:");
        System.out.println("Hash value of \"" + args[2] + "\"");
        Diagram diagram1 = null;
        while (diagram1 == null) {
            diagram1 = client.read();
        }
        System.out.println(diagram1.payload);

        Diagram diagram2 = null;
        while (diagram2 == null) {
            diagram2 = client.read();
        }
        System.out.println(diagram2.payload);
    }
}