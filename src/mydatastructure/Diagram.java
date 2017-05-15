package mydatastructure;

public class Diagram {
    public int sourcePort;
    public int destinationPort;
    public String checksum;
    public int length;
    public boolean isACK;
    public String payload;

    Diagram(int srcPort, int desPort, boolean isAck, String pload) {
        try {
            if (pload.length() > 80) throw new Exception();
            sourcePort = srcPort;
            destinationPort = desPort;
            isACK = isAck;
            payload = pload;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Maximum length of message is 80 characters");
        }
    }

    private Diagram(int srcPort, int desPort, String csum, int length, boolean isAck, String pload) {
        try {
            if (pload.length() > 80) throw new Exception();
            sourcePort = srcPort;
            destinationPort = desPort;
            checksum = csum;
            this.length = length;
            isACK = isAck;
            payload = pload;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Maximum length of message is 80 characters");
        }
    }

    static Diagram getDiagramFromByte(byte[] bytePacket) {
        int[] packet = new int[bytePacket.length];
        for (int i = 0; i < packet.length; i++) {
            packet[i] = (bytePacket[i] >= 0) ? bytePacket[i] : bytePacket[i] + 256;
        }
        int sourcePort;
        int destinationPort;
        String checksum;
        int length;
        boolean isACK;
        StringBuilder payload = new StringBuilder();
        sourcePort = 256 * packet[0] + packet[1];
        destinationPort = 256 * packet[2] + packet[3];
        checksum = Integer.toString(256 * packet[4] + packet[5]);
        length = packet[6];
        isACK = packet[7] == 1;
        for (int i = 8; i < packet.length; i++) {
            payload.append((char) packet[i]);
        }
        return new Diagram(sourcePort, destinationPort, checksum, length, isACK, payload.toString());
    }

    byte[] getPacketInByte() {
        byte[] packet = new byte[payload.length() + 8];
        //source port
        packet[0] = (byte) (sourcePort / 256);
        packet[1] = (byte) (sourcePort - 256 * packet[0]);
        //destination port
        packet[2] = (byte) (destinationPort / 256);
        packet[3] = (byte) (destinationPort - 256 * packet[2]);
        //checksum
        packet[4] = packet[5] = 0;  //initially, checksum is set to 0 to compute checksum
        //length
        packet[6] = (byte) (packet.length);
        packet[7] = (byte) ((isACK) ? 1 : 0);
        //payload
        byte[] message = payload.getBytes();
        System.arraycopy(message, 0, packet, 8, packet.length - 8);
        int checksum = computeChecksum(packet);
        //set checksum value to packet
        packet[4] = (byte) (checksum / 256);
        packet[5] = (byte) (checksum - 256 * packet[4]);
        return packet;
    }

    static int computeChecksum(byte[] s) {
        String hex_value;
        int x, i, checksum=0;
        for(i=0 ; i < s.length-2 ; i=i+2) {
            x = getByteValue(s[i]);
            hex_value = Integer.toHexString(x);
            x = getByteValue(s[i+1]);
            hex_value = hex_value + Integer.toHexString(x);
            x = Integer.parseInt(hex_value, 16);
            checksum += x;
        }
        if(s.length%2 == 0) {
            x = getByteValue(s[i]);
            hex_value = Integer.toHexString(x);
            x = getByteValue(s[i+1]);
            hex_value = hex_value + Integer.toHexString(x);
            x = Integer.parseInt(hex_value, 16);
        } else {
            x = getByteValue(s[i]);
            hex_value = "00" + Integer.toHexString(x);
            x = Integer.parseInt(hex_value, 16);
        }
        checksum += x;
        hex_value = Integer.toHexString(checksum);
        if(hex_value.length() > 4) {
            int carry = Integer.parseInt((""+hex_value.charAt(0)), 16);
            hex_value = hex_value.substring(1,5);
            checksum = Integer.parseInt(hex_value, 16);
            checksum += carry;
        }
        checksum = Integer.parseInt("FFFF", 16) - checksum;
        return checksum;
    }

    private static int getByteValue(byte value) {
        if (value >= 0) return value;
        return value + 256;
    }
}
