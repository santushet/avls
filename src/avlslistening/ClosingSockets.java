/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avlslistening;

/**
 *
 * @author sai
 */

import DB.DBManager;
import java.io.IOException;
import java.nio.channels.SocketChannel;


public class ClosingSockets extends Thread {
     static int dbtimer=0;
     DataProcessor dp;
    public ClosingSockets() throws IOException
    {
       dp = new DataProcessor();
       dp.setPriority(MAX_PRIORITY);
       DBManager.setDBPool();
    }
     @Override
    public void run() {
        //System.out.println("INside closing sockets ");
        dp.start();
        dp.setPriority(MAX_PRIORITY);
        while (true) {
            SocketChannel socketChannel = null;
             try{
                 if (dp.isAlive())
                 {
                    Thread.State td = dp.getState();
                     if(dp.isDaemon())
                     {
                        System.out.println("Data Processor is a daemon thread");
                     }
                    if(dp.isInterrupted())
                    {
                        System.out.println("Data Processor is interrupted");
                        dp = new DataProcessor();
                        dp.start();
                    }
                 }
                 else
                 {
                     System.out.println("Data Processor is not alive");
                     dp.start();
                 }
                // System.out.println("Started closing Sockets- Socket List     "+DataListener.newSocketList.size() + " Hash Map size   "+DataListener.socketList.size());

                    Main.gc();
                    if (DataListener.newSocketList.size()>2){

                        //System.out.println(DataListener.socketList.toString());

                    for(int i=0;i<DataListener.newSocketList.size();i++ )
                    {
                         socketChannel = DataListener.newSocketList.get(i);
                        if (!DataListener.socketList.containsValue(socketChannel))
                        {
                            if(socketChannel.isConnected())
                            {
                                socketChannel.socket().setSoLinger(true, 0);
                                socketChannel.close();
                            }
                            DataListener.newSocketList.remove(socketChannel);
                        }

                    }
                    dbtimer++;
                 }
                  Thread.sleep(300000);

                  if (dbtimer >=12 )
                  {
                       dbtimer =0;
                      // DBManager.freeallconnections();
                      // DBManager.setDBPool();

                 }

                }catch(Exception ee){
                    DataListener.newSocketList.remove(socketChannel);
                  //  System.out.println("Error in closing the sockets "+ee.getMessage());

                }
             
             finally{}

        }
    }




}
