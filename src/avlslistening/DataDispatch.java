/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package avlslistening;

import DB.DBManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sai
 */
public class DataDispatch extends Thread {

    Connection con_avls = null;
    

    public DataDispatch() {
       
        try {
            
            DataDispatch.sleep(60000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataDispatch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        
        while (true) {
            try {
                con_avls = DBManager.getConnection();
                CallableStatement getvalues = con_avls.prepareCall("{ call f_avls_text_message_request_new()}");
                               
                getvalues.execute();
                String values ="";
                 ResultSet rs1 = getvalues.getResultSet();
          while (rs1.next()) {
               values = rs1.getString("message");
          }
          rs1.close();
                                               
                if(values.length()>1)
                {
                                    
                    String[] message = values.split("-");
                    String simno = message[0];
                    String mess = message[1];
                   
                    LogMessage.errorfilecreation("message to send======"+mess+"Sim  to send message "+simno);
                   
                    SocketChannel strsoc = DataListener.socketList.get(simno);
                    
                    System.out.println("Socket is open ?  "+strsoc.isOpen());
                    
                    ByteBuffer buf = ByteBuffer.allocate(1000);
                    buf.clear();
                    buf.put(mess.getBytes());
                    buf.flip();
                    
                    while (buf.hasRemaining()) {
                        strsoc.write(buf);
                    }
                      Thread.sleep(500);
                
                }

                Thread.sleep(30000);
                getvalues.close();
            } catch (Exception se) {
//                try {
//                    LogMessage.errorfilecreation("Error in dispatch " + se.getStackTrace());
//                } catch (IOException ex) {
//                }
                
            } finally {
                
                DBManager.freeConnection(con_avls);
            }
        }


    }

    public static void sendToVMU(String data, String simno) {
        try {
            int chksum = CRC16.CalculateCRC((new StringBuilder()).append("06").append(data).append((char)187).toString());
            String CHKSUM;
            for (CHKSUM = Integer.toHexString(chksum).toUpperCase(); CHKSUM.length() < 4; CHKSUM = (new StringBuilder()).append("0").append(CHKSUM).toString()) {
            }

            String mess = "VTS06 " + (new StringBuilder()).append(data).append((char)187).toString() + CHKSUM;

            SocketChannel strsoc = DataListener.socketList.get(simno);
           
           
            ByteBuffer buf = ByteBuffer.allocate(mess.getBytes().length);
            buf.clear();
            buf.put(mess.getBytes());
            buf.flip();
            while (buf.hasRemaining()) {
                strsoc.write(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error inside the sendToVMU"+e.getMessage());
        }

    }
}
