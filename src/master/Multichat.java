package master;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;

public class Multichat {

    private static final String GROUP_ADDRESS = "225.168.20.1";
    private static final int PORT = 4321;

    public static void main(String[] args) {
        new Thread(Multichat::startReceiver).start();

        /*try (Scanner scanner = new Scanner(System.in)) {
            DatagramSocket socket = new DatagramSocket();

            System.out.println("Nhập thông điệp của bạn (gõ 'exit' để thoát): ");
            while (true) {
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                byte[] buffer = message.getBytes();
                InetAddress address = InetAddress.getByName(GROUP_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @SuppressWarnings("deprecation")
	private static void startReceiver() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress group = InetAddress.getByName(GROUP_ADDRESS);
            NetworkInterface netIf = NetworkInterface.getByName("bge0");

            socket.joinGroup(new InetSocketAddress(group, 0), netIf);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                String senderAddress = packet.getAddress().getHostAddress();

                System.out.println("Nhận được thông điệp: \"" + message + "\" từ " + senderAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
