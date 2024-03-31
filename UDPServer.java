import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;

public class UDPServer extends JPanel implements KeyListener {
    private BufferedImage playerSprite;
    private BufferedImage armorsSprite;
    private BufferedImage[] customizationOptions;
    private int currentCustomizationIndex = 0;
    private int characterX = 200;
    private int characterY = 200;

    public UDPServer() {
        try {
            // Load player sprite and armors sprite
            playerSprite = ImageIO.read(new File("player.png"));
            armorsSprite = ImageIO.read(new File("armors.png"));

           // Extract customization options
            int numOptions = 6; // Number of armor options (including no armor)
            customizationOptions = new BufferedImage[numOptions];
            int optionWidth = armorsSprite.getWidth() / 3; // Assuming each row has 3 options
            int optionHeight = armorsSprite.getHeight() / 2; // Assuming 2 rows

            // Extract options from the first row (no armor, armor1, armor2)
            for (int i = 0; i < 3; i++) {
                customizationOptions[i] = armorsSprite.getSubimage(i * optionWidth, 0, optionWidth, optionHeight);
            }

            // Extract options from the second row (armor3, armor4, armor5)
            for (int i = 0; i < 3; i++) {
                customizationOptions[i + 3] = armorsSprite.getSubimage(i * optionWidth, optionHeight, optionWidth, optionHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(600, 400));
        setFocusable(true);
        addKeyListener(this); // Register the KeyListener for this JPanel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw player sprite
        g.drawImage(playerSprite, characterX, characterY, this);

        // Draw selected customization option on top of the player sprite
        if (currentCustomizationIndex != 0) {
            BufferedImage selectedOption = customizationOptions[currentCustomizationIndex - 1];
            g.drawImage(selectedOption, characterX, characterY, this);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                characterY -= 20;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                characterY += 20;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                characterX -= 20;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                characterX += 20;
                break;
            default:
                break;
        }
        repaint();
    }

    // Empty implementations for the other KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server Character Customization");
        UDPServer serverPanel = new UDPServer();
        frame.add(serverPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            DatagramSocket socket = new DatagramSocket(1234);

            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
                socket.receive(receivedPacket);

                String messageFromClient = new String(receivedPacket.getData()).trim();

                // Handle armor selection from client
                int selectedArmorIndex = Integer.parseInt(messageFromClient);
                serverPanel.currentCustomizationIndex = selectedArmorIndex;

                // Send acknowledgment to client
                byte[] acknowledgment = "Armor selection received".getBytes();
                DatagramPacket acknowledgmentPacket = new DatagramPacket(acknowledgment, acknowledgment.length, receivedPacket.getAddress(), receivedPacket.getPort());
                socket.send(acknowledgmentPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
