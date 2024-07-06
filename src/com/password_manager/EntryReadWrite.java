package com.password_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.nio.charset.*;
import java.util.Base64;

public class EntryReadWrite {
	static final int ENTRY_SIZE = 3;
	// Entries in the corresponding text file should be written in the order of Entry Name, Entry Username, and Entry Password with a single space separating them.
	static final int ENTRY_NAME_POS = 0;
	static final int ENTRY_USER_POS = 1;
	static final int ENTRY_PASS_POS = 2;
	
	// A tree map will be used to store entries, where the key will be the entry name and the username and password will be stored as a String array.
	static final int HASH_MAP_USER_POS = 0;
	static final int HASH_MAP_PASS_POS = 1;
	
	// Default path is "C:\Users\Joanna\eclipse-workspace\PasswordManager\PasswordEntries.txt"
	static final String FILE_NAME = "PasswordEntries.txt";
	
	// https://www.baeldung.com/java-cipher-class
	static final String ENCRYPT_KEY = "toEncryptDecrypt";
	static final byte[] ENCRYPT_KEY_BYTES = ENCRYPT_KEY.getBytes();
	static final String SEPARATOR = "-"; //\u00A9
	private Cipher RWCipher;
	private SecretKey cipherKey;
	
	public TreeMap<String, String[]> entries = new TreeMap<>();
	public File entriesFile = new File(FILE_NAME);
	public boolean fileChanged = false;
	
	public void readIn() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		boolean invalid = false;
		
		RWCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipherKey = new SecretKeySpec(ENCRYPT_KEY_BYTES, "AES");
			
		try {
			// Try to make a new file for password entries. If it already exists, read in the entries.
			
			if (!entriesFile.createNewFile()) {
				FileReader reader = new FileReader(FILE_NAME);
				BufferedReader bufferedReader = new BufferedReader(reader);
				String line;
			
				while ((line = bufferedReader.readLine()) != null) {
					String[] splitLine = decrypt(line).split("\u00A9");
				
					// Stop if an entry is of an invalid size.
					if (splitLine.length != ENTRY_SIZE) {
						invalid = true;
						break;
					}
				
					String entryName = splitLine[ENTRY_NAME_POS];
					String[] entryUserPass = {splitLine[ENTRY_USER_POS], splitLine[ENTRY_PASS_POS]};
				
					// Put into the tree map using the entry name as the key.
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
	
	public void addEntry(String entryName, String entryUser, String entryPass) {
		String[] entryUserPass = {entryUser, entryPass};
		entries.put(entryName, entryUserPass);
		
		fileChanged = true;
	}
	
	public void deleteEntry(String entryName) {
		entries.remove(entryName);
		
		fileChanged = true;
	}
	
	public void writeIn() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		try {
			entriesFile.delete();
			entriesFile.createNewFile();
			
			FileWriter writer = new FileWriter(FILE_NAME);
			
			// Loop through each key and write it into the file. The name of the entry, username, and password are separated by a copyright symbol.
			for (String name : entries.keySet()) {
				writer.append(encrypt(name + "\u00A9" + entries.get(name)[HASH_MAP_USER_POS] + "\u00A9" + entries.get(name)[HASH_MAP_PASS_POS]) + "\n");
			}
			
			writer.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// https://www.baeldung.com/java-cipher-class
	private String encrypt(String toEncrypt) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		RWCipher.init(Cipher.ENCRYPT_MODE, cipherKey);
		
		byte[] encrypted = RWCipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));
		byte[] encoded = Base64.getEncoder().encode(encrypted);
		
		return new String(encoded);
	}
	
	private String decrypt(String toDecode) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
		RWCipher.init(Cipher.DECRYPT_MODE, cipherKey);
		
		byte[] decoded = Base64.getDecoder().decode(toDecode.getBytes());
		byte[] decrypted = RWCipher.doFinal(decoded);
		
		return new String(decrypted, StandardCharsets.UTF_8);
	}
	
	public TreeMap<String, String[]> getEntries() {
		return entries;
	}
	
	public boolean isFileChanged() {
		return fileChanged;
	}
}
