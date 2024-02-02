package awassignment;
import java.net.ServerSocket;

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
		

		
		//Done
		disconnect();
	}
	
	private static void loop() {
		while (true) {
			
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
