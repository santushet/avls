/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package avlslistening;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.nio.*;

//import java.net.SocketAddress;
//import java.nio.channels.Channel;
//import java.nio.channels.DatagramChannel;
/**
 *
 * @author sai
 */
public class DataListener extends Thread {

    public static DataQueue queue = null;
    String strTempMsg = "";
    boolean tflag = false;
    int fInsert = 0;

    private InetAddress hostAddress;
    private int port;
    int portNum;
    private ServerSocketChannel serverChannel;
    //private DatagramChannel udpserver;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    public static ArrayList<SocketChannel> newSocketList;
    public static HashMap<String, SocketChannel> socketList;
    private ServerSocketChannel serverSocketChannel;

    public DataListener(int Port) {
        queue = DataQueue.getInstance();
        this.port = Port;
        this.selector = this.initSelector();
    }

    public DataListener() {
    }

    @Override
    @SuppressWarnings("SleepWhileHoldingLock")
    public void run() {
        newSocketList = new ArrayList<>();
        socketList = new HashMap<>();

        while (true) {
            try {
                this.selector.select();
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();

                    selectedKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
                Thread.sleep(1);
            } catch (Exception e) {

                //  System.out.println("EXCEPTION IN RUN " + e);
                Main.gc();
            }
        }
    }

    private void accept(SelectionKey key) {
        SocketChannel socketChannel = null;
        try {
            serverSocketChannel = (ServerSocketChannel) key.channel();

            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.socket().setReceiveBufferSize(8192);

            socketChannel.register(this.selector, SelectionKey.OP_READ);

            if (!newSocketList.contains(socketChannel)) {
                newSocketList.add(socketChannel);
            }
        } catch (Exception e) {
            // newly added by sai
            try {
                socketChannel.socket().setSoLinger(true, 0);
                socketChannel.close();
                key.cancel();
            } catch (Exception ee) {
            }
            Main.gc();
            //  System.out.println("Inside accept method " + e);
        }
    }

    private Selector initSelector() {
        Selector socketSelector = null;
        try {
            socketSelector = SelectorProvider.provider().openSelector();
            // System.out.println("Create a new server socket channel ");
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);

            serverChannel.socket().bind(isa);
            //serverSocket.bind(new InetSocketAddress(InetAddress.getByName(LocalSettings.getInstance().getServerSettings().getServerPublicIP()), port));
            hostAddress = InetAddress.getLocalHost();
            // System.out.println(hostAddress);
            //System.out.println(InetAddress.getByName("Fleet/138.91.32.145"));
            // this.hostAddress.getLocalHost()
            serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

            //Working on UDP

            /* SocketAddress localport = new InetSocketAddress(5000);
             udpserver = DatagramChannel.open();
             udpserver.socket().bind(localport);

             udpserver.configureBlocking(false);
             udpserver.register(selector, SelectionKey.OP_READ);

             System.out.println("UDP Socket");*/
        } catch (Exception e) {
            e.printStackTrace();

            try {
                serverChannel.close();
            } catch (Exception ee) {
            }

            //Main.gc();
            // System.out.println("DEBUG 1  " + e);
        }
        return socketSelector;
    }

    private void read(SelectionKey key) {

        //TCP socket reading
        try {

            SocketChannel socketChannel = (SocketChannel) key.channel();
            portNum = socketChannel.socket().getLocalPort();

            this.readBuffer.clear();
            int numRead;
            try {

                numRead = socketChannel.read(this.readBuffer);

            } catch (Exception e1) {
                //System.out.println("Exception in read method1  " + e1);
                socketChannel.socket().setSoLinger(true, 0);

                socketChannel.close();
                key.cancel();
                newSocketList.remove(socketChannel);
                return;
            }

            //UDP Socket Reading
/*
             try
             {
             Channel c = (Channel) key.channel();
             if(c == udpserver)
             {
             SocketAddress clientAddress = udpserver.receive(this.readBuffer);
             System.out.println(readBuffer.toString());



             }

             }
             catch(Exception e)
             {}
             */
            if (numRead == -1) {
                try {
                    // key.channel().close();

                    if (newSocketList.contains(socketChannel)) {
                        socketChannel.socket().setSoLinger(true, 0);
                        socketChannel.close();
                        newSocketList.remove(socketChannel);
                    }
                    key.cancel();

                } catch (Exception ee) {
                    //System.out.println("numRead Exception  " + ee);
                }
            } else {
                if (readBuffer != null) {
                    readBuffer.flip();
                    byte[] array = new byte[10000];
                    while (readBuffer.hasRemaining()) {
                        String stOutput = "";
                        String[] packets;
                        String strSimNo = "";
                        try {
                            int n = readBuffer.remaining();
                            readBuffer.get(array, 0, n);
                            stOutput = new String(array, 0, n);
                            // System.out.println("first " + stOutput);
                        } catch (Exception ee) {
// newly added by sai
                            // key.channel().close();

                            if (newSocketList.contains(socketChannel)) {
                                socketChannel.socket().setSoLinger(true, 0);
                                socketChannel.close();
                                newSocketList.remove(socketChannel);
                            }
                            key.cancel();
                            // System.out.println("Array Exception " + ee);
                        }
                        stOutput = stOutput.trim();
                        if (stOutput.length() > 1) {
                            LogMessage.filecreation(stOutput, portNum);

                            //System.out.println(queue.qe.size());
                            if (stOutput.contains("VTS") || stOutput.contains("MTS") ) {
                                packets = stOutput.split(",");
                                if (packets.length >= 3) {
                                    queue.storeData(stOutput);
                                    strSimNo = packets[3];
                                    if (socketChannel.isConnected()) {
                                        if (strSimNo.length() >= 10) {
                                            socketList.put(strSimNo, socketChannel);
                                        } else {

                                        }
                                    }
                                }
                            } else {
                                
                                stOutput = stOutput.replace("|", ",");
                                packets = stOutput.split(",");
                                //System.out.println("Packets length = "+packets[0]);
                                //System.out.println("Packets length = "+packets[1]);
                                
                                if (packets[0].equals("^TMALT")) {
                                    //^TMALT|354678456723764|3|2|1|12.59675|77.56789|123456|030414|1.2|34|#
                                    //$TMALTACK#                        
                                    strSimNo = packets[1];
                                    queue.storeData(stOutput);
                                    socketList.put(strSimNo, socketChannel);
                                    ByteBuffer buf = ByteBuffer.allocate(250);
                                    String mess = "$TMALTACK#";
                                    buf.clear();
                                    buf.put(mess.getBytes());
                                    buf.flip();

                                    while (buf.hasRemaining()) {
                                        socketChannel.write(buf);
                                    }
                                } else if (packets[0].equals("^TMSRT")) {

                                } else if (packets[0].equals("^TMPER")) {
                                    //^TMPER|354678456723764|1|12.59675|77.56789|123456|030414|2.3|34|1|0|0|0.015|3.9|12.0|23.4|23.4|1|1|0|#
                                        strSimNo = packets[1];
                                        queue.storeData(stOutput);
                                        socketList.put(strSimNo, socketChannel);
                                }
                            }
//
                            //SocketChannel strsoc = socketList.get(strSimNo);
                        }
                    }
                } else {
                    //  System.out.println("UnIdentified Format >>>>>>>>>>>>>>");

                }
            }

        } catch (Exception e) {
            //System.out.println("Inside read method2 " + e);

            //Main.gc();
            return;
        }
    }

}
