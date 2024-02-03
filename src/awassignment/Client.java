package awassignment;

import java.io.File;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Client Application.
 * 
 * I put everything into this class as static functions to contain it and make
 * it easy to review. In the real world, I would spend time making reusable
 * functionality in more generic classes.
 * 
 */
public class Client {
    // HashMap of Properties (also implements Map), keyed on the MD5 checksum of the
    // file
    private static HashMap<String, Properties> monitoredFiles = new HashMap<String, Properties>();

    // Internal key used to store the filename of a properties file in its
    // associated Properties object.
    // It has a delim and a hash in it to be 200% sure it does not collide with real data.
    private static final String filenameKey = "PLACEHOLDER ENTRY FILENAME : bb1b365b692f1d2fb5400121313981ea";
    
    // If false, the file will not be deleted on transmit
    private static Boolean deleteAfterXmit= true;

    /**
     * Main class for the Client application.
     * 
     * The optional <delete after> argument allows you to keep the source file upon transmission if desired.
     * Set this to false to retain.
     * 
     * @param args <directory> <key filter> <server address> <server port> <delete after>
     */
    public static void main(String[] args) {
        String dir;
        String keyFilter;
        String serverAddr;
        int serverPort;
        
        // If the optional delete argument is supplied, parse it
        if (args.length == 5) {
            deleteAfterXmit = Boolean.parseBoolean(args[4]);
        }

        // Only run if the correct number of arguments were supplied
        if (args.length >= 4) {
            System.out.println("");
            dir = args[0];
            keyFilter = args[1];
            serverAddr = args[2];
            serverPort = Integer.parseInt(args[3]);

        } else { // Print usage statement if argument check fails
            System.err.println("Required arguments: <watched directory> <key filter> <server address> <server port> <delete after (optional)>");
            return;
        }

        // Startup message
        System.out.println("---------------------------------------");
        System.out.println("-----Welcome to the Client App---------");
        System.out.println("-Monitoring Directory: " + dir);
        System.out.println("-Connecting to server: " + serverAddr);
        System.out.println("-Filtering Keys with pattern: " + keyFilter);
        System.out.println("---------------------------------------");

        loop(dir, keyFilter, serverAddr, serverPort);

    }

    /**
     * Function to loop forever and call out to the function that processes the file
     * changes.
     * 
     * @param dir           Input directory to watch for new files or changes
     * @param filter        Regex filter to apply to properties file key
     * @param serverAddress Address of the server
     * @param serverPort    Server port
     */
    public static void loop(String dir, String filter, String serverAddress, int serverPort) {
        File folder = new File(dir);
        while (folder != null) {
            processFiles(dir, filter, serverAddress, serverPort);
        }

    }

    /**
     * Process the files in the directory.
     * 
     * @param dir           Input directory to watch for new files or changes
     * @param filter        Regex filter to apply to properties file key
     * @param serverAddress Address of the server
     * @param serverPort    Server port
     */
    public static void processFiles(String dir, String filter, String serverAddress, int serverPort) {
        File folder = new File(dir);

        // Make an array of files in the directory
        File[] files = folder.listFiles();

        // Check every file in the directory
        for (int i = 0; i < files.length; i++) {
            // Condition to check if this file is both a new file and if it is a
            // ".properties" file
            if (files[i].getName().toLowerCase().endsWith(".properties")
                    && !monitoredFiles.containsKey(md5(files[i]))) {
                System.out.println("---------------------------------------");
                System.out.println("Found: " + files[i].getName());
                // Generate checksum of the file to ensure it is new.
                String checksum = md5(files[i]);
                System.out.println("MD5: " + checksum);

                // Make a new Properties object to store the new file
                Properties properties = new Properties();
                try {
                    // Load all the properties from the file into the Properties object
                    properties.load(new FileInputStream(files[i]));

                    // Add an interim property inside the Property object to contain the filename.
                    // This gets removed by the server on write-out
                    properties.put(filenameKey, files[i].getName());

                    // Add the new properties file to the HashMap
                    monitoredFiles.put(checksum, properties);

                    // Remove any keys that do not match the supplied filter (excluding the internal
                    // filename key)
                    applyFilter(checksum, filter);

                    // Send the filtered properties file to the server
                    sendFile(monitoredFiles.get(checksum), serverAddress, serverPort);
                    
                    // Only delete if the option is set
                    if(deleteAfterXmit) {
                        files[i].delete();
                    }
                } catch (Exception e) {
                    System.err.println("Error processing files. Please restart client " + e.getMessage());
                    System.exit(0);
                }

            }
        }
    }

    /**
     * This method calls up the properties file from the HashMap and removes entries
     * that do not have a regex match with the supplied filter.
     * 
     * @param md5key MD5 checksum key for the properties file to be filtered
     * @param filter Regex to apply
     */
    private static void applyFilter(String md5key, String filter) {
        // Get the unfiltered properties object
        Properties properties = monitoredFiles.get(md5key);

        // Check every key in the key set
        for (Object key : properties.keySet()) {
            // Apply filter if the key is both not the internal filename key and if it does
            // not match the filter
            if (!((String) key).contains(filenameKey) && !((String) key).matches(filter)) {
                // Print some user information
                System.out.println("Applying filter " + filter + " to: " + (String) key);

                // Remove the entry
                monitoredFiles.get(md5key).remove((String) key);
            }

        }
    }

    /*
     * Generates checksum so that file is only updated if contents change.
     * 
     * @param file file to generate MD5 checksum
     * 
     * @return MD5 checksum as a string
     */
    public static String md5(File file) {
        byte[] data;
        byte[] hash = null;
        try {
            // Load the whole file into a byte array
            data = Files.readAllBytes(Paths.get(file.getPath()));
            // Use the MessageDigest class to digest that previous byte array into a
            // checksum hash
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (Exception e) { // In the real world, this would be part of a larger error management strategy
            System.err.println("Error generating MD5. Please restart client. " + e.getMessage());
            // Shutting down client to avoid corrupt data in the map. Restarting will resync
            // the data.
            System.exit(0);
        }

        // Turn the hash into String and return it.
        return new BigInteger(1, hash).toString(16);
    }

    /**
     * This method opens up a socket and sends the Properties file passed to it to
     * the server specified.
     * 
     * @param properties Properties file to send to the server (with special
     *                   internal filename entry).
     * @param address    Address of the server
     * @param port       Server port.
     */
    private static void sendFile(Properties properties, String address, int port) {
        try {
            // Make a new Socket with the server info supplied
            Socket socket  = new Socket(address, port);
            // Setup an output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            //Print some console info for the user
            System.out.println("Sending request to Socket Server at " + address + ":" + port);

            // Send the properties file
            oos.writeObject(properties);

            //Clean up
            oos.close();
            socket.close();
        } catch (Exception e) {
            // Error message on failure
            System.err.println("Error communicating with server at " + address + ":" + port
                    + ". Please ensure it is running and restart the client.");
            // If this fails we just shut down. Restarting the client will re-synchronize
            System.exit(0);
        }
    }

}
