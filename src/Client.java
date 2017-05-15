import mydatastructure.Diagram;
import mydatastructure.MySocket;
import java.io.IOException;
import java.net.*;

public class Client {

    public static void main(String[] args) throws IOException {
        InetAddress sourceAddr = InetAddress.getByName("127.12.1.10");
        InetAddress destinationAddr = InetAddress.getByName("127.12.1.1");
        MySocket client = new MySocket(sourceAddr, 3200);
        client.send("Welcome to the hell", destinationAddr, 9090, false);
        System.out.println("Sent message: \"Welcome to the hell\" to server 127.12.1.10");
        Diagram diagram1 = null;
        while (diagram1 == null) {
            diagram1 = client.read();
        }
        System.out.println("Message: " + diagram1.payload);

        Diagram diagram2 = null;
        while (diagram2 == null) {
            diagram2 = client.read();
        }
        System.out.println("Message: " + diagram2.payload);
    }
}