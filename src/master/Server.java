package master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class Server extends Thread {
	public static final int PORT = 9876;
	public static final int MULTICASTPORT = 8000;
	public static final String GROUP_ADDRESS = "224.0.0.1";
	private static Hashtable<String, InetAddress> userAddress = new Hashtable<>();
	private static Hashtable<String, Integer> userPort = new Hashtable<>();
	private static Hashtable<InetAddress, Integer> userAddressPort = new Hashtable<>();
	private Hashtable<String, List<ClientAddress>> clientsAddress = new Hashtable<>();
	private DatagramSocket socketGroup;
	private MainForm mainForm;

	public Server(MainForm mainForm) {
		this.mainForm = mainForm;
		this.start();
	}

	public void run() {
		try {
			socketGroup = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try (DatagramSocket socket = new DatagramSocket(PORT)) {
			byte[] buffer = new byte[1024 * 1024];
			InetAddress GroupAddress = InetAddress.getByName(GROUP_ADDRESS);
			DatagramPacket outPacket = null;
			while (true) {

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String target ="";
				String incoming = new String(packet.getData(), 0, packet.getLength());
				if(incoming.contains("{"))
					target = incoming.substring(incoming.indexOf("{")+1, incoming.indexOf("}"));
				String[] parts = incoming.split(":"); // "from:to:message"
				this.mainForm.appendMessageClient(incoming);
				if (parts.length == 2) {
					String username = parts[0].trim();
					InetAddress address = packet.getAddress();
					int clientPort = packet.getPort();

					userAddress.put(username, address);
					userPort.put(username, clientPort);
					userAddressPort.put(address, clientPort);
					this.mainForm.appendMessage("Client: " + address + " " + clientPort + " connected");
					
				} else {
					InetAddress address = packet.getAddress();
					int clientPort = packet.getPort();
					String msg = address.toString() +" "+ clientPort + " ";
					msg += incoming.substring(incoming.indexOf("}"));
					if(target.equalsIgnoreCase("group")) {					           
			             outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, GroupAddress, MULTICASTPORT);
			             socketGroup.send(outPacket);
					}
					else if(target.equalsIgnoreCase("all")) {
						outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName("255.255.255.255"), MULTICASTPORT);
						 socketGroup.send(outPacket);
					}else {
						InetAddress i = InetAddress.getByName("127.0.0.1");
						outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, i, Integer.parseInt(target));
						socket.send(outPacket);
					}														
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendResponse(DatagramSocket socket, InetAddress address, int clientPort, String response)
			throws IOException {
		byte[] responseData = response.getBytes();
		DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, address, clientPort);
		socket.send(responsePacket);
	}
}
