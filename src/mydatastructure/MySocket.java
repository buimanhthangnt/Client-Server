package mydatastructure;

import com.savarese.rocksaw.net.RawSocket;

import java.io.IOException;
import java.net.InetAddress;

public class MySocket extends RawSocket {
    private int sourcePort;
    private int countFailureWhileSending;

    public MySocket(InetAddress srcAddr, int sp) {
        sourcePort = sp;
        try {
            this.open(RawSocket.PF_INET, RawSocket.getProtocolByName("ip"));
            this.bind(srcAddr);
        } catch (Exception e) {
            System.out.println("Can not open socket");
        }
    }

    public Diagram read() throws IOException {
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
        write(InetAddress.getByAddress(senderAddr), ACK.getPacketInByte());
        return receivedDiagram;
    }

    public void send(String message, InetAddress destinationAddr, int destinationport, boolean isACK) throws IOException {
        Diagram diagram = new Diagram(sourcePort, destinationport, isACK, message);
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
        timeout.start();
        Diagram diagram2;
        while (true) {
            diagram2 = this.read();
            if (diagram2 != null && diagram2.isACK) {
                System.out.println("Received ACK!!!");
                timeout.stop();
                break;
            }
        }
    }

    private void increseCountFailure() {
        countFailureWhileSending++;
    }
}
