
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerListener {

    public static void main(String[] args) {
        new ServerListener().startServer();
    }

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors
                .newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(1339);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool
                                .submit(new ClientTask(clientSocket));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Got a client !");
            try {
            	int red = -1;
            	byte[] buffer = new byte[5*1024]; // a read buffer of 5KiB
            	byte[] redData;
            	StringBuilder clientData = new StringBuilder();
            	String redDataText;
            	while ((red = clientSocket.getInputStream().read(buffer)) > -1) {
            	    redData = new byte[red];
            	    System.arraycopy(buffer, 0, redData, 0, red);
            	    redDataText = new String(redData,"UTF-8"); // assumption that client sends data UTF-8 encoded
            	    System.out.println("message part recieved:" + redDataText); 
            	    clientData.append(redDataText);
            	}
            	System.out.println("Data From Client :" + clientData.toString());

            /* Send Data To Client */

                //Code

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}