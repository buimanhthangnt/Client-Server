import mydatastructure.Diagram;
import mydatastructure.MySocket;

import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerMD5 {
    private static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(number.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.2");
            MySocket serverMD5 = new MySocket(serverAddress, 9091);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = serverMD5.read();
            }
            System.out.println("Source port: " + diagram.sourcePort);
            System.out.println("Destination port: " + diagram.destinationPort);
            System.out.println("Checksum: " + diagram.checksum);
            System.out.println("Length: " + diagram.length);
            System.out.println("ACK: " + diagram.isACK);
            System.out.println("Message: " + diagram.payload);

            InetAddress ClientAddr = InetAddress.getByName("127.12.1.10");
            String MD5value = getMD5(diagram.payload);
            serverMD5.send(MD5value, ClientAddr, 3200, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
