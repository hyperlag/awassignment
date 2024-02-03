# awassignment

Hello and welcome to my attempt at a simple implimentation of the assignment that was handed to me.

# Overview
The server application must be run first. It will listen on the supplied port for Client connections and write 
properities files to the supplied ouotput directory upon recipt.

Once the server is running, the Client application can be started. It will read all files in the directory supplied
in the execution arguments. If any of those files are .properties files, they will be filtered based on the 
supplied regex and transmit them to the server application. Upon transmission, the original file is deleted (if 
the <delete after> option is not set to false). 

The Client application will continue to look for new files. In the case where <delete after> has been set to false, 
the Client will periodically check MD5 checksums and transmit updates if any exisiting files change.

If you wish to directly run the jar files I have supplied and you happen to be running bash, two example run scripts have been provided.

# Running the Server Application (via the provided jar file)
## An example set of arguments has been supplied here:
>Server.sh

## Any system running bash and java on path can simply run:
>./Server.sh

This will start the server application with the output directory set to "out" and the listen port "1337".

## This will execut the command:
>$java -jar Server.jar out 1337

Arguments are: <output directory> and <listen port>


# Running the Client Application (via the provided jar file) 
## An example set of arguments has been supplied here:
>Client.sh

## Any system running bash and java on path can simply run:
>./Client.sh

This will start the Client application watching the directory "input" for new files. It will also filter only 
Properties with keys that end in "Delim". The default server address is assumed to be localhost and the port 
is set ot 1337. The optional delete option has been set to true, but it can also be left out entirely. 
Setting this to false will cause the Client to update on change but not delete the file.

## This will execut the command:
>$java -jar Client.jar input '.*Delim$' localhost 1337 true

Arguments are: <directory> <key filter> <server address> <server port> <delete after (optional. Default true)>


# Building 
For ease of review, nothing special is require to build the applications from scratch.

## Client
To manually build the Client perform the following command in the project directory:
>javac src/awassignment/Client.java

Once built, it can be run with the following command:
>java -cp src/ awassignment.Client input '.*Delim$' localhost 1337


## Server
To manually build the Server perform the following command in the project directory:
>javac src/awassignment/Server.java

Once built, it can be run with the following command:
>java -cp src/ awassignment.Server out 1337

