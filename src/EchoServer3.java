import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * This module contains the application logic of an echo server
 * which uses a stream-mode socket for interprocess communication.
 * Unlike EchoServer2, this server services clients concurrently.
 * A command-line argument is required to specify the server port.
 * @author M. L. Liu
 */

public class EchoServer3 {
   public static void main(String[] args) throws IOException {

      System.setProperty("javax.net.ssl.keyStore","TMPStore.store");
      System.setProperty("javax.net.ssl.keyStorePassword","password");
      /*
      I created a text file the stores the users logged in while the server is running
      Everytime the server is started, it clears the contents of the file as no one can
      be logged in if the server is starting up fresh
      it clears it by entering an empty string that overwrites everything
       */

      FileWriter fileWriter = null;
      BufferedWriter bw = null;
      try {
         fileWriter = new FileWriter("UsersLog.txt",false);
         bw = new BufferedWriter(fileWriter);
         bw.write("");
         bw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }


      int serverPort = 7;    // default port

      if (args.length == 1 )
         serverPort = Integer.parseInt(args[0]);       
      try {
         // instantiates a stream socket for accepting
         //   connections
   	   ServerSocket myConnectionSocket =(SSLServerSocketFactory.getDefault()).createServerSocket(serverPort);
/**/     System.out.println("Echo server ready.");  
         while (true) {  // forever loop
            // wait to accept a connection

/**/        System.out.println("Waiting for a connection.");
            MyStreamSocket myDataSocket = new MyStreamSocket
                (myConnectionSocket.accept( ));
/**/        System.out.println("connection accepted");
            // Start a thread to handle this client's session
            Thread theThread = 
               new Thread(new EchoServerThread(myDataSocket));
            theThread.start();
            // and go on to the next client
            } //end while forever
       } // end try
	    catch (Exception ex) {
          ex.printStackTrace( );
	    } // end catch
   } //end main
} // end class
