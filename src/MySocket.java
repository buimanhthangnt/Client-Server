import com.savarese.rocksaw.net.RawSocket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

class MySocket extends RawSocket {
    static {
        try {
            String temp = new File("rocksaw.dll").getAbsolutePath();
            StringBuilder absulutePath = new StringBuilder();
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) == '\\') {
                    absulutePath.append("\\\\");
                } else absulutePath.append(temp.charAt(i));
            }
            System.load(absulutePath.toString());
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            System.out.println("fail");
        }
    }

    private int sourcePort;
    private int countFailureWhileSending;

    MySocket(InetAddress srcAddr, int sp) {
        sourcePort = sp;
        try {
            this.open(RawSocket.PF_INET, RawSocket.getProtocolByName("ip"));
            this.bind(srcAddr);
        } catch (Exception e) {
            System.out.println("Can not open socket");
        }
    }

    Diagram read() throws IOException {
        byte[] receivedPacket = new byte[2400];
        byte[] senderAddr = new byte[4];
        read(receivedPacket, senderAddr);
        int endOfPacket = 0;
        for (int i = 28; i < 108; i++)
            if (receivedPacket[i] == 0) {
                endOfPacket = i;
                break;
            }
        byte[] diagramInByte = new byte[endOfPacket - 20];
        System.arraycopy(receivedPacket, 20, diagramInByte, 0, endOfPacket - 20);
        Diagram receivedDiagram = Diagram.getDiagramFromByte(diagramInByte);
        if (!(receivedDiagram.destinationPort == sourcePort)) return null;
        if (receivedDiagram.isACK) return receivedDiagram;
        diagramInByte[4] = diagramInByte[5] = 0;
        if (!receivedDiagram.checksum.equals(Integer.toString(Diagram.computeChecksum(diagramInByte))))
            return null;
        Diagram ACK = new Diagram(receivedDiagram.destinationPort,
                receivedDiagram.sourcePort, true, "ACK");
        //send ACK back to sender
        write(InetAddress.getByAddress(senderAddr), ACK.getPacketInByte());
        receivedDiagram.sourceAddr = InetAddress.getByAddress(senderAddr);
        return receivedDiagram;
    }

    void send(String message, InetAddress destinationAddr, int destinationport) throws IOException {
        Diagram diagram = new Diagram(sourcePort, destinationport, false, message);
        countFailureWhileSending = 0;
        Thread timeout = new Thread(() -> {
            try {
                while (true) {
                    write(destinationAddr, diagram.getPacketInByte());
                    Thread.sleep(10000);
                    increseCountFailure();
                    if (countFailureWhileSending == 20) {
                        System.out.println("20 failures while sending, could not send the message!");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //set timeout
        timeout.start();
        Diagram diagram2;
        //wait for ACK
        while (true) {
            diagram2 = this.read();
            if (diagram2 != null && diagram2.isACK) {
                timeout.stop();
                break;
            }
        }
    }

    private void increseCountFailure() {
        countFailureWhileSending++;
    }
}
