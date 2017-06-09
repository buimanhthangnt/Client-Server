import java.net.*;
import java.util.ArrayList;
import java.util.List;


class FrontendServer {
    static void run(int portFrontend, int portMD5, int portSHA) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.12.1.1");
            MySocket server = new MySocket(serverAddress, portFrontend);
            Diagram diagram = null;
            while (diagram == null) {
                diagram = server.read();
            }
            InetAddress sha256 = InetAddress.getByName("127.12.1.2");
            InetAddress md5 = InetAddress.getByName("127.12.1.3");
            String forward = diagram.sourceAddr.toString() + " " +
                    Integer.toString(diagram.sourcePort) + " " + diagram.payload;
            server.send(forward, sha256, portSHA);
            server.send(forward, md5, portMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<String> parseMessageFromFrontend(String message) {
        StringBuilder addr = new StringBuilder();
        int i = 1;
        for (; i < message.length() && message.charAt(i) != ' '; i++) {
            addr.append(message.charAt(i));
        }
        StringBuilder port = new StringBuilder();
        for (i = i + 1; i < message.length() && message.charAt(i) != ' '; i++) {
            port.append(message.charAt(i));
        }
        StringBuilder mess = new StringBuilder();
        for (i = i + 1; i < message.length(); i++) {
            mess.append(message.charAt(i));
        }
        List<String> achi = new ArrayList<>();
        achi.add(addr.toString());
        achi.add(port.toString());
        achi.add(mess.toString());
        return achi;
    }
}