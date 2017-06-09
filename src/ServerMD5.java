import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

class ServerMD5 {
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

    static void run(int port) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.3");
            MySocket serverMD5 = new MySocket(serverAddress, port);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = serverMD5.read();
            }
            List<String> list = FrontendServer.parseMessageFromFrontend(diagram.payload);
            InetAddress clientAddr = InetAddress.getByName(list.get(0));
            int clientPort = Integer.parseInt(list.get(1));
            String MD5value = getMD5(list.get(2));
            serverMD5.send("MD5: " + MD5value, clientAddr, clientPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
