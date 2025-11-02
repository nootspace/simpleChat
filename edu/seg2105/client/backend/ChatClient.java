// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private boolean expectDisconnect = false; // to ensure we exit properly if the server has a shutdown
  private String loginID; // to store the login ID for each client
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String id) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = id; 
    openConnection();
  }

  
  //Instance methods ************************************************
   
  public void setLoginID(String id) {
	  this.loginID = id;
  }
  
  protected void connectionEstablished() {
	  expectDisconnect = false; // so that when we connect, by default we aren't expecting a disconnection
	  try {
		  sendToServer("#login "+ this.loginID);
	  } catch (Exception E) {
		  clientUI.display("Failed to initialize the login. Quitting...");
		  quit();
	  }
  }
  
  public void setExpectDisconnect(boolean b) { // to set the flag in order to properly exit when unintentional disconnection occurs
	  expectDisconnect = b; 
  }
  
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  
  public void handleMessageFromServer(Object msg) {
	  String s = String.valueOf(msg);
	
	  if (s.equals("SERVER MSG> You are being logged off.")) {
	      expectDisconnect = true; // we EXPECT to be disconnected from the server, so don't terminate
	  }
	
	  if (s.equals("SERVER MSG> Server is shutting down.")) {
	      // shutdown is always fatal, so we must exit the client
	      clientUI.display(s);
	      System.exit(0);
    }

    clientUI.display(s);
	}
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  // implementing hook method to notify client when the connection to server has been closed
  protected void connectionClosed() {
	  clientUI.display("Connection closed by the server: You have been logged out.");
  }

// implementing hook method to disconnect client if theres a connection error to the server (i.e. shutdown)
  protected void connectionException(Exception exception) {
	  if (expectDisconnect) {
	    connectionClosed();   // treat like a normal close
	    expectDisconnect = false;
	    return;	   
	  }
	  clientUI.display("Server connection lost. Exiting...");
	  System.exit(0);   
	}



}

//End of ChatClient class
