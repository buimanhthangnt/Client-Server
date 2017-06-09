import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

class ServerSHA256 {
    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    static void run(int port) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.2");
            MySocket serverSHA256 = new MySocket(serverAddress, port);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = serverSHA256.read();
            }
            List<String> list = FrontendServer.parseMessageFromFrontend(diagram.payload);
            InetAddress clientAddr = InetAddress.getByName(list.get(0));
            int clientPort = Integer.parseInt(list.get(1));
            String SHAvalue = sha256(list.get(2));
            serverSHA256.send("SHA 256: " + SHAvalue, clientAddr, clientPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

