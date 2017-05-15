import mydatastructure.Diagram;
import mydatastructure.MySocket;

import java.net.*;


public class Server {
    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.1");
            MySocket server = new MySocket(serverAddress, 9090);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = server.read();
            }
            System.out.println("Source port: " + diagram.sourcePort);
            System.out.println("Destination port: " + diagram.destinationPort);
            System.out.println("Checksum: " + diagram.checksum);
            System.out.println("Length: " + diagram.length);
            System.out.println("ACK: " + diagram.isACK);
            System.out.println("Message: " + diagram.payload);

            InetAddress sha256 = InetAddress.getByName("127.12.1.2");
            InetAddress md5 = InetAddress.getByName("127.12.1.3");
            server.send(diagram.payload, sha256, 9091, false);
            server.send(diagram.payload, md5, 9092, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}