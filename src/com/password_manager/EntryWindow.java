package com.password_manager;

import java.awt.*;
import java.awt.event.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EntryWindow {
	private static final int WINDOW_INIT_WIDTH = 750;
	private static final int WINDOW_INIT_HEIGHT = 500;
	private static final String PASSWORD = "10pacesleft";

	private static final String[] tableColumnNames = {"Entry Name", "Username", "Password"};
	
	private JTabbedPane tabPanel;
	
	private JPanel entrySavePanel;
	private JTextField nameField;
	private JTextField userField;
	private JTextField passField;
	private JLabel nameFieldLabel;
	private JLabel userFieldLabel;
	private JLabel passFieldLabel;
	private JButton saveButton;
	
	private JPanel entrySearchPanel;
	private JLabel searchFieldLabel;
	private JTextField searchField;
	private JTable searchTable;
	private DefaultTableModel searchTableDTM;
	private JScrollPane searchTableScroll;
	private JButton searchButton;
	private JButton deleteButton;
	
	private EntryReadWrite entryRW;
	
	public EntryWindow() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		// Enter password to access entries
		while (true) {
			String passCheck = JOptionPane.showInputDialog("Enter password:");
			if (passCheck == null) {
				System.exit(0);
			}
			else if (passCheck.equals(PASSWORD)) {
				break;
			}
			else {
				JOptionPane.showMessageDialog(null, "Incorrect password.", "Notice", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		// Read entries and if it returns an empty hash map, put an error message and exit
		entryRW = new EntryReadWrite();
		entryRW.readIn();
		
		if (entryRW.getEntries() == null) {
			JOptionPane.showMessageDialog(null, "Unable to load entries.", "Notice", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		
		JFrame window = new JFrame("Password Manager");
		frameSetUp(window);
	}
	
	//https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/Frame.html
	//https://docs.oracle.com/javase/tutorial/uiswing/events/windowlistener.html
	//https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/event/WindowEvent.html
	//https://www.codejava.net/java-se/swing/jframe-basic-tutorial-and-examples
	//https://www.javatpoint.com/java-gridbaglayout
	//https://www.geeksforgeeks.org/java-jtabbedpane/
	//https://www.geeksforgeeks.org/java-swing-jtable/
	
	public void frameSetUp(JFrame f) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameInitX = (int)screenSize.getWidth()/2;
		int frameInitY = (int)screenSize.getHeight()/2;
		f.setBounds(frameInitX - WINDOW_INIT_WIDTH/2, frameInitY - WINDOW_INIT_HEIGHT/2, WINDOW_INIT_WIDTH, WINDOW_INIT_HEIGHT);
		
		tabPanel = new JTabbedPane();
		
		savePanelSetUp();
		searchPanelSetUp();
		
		tabPanel.addTab("Add Entries", entrySavePanel);
		tabPanel.addTab("Search Entries", entrySearchPanel);
		
		// Save any changes if entries were added/deleted.
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (entryRW.isFileChanged()) {
					try {
						entryRW.writeIn();
					}
					catch (InvalidKeyException exception) {
						exception.printStackTrace();
					}
					catch (NoSuchPaddingException exception) {
						exception.printStackTrace();
					}
					catch (NoSuchAlgorithmException exception) {
						exception.printStackTrace();
					}
					catch (BadPaddingException exception) {
						exception.printStackTrace();
					}
					catch (IllegalBlockSizeException exception) {
						exception.printStackTrace();
					}
				}
				
				System.exit(0);
			}
		});
		
		f.add(tabPanel);
		f.setVisible(true);
	}
	
	public void savePanelSetUp() {
		entrySavePanel = new JPanel();
		entrySavePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		
		nameFieldLabel = new JLabel("Entry Name");
		c.gridx = 0;
		c.gridy = 0;
		entrySavePanel.add(nameFieldLabel, c);
		
		nameField = new JTextField();
		nameField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveEntry();
				}
			}
		});;
		c.ipadx = 100;
		c.gridx = 1;
		c.gridy = 0;
		entrySavePanel.add(nameField, c);
		
		userFieldLabel = new JLabel("Entry Username");
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 1;
		entrySavePanel.add(userFieldLabel, c);
		
		userField = new JTextField();
		userField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveEntry();
				}
			}
		});
		c.ipadx = 100;
		c.gridx = 1;
		c.gridy = 1;
		entrySavePanel.add(userField, c);
		
		passFieldLabel = new JLabel("Entry Password");
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 2;
		entrySavePanel.add(passFieldLabel, c);
		
		passField = new JTextField();
		passField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveEntry();
				}
			}
		});
		c.ipadx = 100;
		c.gridx = 1;
		c.gridy = 2;
		entrySavePanel.add(passField, c);
		
		saveButton = new JButton("Save");
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveEntry();
			}
		});
		
		entrySavePanel.add(saveButton, c);
	}
	
	public void searchPanelSetUp() {
		entrySearchPanel = new JPanel();
		entrySearchPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		
		searchFieldLabel = new JLabel("Search Entry Name");
		c.gridx = 0;
		c.gridy = 0;
		entrySearchPanel.add(searchFieldLabel, c);
		
		searchField = new JTextField();
		searchField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !searchField.getText().isBlank()) {
					searchEntry();
				}
			}
		});
		c.ipadx = 100;
		c.gridx = 0;
		c.gridy = 1;
		entrySearchPanel.add(searchField, c);
		
		searchTableDTM = new DefaultTableModel();
		searchTable = new JTable(searchTableDTM);
		
		for (String name : tableColumnNames) {
			searchTableDTM.addColumn(name);
		}
		
		searchTableScroll = new JScrollPane(searchTable);
		c.ipadx = 500;
		c.ipady = 100;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 4;
		entrySearchPanel.add(searchTableScroll, c);
		
		searchButton = new JButton("Search");
		c.ipadx = 0;
		c.ipady = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 2;
		
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!searchField.getText().isBlank()) {
					searchEntry();
				}
			}
		});
		
		entrySearchPanel.add(searchButton, c);
		
		deleteButton = new JButton("Delete");
		c.gridx = 0;
		c.gridy = 3;
		
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (searchTable.getSelectedRow() >= 0) {
					int decision = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected entry?", "Notice", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					
					if (decision == JOptionPane.YES_OPTION) {
						entryRW.deleteEntry((String)searchTable.getValueAt(0, searchTable.getSelectedRow()));
						searchTableDTM.removeRow(searchTable.getSelectedRow());
						
						searchTableDTM.fireTableDataChanged();
						searchTable.revalidate();
					}
				}
			}
		});
		
		entrySearchPanel.add(deleteButton, c);
		
		// Fill up the search table with all entries
		String[] entryArray = new String[EntryReadWrite.ENTRY_SIZE];
		
		for (String entry : entryRW.entries.keySet()) {
			entryArray[EntryReadWrite.ENTRY_NAME_POS] = entry;
			entryArray[EntryReadWrite.ENTRY_USER_POS] = entryRW.getEntries().get(entry)[EntryReadWrite.HASH_MAP_USER_POS];
			entryArray[EntryReadWrite.ENTRY_PASS_POS] = entryRW.getEntries().get(entry)[EntryReadWrite.HASH_MAP_PASS_POS];
			searchTableDTM.addRow(entryArray);
		}
		
		searchTableDTM.fireTableDataChanged();
		searchTable.revalidate();
	}
	
	private void saveEntry() {
		if (entryRW.entries.containsKey(nameField.getText())) {
			JOptionPane.showMessageDialog(null, "Duplicate entry found. Unable to save entries with the same name.", "Notice", JOptionPane.WARNING_MESSAGE);
		}
		else if (nameField.getText().isBlank() ||
				userField.getText().isBlank() || userField.getText().contains(" ") ||
				passField.getText().isBlank() || passField.getText().contains(" ")) {
			JOptionPane.showMessageDialog(null, "Invalid input. The name of the entry cannot be blank. The username or password cannot be blank or contain spaces.", "Notice", JOptionPane.WARNING_MESSAGE);
		}
		else {
			entryRW.addEntry(nameField.getText(), userField.getText(), passField.getText());
			JOptionPane.showMessageDialog(null, "Entry saved.", "Notice", JOptionPane.INFORMATION_MESSAGE);
			nameField.setText("");
			userField.setText("");
			passField.setText("");
		}
	}
	
	private void searchEntry() {
		String[] matchingEntry = new String[EntryReadWrite.ENTRY_SIZE];
		
		searchTableDTM.setNumRows(0);
		
		for (String entry : entryRW.entries.keySet()) {
			if (entry.toLowerCase().contains(searchField.getText())) {
				matchingEntry[EntryReadWrite.ENTRY_NAME_POS] = entry;
				matchingEntry[EntryReadWrite.ENTRY_USER_POS] = entryRW.getEntries().get(entry)[EntryReadWrite.HASH_MAP_USER_POS];
				matchingEntry[EntryReadWrite.ENTRY_PASS_POS] = entryRW.getEntries().get(entry)[EntryReadWrite.HASH_MAP_PASS_POS];
				searchTableDTM.addRow(matchingEntry);
			}
		}
		
		searchTableDTM.fireTableDataChanged();
		searchTable.revalidate();
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		new EntryWindow();
	}
}
