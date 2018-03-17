package main;

import java.awt.EventQueue;

import ui.MainForm;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainForm();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
