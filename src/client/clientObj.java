package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import master.Server;

public class clientObj {
	private String hostname;
	private int port;
	private DatagramSocket socket;
	private DatagramPacket outgoing;
	private DatagramPacket incoming;
	private InetAddress address;
	private byte[] buffer = new byte[1024 * 1024];

	public clientObj(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		init();
	}

	private void init() {

		InetAddress addressgr;
		try {
			this.socket = new DatagramSocket();
			this.address = InetAddress.getByName(hostname);
			/*
			 * this.address = InetAddress.getByName(Server.GROUP_ADDRESS); socket = new
			 * MulticastSocket(Server.PORT); socket.joinGroup(address);
			 */
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void send(String data) {
		byte[] buffer = data.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String receive() {
		incoming = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(incoming);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(incoming.getData(), 0, incoming.getLength());
	}

	public String multicast() {
		byte[] BUFFER = new byte[4096];

		MulticastSocket socket = null;
		DatagramPacket inPacket = null;
		String msg = "";
		try {
			// Get the address that we are going to connect to.
			InetAddress address = InetAddress.getByName(Server.GROUP_ADDRESS);

			// Create a new Multicast socket
			socket = new MulticastSocket(Server.MULTICASTPORT);

			// Joint the Multicast group
			socket.joinGroup(address);
			inPacket = new DatagramPacket(BUFFER, BUFFER.length);
			socket.receive(inPacket);
			msg = new String(BUFFER, 0, inPacket.getLength());
			
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return msg;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
