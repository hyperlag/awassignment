package awassignment;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/*
 * Also, write a corresponding server program that accepts messages from clients. It should be
capable of handling messages sent by mul9ple clients simultaneously.
Upon receipt of a message from a client, the server should use the message to reconstruct a
ﬁltered proper9es ﬁle and write it to disk, using the original ﬁlename.
The server program’s main method should accept an argument specifying a conﬁg ﬁle path.
The server conﬁg ﬁle should contain values deﬁning:
• the location of the directory to which to write the ﬁles
• what port to listen on
• any other value(s) you think should be conﬁgurable
 * */
public class Server {
	private static ServerSocket server;
	private static final String filenameKey = "PLACEHOLDER ENTRY FILENAME";
	//Syntax: Server <output directory> <listen port> <anthing else>
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
		System.out.println("-Listening on localhost:" + port );
		System.out.println("---------------------------------------");
		
		loop();
		
		//Done
		disconnect();
	}
	
	private static void loop() {
		try {
			while (true) {
	            Socket socket = server.accept();
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            Properties properties = (Properties) ois.readObject();
	            System.out.println("File Update received: " + properties.getProperty(filenameKey));
	            properties.remove(filenameKey);//Remove the placeholder before writing out the file
	            //TODO write out the file
	            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	
	            ois.close();
	            //oos.close();
	            socket.close();
			}
		} catch (Exception e) {
			
		}
	}
	
	private static void connect(int listenPort) {
		try { 
			server = new ServerSocket(listenPort);
		} catch (Exception e) {
			System.err.println("Error starting socket " + e.getMessage());
		}
	}

	private static void disconnect() {
		try {
			server.close();
		} catch (Exception e) {
			System.err.println("Error shutting down socker " + e.getMessage());
		}
	}
	
	
}
