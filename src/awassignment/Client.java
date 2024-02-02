package awassignment;
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
	// Syntax: Client <directory> <key filter> <server address> <any others>
	public static void main(String[] args) {
		String dir;
		String keyFilter;
		String serverAddr;
		if (args.length == 3) {
			System.out.println("");
			dir = args[0];
			keyFilter = args[1];
			serverAddr = args[2];
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

	}

}
