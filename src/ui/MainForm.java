package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class MainForm {

	private JFrame frame;

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
