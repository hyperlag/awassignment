package awassignment;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.io.OutputStream;

/**
 * Server Application.
 * 
 * Listens for client connections then write Properties files to disk when asked.
 */
public class Server {
    private static ServerSocket server;
    
    // Internal key used to store the filename of a properties file in its
    // associated Properties object.
    // It has a delim and a hash in it to be 200% sure it does not collide with real data.
    private static final String filenameKey = "PLACEHOLDER ENTRY FILENAME : bb1b365b692f1d2fb5400121313981ea";

    /**
     * Server application main.
     * 
     * @param args <output directory> <listen port> <anthing else>
     */
    public static void main(String[] args) {
        String outDir;
        int port;
        ServerSocket server;
        if (args.length == 2) {
            System.out.println("");
            outDir = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            System.err.println("Required arguments: <output directory> <listen port>");
            return;
        }
        System.out.println("---------------------------------------");
        System.out.println("-----Welcome to the Server App---------");
        System.out.println("-Output Directory: " + outDir);
        connect(port);
        System.out.println("-Listening on localhost:" + port);
        System.out.println("---------------------------------------");

        loop(outDir);

        // Done
        disconnect();
    }

    private static void loop(String outDir) {
        try {
            while (true) {
                Socket socket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                
                // Read the Properies object passed to server
                Properties properties = (Properties) ois.readObject();
                
                // Read the filename stored in the interim filename key
                String filename = properties.getProperty(filenameKey);
                
                //Print some console information for the user
                System.out.println("File Update received: " + filename);
                
                // Remove the placeholder/interim key before writing out the file
                properties.remove(filenameKey);

                //Write the file to disk
                writeFile(filename, outDir, properties);

                // Clean up the stream and socket
                ois.close();
                socket.close();
            }
        } catch (Exception e) {
            // If something failed here, it is likely that the client has some sort of corruption or issues 
            // sending the Properties object. Restarting the client should force it to try again.
            System.err.println("Error recieving properties file. Please restart cleint and try again. " + e.getMessage());

        }
    }
    
    /**
     * Method to write the server file out to disk.
     * 
     * @param filename Filename to write
     * @param dir Output directory to place file.
     * @param properties Properties file to write.
     */
    private static void writeFile(String filename, String dir, Properties properties) {
        try {
            // Setup an OutputStream for the file.
            OutputStream output = new FileOutputStream(dir + "/" + filename);
            
            // Print some console info for the user
            System.out.println("Writing out " + dir + "/" + filename);
            System.out.println("---------------------------------------");
            
            // Write the object to a file using the stream 
            properties.store(output, null);
            
            // Close the output stream
            output.close();

        } catch (Exception e) {
            // If something failed here, it is likely that the client has some sort of corruption or issues 
            // sending the Properties object. Restarting the client should force it to try again.
            System.err.println("Error recieving properties file. Please restart cleint and try again. " + e.getMessage());
        }
    }

    /**
     * Create a new ServerSocket on the supplied listen port.
     * 
     * This is broken out to make error tandling a little cleaner.
     * 
     * @param listenPort Port to listen for client connections on.
     */
    private static void connect(int listenPort) {
        try {
            server = new ServerSocket(listenPort);
        } catch (Exception e) {
            System.err.println("Error starting socket. Please restart server " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Closes the ServerSocket and handles errors associated with that.
     */
    private static void disconnect() {
        try {
            server.close();
        } catch (Exception e) {
            System.err.println("Error shutting down socket." + e.getMessage());
        }
    }

}
