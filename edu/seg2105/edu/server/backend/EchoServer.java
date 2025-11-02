package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import edu.seg2105.edu.server.ui.ServerConsole;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  
  
  
  
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient (Object msg, ConnectionToClient client){
	  String[] arr = msg.toString().split(" "); // split the message
	 switch(arr[0]) { // arr[0] will be the command #___
	 
	  case "#logoff": // for diconnecting clients
		  System.out.println("Client requesting logoff from " + client + "...");
		  try{client.close(); // try to close the connection
		  } catch(Exception e) {
			  System.out.println("Error closing client: " + e.getMessage());
		  } break;
	  case "#login": // to save client's ID
		  client.setInfo("loginID", arr[1]); // don't need a try/catch since the message for #login was already preformatted/checked
		  System.out.println(arr[1] + " has logged on.");
		  this.sendToAllClients("[" + client.getInfo("loginID") + "] > " + msg);
		 break;
      default:
    	  System.out.println("Message received: \"" + msg + "\" from " + client.getInfo("loginID"));
    	    
	    	this.sendToAllClients("[" + client.getInfo("loginID") + "] > " + msg);
    	    
    	    break;
	  }
  }
    
  
  
  public void handleServerInput(String message) {
	System.out.println("> " + message); // to display in server console
	this.sendToAllClients("SERVER MSG> " + message); // to send to clients
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  // HOOK to display a message upon connection to the server
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected to the server.");
  }
  
  
  // HOOK to display a message upon disconnecting to the server
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println(client.getInfo("loginID") + " has disconnected.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    ServerConsole console = new ServerConsole(sv);
    console.accept(); // in order to accept messages from server console
  }
}
//End of EchoServer class
