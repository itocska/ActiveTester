package ecdh;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTextArea;

public class DataSaver {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataSaver window = new DataSaver();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DataSaver() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea txtrHey = new JTextArea();
		txtrHey.setText("hey!");
		txtrHey.setBounds(16, 88, 428, 144);
		frame.getContentPane().add(txtrHey);
		
		JButton btnUserLogin = new JButton("login user");
		btnUserLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String args[] = null;
				try {
					LoginUser.main(args);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnUserLogin.setBounds(72, 0, 225, 29);
		frame.getContentPane().add(btnUserLogin);
		
		JButton btnNewButton = new JButton("delete user");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String args[] = null;
				try {
					DeleteUser.main(args);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(72, 29, 225, 29);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnAddNewCar = new JButton("Add new car");
		btnAddNewCar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String args[] = null;
				try {
					txtrHey.setText("hey!ho!");
					AddNewCar.main(args);
				} catch (Throwable e1) {
					txtrHey.setText(e1.getMessage());
					e1.printStackTrace();
					
				}
			}
		});
		btnAddNewCar.setBounds(72, 59, 225, 29);
		frame.getContentPane().add(btnAddNewCar);
		
		String path = (new File("")).getAbsolutePath();
		txtrHey.setText(path);
		
		
		
		
	}
}
