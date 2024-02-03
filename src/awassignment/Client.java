package awassignment;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/*
 Write a Java (client) program that monitors a directory. When a new Java proper9es ﬁle
appears in the monitored directory, it should process it as follows:
1) Read the ﬁle into a Map
2) Apply a regular expression paEern ﬁlter for the keys (i.e., remove key/value mappings
where keys do not match a conﬁgurable regular expression paEern).
3) Relay the ﬁltered mappings to a server program
4) Delete the ﬁle
The client program’s main method should accept an argument specifying a conﬁg ﬁle path. The
client conﬁg ﬁle should contain values deﬁning:
• the directory path that will be monitored
• the key ﬁltering paEern that will be applied
• the address of the corresponding server program
• any other value(s) you think should be conﬁgurable
 */


public class Client {
	private static HashMap<String, Properties> monitoredFiles = new HashMap<String, Properties>();
	
	// Syntax: Client <directory> <key filter> <server address> <server port>
	public static void main(String[] args) {
		String dir;
		String keyFilter;
		String serverAddr;
		int serverPort;
		if (args.length == 4) {
			System.out.println("");
			dir = args[0];
			keyFilter = args[1];
			serverAddr = args[2];
			serverPort = Integer.parseInt(args[3]);
			
		} else {
			System.err.println("Required arguments: <watched directory> <key filter> <server address>");
			return;
		}
		System.out.println("---------------------------------------");
		System.out.println("-----Welcome to the Client App---------");
		System.out.println("-Monitoring Directory: " + dir);
		System.out.println("-Connecting to server: " + serverAddr );
		System.out.println("-Filtering Keys with pattern: " + keyFilter);
		System.out.println("---------------------------------------");
		loop(dir, keyFilter, serverAddr, serverPort);

	}
	
	/**
	 * Simple loop to check for new files.
	 * String dir - Directory to watch
	 */
	public static void loop(String dir, String filter, String serverAddress, int serverPort) {
		File folder = new File(dir);
		File[] files;
		while (folder != null) {
			 processFiles(dir, filter, serverAddress, serverPort);
		}

	}
	
	/*
	 * String dir - directory to search
	 * 
	 * Returns a list of .properties files
	 */
	public static void processFiles(String dir, String filter, String serverAddress, int serverPort) {
		File folder = new File(dir);
		File[] files = folder.listFiles();
		int numOfFiles = 0;
		for(int i=0;i<files.length;i++) {
			//Check if this is a new or modified file
			if(files[i].getName().toLowerCase().endsWith(".properties") && !monitoredFiles.containsKey(md5(files[i]))) {
				System.out.println("Found: " + files[i].getName());
				numOfFiles++;
				String checksum = md5(files[i]);
				System.out.println("MD5: " + checksum);
				//Read the properties file and make a hashmap
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(files[i]));
					for(Entry<Object, Object> e : properties.entrySet()) {
			            System.out.println("Entry found: " + e);
			            //TODO: Apply filters
			        }
					//Store the name of the file within the property itself in a placeholder entry
					properties.put("PLACEHOLDER ENTRY FILENAME", files[i].getName()); //Make the key unlikely to stamp on another real one
					//Add the new properties file to the map
					monitoredFiles.put(checksum, properties);
					//TODO: Upload filtered properties to server
					sendFile(properties, serverAddress, serverPort);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
	
	/*
	 * Generates checksum so that file is only updated if contents change.
	 * 
	 * @param file file to generate MD5 checksum
	 * @return
	 */
	public static String md5(File file) {
		byte[] data;
		byte[] hash = null;
		try {
			data = Files.readAllBytes(Paths.get(file.getPath()));
			hash = MessageDigest.getInstance("MD5").digest(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new BigInteger(1, hash).toString(16);
	}
	
	public static void sendFile(Properties properties, String address, int port) {
		try {
			InetAddress host = InetAddress.getLocalHost(); //Assuming this is only going to be used on localhost
	        Socket socket;
	        ObjectOutputStream oos;
	
	
	        socket = new Socket(address, port);
	        oos = new ObjectOutputStream(socket.getOutputStream());
	        System.out.println("Sending request to Socket Server");
	   
	        oos.writeObject(properties);
	    
	        oos.close();
		} catch (Exception e) {
			
		}
	}
	

}
