package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String id) 
  {
    try 
    {
      client= new ChatClient(host, port, this, id);
   
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        String[] msgArray = message.split(" "); // split string by spaces to acccess parameters
        boolean connected = client.isConnected();
        // check if user is trying to use a command!
        switch (msgArray[0].toLowerCase()) { // if it's a command, the first element of msgArray should be the command!
        	case "#quit":
        		if (connected) {client.sendToServer("#logoff");}  // tell the server that we are disconnecting
        		 // the server recieves the above and 'gracefully' quits the client (disconnect, then quit)
        		client.quit();
        	
        		break;
        		
        	case "#logoff":
        		client.setExpectDisconnect(true);
        		client.sendToServer(message); // tell the server we are disconnecting
        		try{client.closeConnection(); }// close the connection (NOT quit()! since we want to remain running)
        		
        		catch(Exception ignore) {}
        		break;
        			
        	case "#sethost":
        		if (!connected) { // ONLY if the client is not connected
        			client.setHost(msgArray[1]); // param should be element 2
        		}
        		else {
        			System.out.println("Error: Please log off before modifying the host.");
        		} break;
        		
        	case "#setport":
        		if (!connected) { //only if not connected
        			try {client.setPort(Integer.parseInt(msgArray[1]));} // get the integer port #
        			catch (Exception e) {
        				System.out.println("Error: the port number must be an integer."); // for incorrect parameter
        			}
        		} else {System.out.println("Error: Please log off before modifying the port number."); // if already connected
        		} break; 
        	
        	case "#login":
        		if (!connected) {//only if not connected!
        			
        			try {client.openConnection();} // try to open the connection 
        			catch(Exception e) {
        				System.out.println(e);
        				System.out.println("Error: Unable to initiate connection to the server. Verify the parameters, and try again.");
        			}
        		} else {System.out.println("Error: Already logged in!");
        		} break;
        		
        	case "#gethost":
        		System.out.println("Host: " + client.getHost());
        		break;
        	
        	case "#getport":
        		System.out.println("Port: " + client.getPort());
        		break;
        	
        	default: // if it's not a command, run the default operation (handle msg)
        		client.handleMessageFromClientUI(message);
        		break;
        			
        	}
      }
    } 
    catch (Exception ex) 
    {
     
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
	  if (!message.startsWith("[") && !message.startsWith("SERVER MSG>")) {
		  System.out.print("> ");}
	  System.out.println(message);
	  
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to. args[1] is the port number
   */
  public static void main(String[] args) 
  {
	  
	  String id = null;
		try {
			id = args[0]; // get the ID
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ERROR - No login ID specifid. Connection aborted.");
			System.exit(0);
		}
	    
		
		
    String host = "";
    int port = DEFAULT_PORT; // set the port to default

	try {
	port = Integer.parseInt(args[2]); // if we're given an argument, set it as the port
	System.out.println("Proceeding with port number " + port);
	}
	catch (Throwable t){ // if the parsing fails, assumed the port was invalid and use default
		//ignore
	}

    
  
	
    try
    {
      host = args[1];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    ClientConsole chat= new ClientConsole(host, port, id);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
