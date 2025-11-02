package edu.seg2105.edu.server.ui;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;
import ocsf.server.ConnectionToClient;

public class ServerConsole implements ChatIF{
	
	EchoServer server;
	
	Scanner fromConsole; // scanner to read from the console

	
    public ServerConsole(EchoServer server) { // we're just connecting to the main server, so we don't have to specify host/port (just use the echoserver object!)
    	
    	this.server = server;
    	
    	fromConsole = new Scanner(System.in); // scanner to read from the console
    }

    
	@Override
	public void display(String message) {
		// TODO Auto-generated method stub
		System.out.println("SERVER MSG> " + message);
	}

	public void accept() {
        try {
            String message;
            while (true) {
            	message = fromConsole.nextLine();
            	String[] msgArray = message.split(" "); // to access command parameters

            	switch (msgArray[0].toLowerCase()) {
            	
            	case "#quit":
            		server.sendToAllClients("SERVER MSG> Server is shutting down.");
        		    try { server.close(); } catch (IOException ignore) {}
        		    System.out.println("Server quitting...");
        		    System.exit(0);
        		    break;
            		
            	case "#stop":
            		server.stopListening(); // already prints
            		break;
            	
            	case "#close":
            		server.sendToAllClients("SERVER MSG> You are being logged off.");
            	    try {
            	        server.close();  // already stops listening + disconnects clients
            	        System.out.println("The server has shut down.");
            	    } catch (IOException e) {
            	        System.out.println("Error closing server: " + e);
            	    }
            	    break;
            	
            	case "#setport":
            		if (server.isListening()) {
            			System.out.println("Error: Close the server before attempting to modify the port.");
            		} else {
            			try { server.setPort(Integer.parseInt(msgArray[1]));
            			} catch (Exception e) {
            				System.out.println("Error: Invalid port number provided.");
            			}
            		} break;
            	case "#start":
            		if (!server.isListening()) {
            			server.listen();
            		} else { System.out.println("Error: Server is already listening. Use #stop to stop listening.");
            		} break;
            	
            	case "#getport":
            		System.out.println("Port: " + server.getPort());
            		break;
            		
            	default: 
            	server.handleServerInput(message);
            	break;
            	}
            }
       } catch (Exception e ) {
            	System.out.println("Unexpected error while reading from console!"); }
            
	    }
	
	
	

}
