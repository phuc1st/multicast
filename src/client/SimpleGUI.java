package client;

import javax.swing.*;
import java.awt.*;

public class SimpleGUI extends Thread {
	private clientObj clObj;
	private String username;
	private   JTextField textField;
	private JTextArea textArea;
	public final Runnable typeA;
	public SimpleGUI(String username, String serverAddress) {
		this.username = username;
//		init(username);	
		init();
		this.clObj = new clientObj(serverAddress,9876);
		connect();
		this.start();
		  typeA = new Runnable() {
	            public void run() {
	               while(true) {
	            	 String data2 = clObj.multicast();
	  				 textArea.append(data2+"\n");
	               }
	            }
	        };
	        new Thread(this.typeA).start();
	        
	}
	public void init() {
		 	JFrame frame = new JFrame("Simple GUI");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(400, 300);

	        // Tạo JTextArea
	        textArea = new JTextArea(10, 30);
	        JScrollPane scrollPane = new JScrollPane(textArea);

	        // Tạo JTextField và JButton
	        textField = new JTextField(20);
	        JButton button = new JButton("Send");
	        button.addActionListener(e->{
	        	clObj.send(textField.getText());
	        	textArea.setText("");
	        });

	        // Tạo panel cho JTextField và JButton
	        JPanel panel = new JPanel();
	        panel.setLayout(new BorderLayout());
	        panel.add(textField, BorderLayout.CENTER);
	        panel.add(button, BorderLayout.EAST);

	        // Thêm các thành phần vào frame
	        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	        frame.getContentPane().add(panel, BorderLayout.SOUTH);

	        // Hiển thị frame
	        frame.setVisible(true);
	}
	@Override
	public void run() {
		while(true) {
			String data = this.clObj.receive();
			textArea.append(data + "\n");
			try {
				this.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void connect() {
		this.clObj.send(this.username + ":" + "connect");
	}
    public static void main(String[] args) {
     new SimpleGUI("T", "127.0.0.1");
       
    }
}
