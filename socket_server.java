//java server example
import java.io.*;
import java.net.*;
 
public class socket_server
{
    public static void main(String args[])
    {
        ServerSocket s = null;
        Socket conn = null;
         
        try
        {
            //1. creating a server socket - 1st parameter is port number and 2nd is the backlog
            s = new ServerSocket(1339 , 10);
             
            //2. Wait for an incoming connection
            echo("Server socket created.Waiting for connection...");
             
            while(true)
            {
                //get the connection socket
                conn = s.accept();
                 
                //print the hostname and port number of the connection
                echo("Connection received from " + conn.getInetAddress().getHostName() + " : " + conn.getPort());
                 
                //create new thread to handle client
                new client_handler(conn).start();
            }
        }
         
        catch(IOException e)
        {
            System.err.println("IOException");
        }
         
        //5. close the connections and stream
        try
        {
            s.close();
        }
         
        catch(IOException ioException)
        {
            System.err.println("Unable to close. IOexception");
        }
    }
     
    public static void echo(String msg)
    {
        System.out.println(msg);
    }
}
 
class client_handler extends Thread 
{
    private Socket conn;
     
    client_handler(Socket conn) 
    {
        this.conn = conn;
    }
 
    public void run() 
    {
        String line , input = "";
         
        try
        {
            //get socket writing and reading streams
            DataInputStream in = new DataInputStream(conn.getInputStream());
            PrintStream out = new PrintStream(conn.getOutputStream());
 
            //Send welcome message to client
            out.println("Welcome to the Server");
 
            //Now start reading input from client
            while((line = in.readLine()) != null && !line.equals(".")) 
            {
                //reply with the same message, adding some text
                out.println("I got : " + line);
            }
             
            //client disconnected, so close socket
            conn.close();
        } 
       
        catch (IOException e) 
        {
            System.out.println("IOException on socket : " + e);
            e.printStackTrace();
        }
    }
}