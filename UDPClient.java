import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        final InetAddress serverAddress;
        final int serverPort = 1234;
        DatagramSocket socket;

        try {
            serverAddress = InetAddress.getByName("127.0.0.1");
            socket = new DatagramSocket();

            // Create JComboBox for armor selection
            String[] armorOptions = {"No Armor", "Armor 1", "Armor 2", "Armor 3", "Armor 4", "Armor 5"};
            JComboBox<String> armorComboBox = new JComboBox<>(armorOptions);
            armorComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedArmor = String.valueOf(armorComboBox.getSelectedIndex());
                    byte[] messageToServer = selectedArmor.getBytes();
                    try {
                        DatagramPacket packetToSend = new DatagramPacket(messageToServer, messageToServer.length, serverAddress, serverPort);
                        socket.send(packetToSend);
                        System.out.println("Sent selected armor to server: " + selectedArmor);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            JFrame frame = new JFrame("Client Armor Selection");
            frame.setLayout(new BorderLayout());
            frame.add(new JLabel("Select Armor:"), BorderLayout.NORTH);
            frame.add(armorComboBox, BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
