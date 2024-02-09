package com.password_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class EntryReadWrite {
	static final int ENTRY_SIZE = 3;
	static final int ENTRY_NAME_POS = 0;
	static final int ENTRY_USERNAME_POS = 1;
	static final int ENTRY_PASSWORD_POS = 2;
	
	static final String FILE_NAME = "PasswordEntries.txt";
	
	public static HashMap<String, String[]> entries = new HashMap<>();
	public static File entriesFile = new File(FILE_NAME);
	public static boolean fileChanged = false;
	
	public void readIn() {
		// Entries in the corresponding text file should be written in the order of Entry Name, Entry Username, and Entry Password
		// with a single space separating them.
		boolean invalid = false;
		
		try {
			// Try to make a new file for password entries. If it already exists, read in the entries.
			// Default path is "C:\Users\Joanna\eclipse-workspace\PasswordManager\PasswordEntries.txt"
			//File entriesFile = new File("PasswordEntries.txt");
			
			if (!entriesFile.createNewFile()) {
				FileReader reader = new FileReader(FILE_NAME);
				BufferedReader bufferedReader = new BufferedReader(reader);
				String line;
			
				while ((line = bufferedReader.readLine()) != null) {
					String[] splitLine = line.split(" ");
				
					// Stop if an entry is of an invalid size
					if (splitLine.length != ENTRY_SIZE) {
						invalid = true;
						break;
					}
				
					String entryName = splitLine[ENTRY_NAME_POS];
					String[] entryUserPass = {splitLine[ENTRY_USERNAME_POS], splitLine[ENTRY_PASSWORD_POS]};
				
					// Put into hash map using the entry name as the key
					entries.put(entryName, entryUserPass);
				}
							
				reader.close();
			}
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
		entries = invalid ? null : entries;
	}
	
	public boolean addEntry(String entryName, String entryUser, String entryPass) {
		//TODO: Use cipher to obscure data
		String[] entryUserPass = {entryUser, entryPass};
		entries.put(entryName, entryUserPass);
		
		fileChanged = true;
		
		return true;
	}
	
	public static void writeIn() {
		try {
			entriesFile.delete();
			entriesFile.createNewFile();
			
			FileWriter writer = new FileWriter(FILE_NAME);
			
			// Loop through each key and write it into the file
			//for () {
				
			//}
			writer.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
