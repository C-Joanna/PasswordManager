package com.password_manager;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
//import java.io.Serializable;
import com.password_manager.EntryReadWrite;

public class EntryWindow extends Frame {
	private static final long serialVersionUID = 7043752297123044721L;
	
	static final int WINDOW_INIT_WIDTH = 750;
	static final int WINDOW_INIT_HEIGHT = 500;
	
	public EntryWindow() {
		TextField nameField = new TextField();
		TextField userField = new TextField();
		TextField passField = new TextField();
		Button addButton = new Button();
		
		// read entries and if it returns an empty hash map, put an error message and exit
		EntryReadWrite entryRW = new EntryReadWrite();
		entryRW.readIn();
		
		if (EntryReadWrite.entries == null) {
			//TODO: Error reading the entry text file
		}
		
		Frame window = new Frame("Password Manager");
		frameSetUp(window);
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (EntryReadWrite.entries.containsKey(nameField.getText())) {
					// TODO: Error message - no dupes
				}
				else if (nameField.getText().isBlank() || nameField.getText().contains(" ") ||
						userField.getText().isBlank() || userField.getText().contains(" ") ||
						passField.getText().isBlank() || passField.getText().contains(" ")) {
					//TODO: Error message - invalid input
				}
				else {
					entryRW.addEntry(nameField.getText(), userField.getText(), passField.getText());
				}
			}
		});
		
		window.add(nameField);
		window.add(userField);
		window.add(passField);
		window.add(addButton);
	}
	
	//https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/Frame.html
	//https://docs.oracle.com/javase/tutorial/uiswing/events/windowlistener.html
	//https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/event/WindowEvent.html
	//https://www.wikihow.com/Close-a-Window-in-Java
	public void frameSetUp(Frame f) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameInitX = (int)screenSize.getWidth()/2;
		int frameInitY = (int)screenSize.getHeight()/2;
		f.setBounds(frameInitX - WINDOW_INIT_WIDTH/2, frameInitY - WINDOW_INIT_HEIGHT/2, WINDOW_INIT_WIDTH, WINDOW_INIT_HEIGHT);
		//f.addWindowListener();
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (EntryReadWrite.fileChanged) {
					EntryReadWrite.writeIn();
				}
				
				System.exit(0);
			}
		});
		
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EntryWindow();
	}
}
