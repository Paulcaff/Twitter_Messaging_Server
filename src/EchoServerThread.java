import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

public class EchoServerThread implements Runnable {
   static final String endMessage = ".";
    MyStreamSocket myDataSocket;


   public EchoServerThread(MyStreamSocket myDataSocket) {
      this.myDataSocket = myDataSocket;
   }

   /*
   The following method is used to remove the login details from the text file contining the details
   of all logged in users
   It takes in the users details stored as 1 string and splits it into an array and then to an arraylist
   on the spaces as this denotes a different user
   I then remove the users details from the arraylist and I place the contents of the arraylist to a string
   and re write it to the file again with the logging off user removed
    */
   public boolean LogUserOff(String message){
       try{
           String user = message.substring(6);
           String remainingUsersLoggedIn = "";
           BufferedReader buff = new BufferedReader(new FileReader("UsersLog.txt"));
           String users = buff.readLine();
           System.out.println("aaa"+users);
           ArrayList<String> usersLoggedIn = new ArrayList<String>();
           String[] usersList = users.split(" ");
           usersLoggedIn.addAll(Arrays.asList(usersList));
           usersLoggedIn.remove(user);

           for (int i = 0; i < usersLoggedIn.size(); i++) {
                   System.out.println(usersLoggedIn.get(i));
                   remainingUsersLoggedIn += usersLoggedIn.get(i) + " ";
           }
               System.out.println("string" + remainingUsersLoggedIn);
               FileWriter fileWriterUser = new FileWriter("UsersLog.txt", false);
               BufferedWriter bwUser = new BufferedWriter(fileWriterUser);


               bwUser.write(remainingUsersLoggedIn);
               bwUser.close();
               buff.close();


           return true;

       }catch(Exception e){
           e.printStackTrace();
       }
       return false;
   }
    /*
    The following method checks to see if it can find the login details provided in the textfile
    if the details are fund a boolean true value is returned
     */
    public boolean AlreadyLoggedIn(String login){
        try{
            String userCheck = login.substring(5, login.length() - 5);
            BufferedReader buff = new BufferedReader(new FileReader("UsersLog.txt"));
            String users;
            while((users=buff.readLine())!=null){
                String[] usersList = users.split(" ");
                for (int i = 0; i < usersList.length; i++) {
                    if(usersList[i].equals(userCheck)){
                        return true;
                    }
                }
            }buff.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;

    }
 
   public void run( ) {
      boolean done = false;
      boolean loggedIn = false;
      String message;

      //Arraylists containing the messages of all the users
      ArrayList<String> paulcaff = new ArrayList<String>();
      paulcaff.add("Meeting at 12am to discuss new ideas");
      paulcaff.add("Lunch at 1pm to discuss project");
      paulcaff.add("Interview with new candidate at 4");
      paulcaff.add("Day Done");

       ArrayList<String> petergiven = new ArrayList<String>();
       petergiven.add("Distributed Computing Rocks");
       petergiven.add("Stream Sockets are tricky but cool once you understand them");
       petergiven.add("The project requires protocols for login, add messages,getting all messages  and logging out ");
       petergiven.add("But remember the server and client in reality should be on separate machine");

       ArrayList<String> mikelarry = new ArrayList<String>();
       mikelarry.add("Hi Im Mike Larry");
       mikelarry.add("I am a fictional users created for this project");
       mikelarry.add("I am only created to be another user so you can log in to the system as a concurrent user");
       mikelarry.add("When the process is killed I died and no longer exist");

       ArrayList<String> patjohntom = new ArrayList<String>();
       patjohntom.add("Im patjohntom and am not as deep as mike larry");
       patjohntom.add("I am also a user for this system and without this project I also do not exist");
       patjohntom.add("I am happy to exist and when I die I can be resurrected like lazarus by logging in");
       patjohntom.add("I never die aonly lie dormant until my login details are used again");

      try {
             while(!done) {
                 message = myDataSocket.receiveMessage();

                 if ((message.trim()).equals(endMessage)) {
                     myDataSocket.close();
                     done = true;
                 } else {
                     /*
                     option string takes the 1st 5 characters so the server knows how this message from
                     the client should be processed*/
                     String option = message.substring(0, 5);

                     if (option.equals("Login")) {
                         loggedIn = AlreadyLoggedIn(message);

                         if (loggedIn) {
                             //if details in users logged in text file return 202 error code
                             myDataSocket.sendMessage("202");
                         }
                         //if details match any of these  add the details to the usersLog text file and return code 200
                         else if (message.equals("Loginpaulcaff12345") || message.equals("Loginpetergiven67890") ||
                                 message.equals("Loginmikelarry24680") || message.equals("Loginpatjohntom13579")) {

                                 String user = message.substring(5, message.length() - 5);
                                 FileWriter fileWriterUser = new FileWriter("UsersLog.txt",true);
                                 BufferedWriter bwUser = new BufferedWriter(fileWriterUser);
                                 bwUser.write(user+" ");
                                 bwUser.close();

                                 myDataSocket.sendMessage("200");
                         }
                         //else the username or password is incorrect
                         else {
                             myDataSocket.sendMessage("201");
                         }
                     }

                     /*Send Message
                     I split the string on the hyphen into an array where the second element is the username
                     and the third element is the message saved
                     I used an if else to determine the user and then appended it to the correct arraylist
                     for that user
                     */
                     else if (option.equals("SendM")) {
                         String[] splitString = message.split("-");
                         String username = splitString[1];
                         String messageToBeAdded = splitString[2];

                         if(username.equals("paulcaff")) {
                             paulcaff.add(messageToBeAdded);
                             myDataSocket.sendMessage("300");
                         }
                         else if(username.equals("petergiven")) {
                             petergiven.add(messageToBeAdded);
                             myDataSocket.sendMessage("300");
                         }
                         else if(username.equals("mikelarry")) {
                             mikelarry.add(messageToBeAdded);
                             myDataSocket.sendMessage("300");
                         }
                         else if(username.equals("patjohntom")) {
                             patjohntom.add(messageToBeAdded);
                             myDataSocket.sendMessage("300");
                         }
                         else if(messageToBeAdded.equals("")){
                             myDataSocket.sendMessage("301");
                         }

                     }
                     /*
                     GET MESSAGE
                     I split the string of messages based on the hyphen and sent them back with the reply message
                     prefixed with 400 code
                      */
                     else if (option.equals("GetMs")) {
                         String[] splitString = message.split("-");
                         String username = splitString[1];
                         String reply = "400-";
                         if(username.equals("paulcaff")) {

                             for (int i = 0; i < paulcaff.size(); i++) {
                                 reply = reply + paulcaff.get(i) + "-";
                             }
                             myDataSocket.sendMessage(reply);
                         }

                         if(username.equals("petergiven")) {

                             for (int i = 0; i < petergiven.size(); i++) {
                                 reply = reply + petergiven.get(i) + "-";
                             }
                             myDataSocket.sendMessage(reply);
                         }
                         if(username.equals("mikelarry")) {

                             for (int i = 0; i < mikelarry.size(); i++) {
                                 reply = reply + mikelarry.get(i) + "-";
                             }
                             myDataSocket.sendMessage(reply);
                         }
                         if(username.equals("patjohntom")) {

                             for (int i = 0; i < patjohntom.size(); i++) {
                                 reply = reply + patjohntom.get(i) + "-";
                             }
                             myDataSocket.sendMessage(reply);
                         }
                     }
                     /*code for Logging off
                     this calls the LogUserOff method which removes the users details from the log text file
                     */
                     else if (option.equals(("LogOf"))) {
                         //code for Log off
                         boolean value = LogUserOff(message);

                         if(value) {
                             myDataSocket.sendMessage("500");
                         }
                         else{
                             myDataSocket.sendMessage("501");
                         }
                     }
                 }
             }
        }// end try
        catch (Exception ex) {
           System.out.println("Exception caught in thread: " + ex);
        } // end catch
   } //end run
} //end class 
