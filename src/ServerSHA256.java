import mydatastructure.Diagram;
import mydatastructure.MySocket;

import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerSHA256 {
    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.3");
            MySocket serverSHA256 = new MySocket(serverAddress, 9092);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = serverSHA256.read();
            }
            System.out.println("Source port: " + diagram.sourcePort);
            System.out.println("Destination port: " + diagram.destinationPort);
            System.out.println("Checksum: " + diagram.checksum);
            System.out.println("Length: " + diagram.length);
            System.out.println("ACK: " + diagram.isACK);
            System.out.println("Message: " + diagram.payload);

            InetAddress ClientAddr = InetAddress.getByName("127.12.1.10");
            String MD5value = sha256(diagram.payload);
            serverSHA256.send(MD5value, ClientAddr, 3200, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

