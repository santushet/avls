/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package avlslistening;

import DB.DBManager;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sai
 */
public class DataProcessor extends Thread {

    
    DataQueue queue;

    public DataProcessor() {
        System.out.println("Intiated the Data Processor");
        queue = DataQueue.getInstance();

    }

    @Override
    @SuppressWarnings("SleepWhileHoldingLock")
    public void run() {
        int corePoolSize = 1000;
        int maxPoolSize = 3000;
        long keepAliveTime = 3000;
        ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
        //System.out.println("Data Processor started ");
        while (true) {
            try {
                //Reading from the queue
                //System.out.println("Queue Size  " + queue.qe.size());
                while (queue.qe.size() > 0) {
                    //System.out.println("Queue Size  " + queue.qe.size());

                    if (DBManager.getStatus()) {
                        String tempstring = queue.getData();//DataListener.queue.getData();
                       // tempstring = tempstring.replaceAll("^","");
                       // System.out.println("String Received at start"+ tempstring);
                        //Process Data
                        if (tempstring.contains("+++")) {
                            tempstring = tempstring.replaceAll("\\+++", "");
                        }
                        if (!(tempstring.contains("VTS") || tempstring.contains("MTS") || tempstring.contains("GET") || tempstring.contains("^TMPER") || tempstring.contains("^TMALT"))) {
                           // System.out.println("Not a valid String");
                            LogMessage.errorfilecreation("Not a Valid Packet" + tempstring);
                        } 
                        else {
                            //System.out.println("Inside data processor");

                            Runnable dl = new DataProcesssorThread(tempstring);
                            //executor.submit(dl);
                            executor.execute(dl);
                            //  Thread t =new Thread(new DataProcesssorThread(tempstring));
                            //  t.start();

                        }
                    }
                    Thread.sleep(10);
                }
                Thread.sleep(100);
            } catch (Exception et) {

                try {
                    LogMessage.errorfilecreation(et.getMessage());
                    Main.gc();

                } catch (Exception ie) {
                }
                //return;
            } finally {
            }
        }
    }
}

class DataProcesssorThread implements Runnable {

    String tempstring;
    DataListener dl;
    DataPusher dp;

    DataProcesssorThread(String data) throws IOException {

        this.tempstring = data;
        dl = new DataListener();
        dp = new DataPusher();
        // System.out.println("Inside data processor thread");
    }

    @Override
    public void run() {


        try {
            /// System.out.println("");
            //if (DBManager.getStatus()){
             // System.out.println("Inside data processor run method");
            if (tempstring.contains("^TMPER"))
            {
                //System.out.println("String Received "+ tempstring);
                 String[] st = tempstring.split("^TMPER");
                for (String dpackt : st) {
                    if (!dpackt.equals("")) {
                        String datapackt = "^TMPER" + dpackt;
                        if (datapackt.contains("^TMPER")) {
                                TMDataPacket(datapackt);
                        } else {
                            
                        }
                    }
                }
                                
            }
            else if(tempstring.contains("^TMALT"))
            {
                String[] st = tempstring.split("^TMALT");
                for (String dpackt : st) {
                    if (!dpackt.equals("")) {
                        String datapackt = "^TMALT" + dpackt;
                        if (datapackt.contains("^TMALT")) {
                                TMAlertPacket(datapackt);
                        } else {
                            
                        }
                    }
                }
            }
            else if (tempstring.contains("VTS")) {
                String[] st = tempstring.split("VTS,");

                for (String dpackt : st) {
                    if (!dpackt.equals("")) {
                        String datapackt = "";
                        datapackt = "VTS," + dpackt;
                        // System.out.println("Data Processor Started " + datapackt);
                        if (datapackt.contains("VTS,01")) {

                            VTSDataPacket(datapackt);

                        } else if (datapackt.contains("VTS,05")) {
                            VTSSOSMSG(datapackt);
                           // VTSACKMSG(datapackt);
                        } else if (datapackt.contains("VTS,06")) {
                            VTSSOSMSG(datapackt);
                        } else if (datapackt.contains("VTS,03")) {
                            VTSRxRtpacket(datapackt);
                        } else if (datapackt.contains("VTS,51")) {
                            VTSInfoDatapacket(datapackt);
                        } else if (datapackt.contains("VTS,52")) {
                            VTSLoginPacket(datapackt);
                        } else if (datapackt.contains("VTS,61")) {
                        } else if (datapackt.contains("VTS,62")) {
                            VTSFuelDataPacket(datapackt);
                        } else if (datapackt.contains("VTS,63")) {
                            VTSIgnitionDataPacket(datapackt);
                        } else if (datapackt.contains("VTS,64")) {
                            // VTSBonnetDataPacket(datapackt);
                        } else if (datapackt.contains("VTS,65")) {
                            //VTSDoorDataPacket(datapackt);
                        } else if (datapackt.contains("VTS,66")) {
                            //VTSEngineImmobilizerDataPacket(datapackt);
                        } else if (datapackt.contains("VTS,67")) {
                            //VTSACDataPacket(datapackt);
                        }
                    }
                }
            } else if (tempstring.startsWith("MTS")) {
                
                String[] st = tempstring.split("MTS,");
                for (String dpackt : st) {
                    if (!dpackt.equals("")) {
                        String datapackt = "";
                        datapackt = "MTS," + dpackt;
                        if (datapackt.contains("MTS,01")) {
                                MTSDataPacket(datapackt);
                        } else if (datapackt.contains("MTS,05")) {
                            //MTSSOSMSG(datapackt);
                        }
                    }
                }
            } else if (tempstring.contains("GET")) {
                //GETDataPacket(tempstring);
            }
            //Job Confirmation Command Received from Mobile Devices
            else if (tempstring.contains("^MJOBCONF")){
                //
            }
            //Job Status Update Command Received from Mobile Devices
            else if (tempstring.contains("^MJOBSTATUS")){
                //
                String[] st = tempstring.split("^MJOBSTATUS");
                for (String dpackt : st) {
                    if (!dpackt.equals("")) {
                        String datapackt = "^MJOBSTATUS" + dpackt;
                        if (datapackt.contains("^MJOBSTATUS")) {
                                JobConfirmationPacket(datapackt);
                        }
                    }
                }
            }
            
            // }
        } catch (Exception e) {
            try {
                LogMessage.errorfilecreation(e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //Job Confirmation packet 
    public void JobConfirmationPacket(String datapackt) throws IOException {
        
        String unitNo = "";
        String jobid = "";
        double lat = 0;
        double lon = 0;
        String utcTime = "";
        String utcDate = "";
        
        String[] str = datapackt.split("|");
        String dt="";
        
        try{
        //^MJOBCONF|9742022748|PJ1234|12.786453|27.348608|20:59:39|200415|!
            
            unitNo = str[1];
            jobid = str[2];
            lat = Double.parseDouble(str[3].trim());
            lon = Double.parseDouble(str[4].trim());
            utcTime = str[5];
            utcDate = str[6];
            
            dt = utcDate + " " + utcTime;
            
            //System.out.println(dt);
            /*DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            if (utcTime.equals("") || utcTime.equals(" 00:00:00") || utcTime.equals("0")) {
                date = new Date();
            } else {
                date = utcFormat.parse(dt);
            }
            DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
            String utcgmt = "";
            */
            //dp.InsertTMAPacket( unitNo,lait,logit,dt,speed,heading,Integer.parseInt(alerttype));         
        }
        catch(Exception e)
        {
            System.out.println("Error message "+ e.getMessage());
        }
    }

    //Job Confirmation packet 
    public void JobStatusPacket(String datapackt) throws IOException {
        
        String unitNo = "";
        String jobid = "";
        double lat = 0;
        double lon = 0;
        String utcTime = "";
        String utcDate = "";
        
        String[] str = datapackt.split("|");
        String dt="";
        
        try{
        //^MJOBSTATUS|9742022748|PJ1234||JOB START |12.786453|27.348608|20:59:39 |200415|!
            
            unitNo = str[1];
            jobid = str[2];
            lat = Double.parseDouble(str[3].trim());
            lon = Double.parseDouble(str[4].trim());
            utcTime = str[5];
            utcDate = str[6];
            
            dt = utcDate + " " + utcTime;
            
            //System.out.println(dt);
            /*DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            if (utcTime.equals("") || utcTime.equals(" 00:00:00") || utcTime.equals("0")) {
                date = new Date();
            } else {
                date = utcFormat.parse(dt);
            }
            DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
            String utcgmt = "";
            */
            //dp.InsertTMAPacket( unitNo,lait,logit,dt,speed,heading,Integer.parseInt(alerttype));         
        }
        catch(Exception e)
        {
            System.out.println("Error message "+ e.getMessage());
        }
    }
    
    //Trakmate Alert packet 
    public void TMAlertPacket(String datapackt) throws IOException {
        
        String unitNo = "";
        double lait = 0;
        double logit = 0;
        String utcTime = "";
        String utcDate = "";
        String heading="";
        String speed = "";
        
        String alerttype = "";
        String[] str = datapackt.split(",");
        int len = str.length;
        String dt="";
        try{
        //^TMALT|354868055694931|1|6|1|0.00000|0.00000|0|0|0.0|0|#
            
            unitNo = str[1];
            lait = Double.parseDouble(str[5].trim());
            logit = Double.parseDouble(str[6].trim());
            utcTime = str[7];
            utcDate = str[8];
            speed  = str[9];
            heading = str[10];
            alerttype = str[3];
            
            if (!utcDate.equals("") || utcDate.equals("0")) {
                utcDate = "20" + utcDate.substring(4, 6)+ "-"+utcDate.substring(2, 4)+ "-" + utcDate.substring(0, 2)  ;
            }
            if (!utcTime.equals("") || utcTime.equals("0")) {
                utcTime = utcTime.substring(0, 2) + ":" + utcTime.substring(2, 4) + ":" + utcTime.substring(4, 6);
                dt = utcDate + " " + utcTime;
            }
            //System.out.println(dt);
            DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            if (utcTime.equals("") || utcTime.equals(" 00:00:00") || utcTime.equals("0")) {
                date = new Date();
            } else {
                date = utcFormat.parse(dt);
            }
            DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
            String utcgmt = "";
            
            dp.InsertTMAPacket( unitNo,lait,logit,dt,speed,heading,Integer.parseInt(alerttype));
            

        }
        catch(Exception e)
        {
            System.out.println("Error message "+ e.getMessage());
        }
        
        
        
        
    }
    //TrackMate Packet
    public void TMDataPacket(String datapackt) throws IOException {
        
        String unitNo = "";
        double lait = 0;
        double logit = 0;
        String utcTime = "";
        String utcDate = "";
        String heading="";
        String speed = "";
        String ignition = "";
        String dinput1 = "";
        String dinput2 = "";
        String analog="";
        String gOdometer = "";
        String pOdometer = "";
        String mainSupply = "";
        String gValid = "";
        String pStatus = "";
        String[] str = datapackt.split(",");
        int len = str.length;
        String dt="";
        try{
            //^TMPER|354868055694931|234|12.99829|77.54013|130035|240315|0.0|341.18|1|0|0|0.030|3.7|11.8|0.0|0.0|1|1|0|
            //^TMPER|868050905399|1313|12.99838|77.54011|164344|070115|0.4||276.64|1|0|0|0.030|4.0|13.0|157.1|3.6|1|1|0|# at : 07-01-2015 22:10:22
            unitNo = str[1];
            lait = Double.parseDouble(str[3].trim());
            logit = Double.parseDouble(str[4].trim());
            utcTime = str[5];
            utcDate = str[6];
            speed  = str[7];
            heading = str[8];
            ignition = str[9];
            dinput1 = str[10];
            dinput2 = str[11];
            analog=str[12];
            gOdometer = str[15];
            pOdometer = str[16];
            mainSupply = str[17];
            gValid = str[18];
            pStatus = str[19];
            
            if (!utcDate.equals("")) {
                utcDate = "20" + utcDate.substring(4, 6)+ "-"+utcDate.substring(2, 4)+ "-" + utcDate.substring(0, 2)  ;
            }
            if (!utcTime.equals("")) {
                utcTime = utcTime.substring(0, 2) + ":" + utcTime.substring(2, 4) + ":" + utcTime.substring(4, 6);
                dt = utcDate + " " + utcTime;
            }
            //System.out.println(dt);
            DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            if (utcTime.equals("") || utcTime.equals(" 00:00:00")) {
                date = new Date();
            } else {
                date = utcFormat.parse(dt);
            }
            DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
            String utcgmt = "";
            if (gValid.equals("0")) {
                utcgmt = pstFormat.format(date);
               // System.out.println("Date for invalid packet "+utcgmt);
            }
           
            if (gValid.equals("1")) {
                dp.InsertTMPacket( unitNo,lait,logit,dt,speed,heading,Integer.parseInt(ignition),Integer.parseInt(dinput1),Integer.parseInt(dinput2)
                        ,analog,gOdometer,pOdometer,Integer.parseInt(mainSupply),Integer.parseInt(gValid),Integer.parseInt(pStatus));
            } else {
                 dp.InsertTMPacket( unitNo,lait,logit,dt,speed,heading,Integer.parseInt(ignition),Integer.parseInt(dinput1),Integer.parseInt(dinput2)
                        ,analog,gOdometer,pOdometer,Integer.parseInt(mainSupply),Integer.parseInt(gValid),Integer.parseInt(pStatus));
               // dp.InsertTMNoGPSPacket(unitNo, utcgmt, "Not valid packet");
            }

        }
        catch(Exception e)
        {
            System.out.println("Error message "+ e.getMessage());
        }
        
        
        
        
    }
    // VTS Response Packet
    public void VTSResponsepacket(String datapackt) throws IOException {
        String messageID;
        
        String eventID ;
        String simno ;
        String msgResponse = "";
        double lait = 0;
        double logit = 0;
        String utcTime = "";
        String utcDate = "";

        //VTS,05,0068(Count),simno,MSGID,eventid,msgresponse,utctime,date,lat,lon,587D
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");

                    simno = str[4];
                    messageID = str[5];
                    eventID = str[6];
                    msgResponse = str[7];
                    String std = "";
                    String newStr1 = "";
                    String newStr = "";
                    String str2 = str[8];
                    String str1 = str[9];

                    if (!str2.equals("")) {
                        newStr = str2.substring(0, 2) + "-" + str2.substring(2, 4) + "-20" + str2.substring(4, 6);
                    }
                    if (!str1.equals("")) {
                        newStr1 = str1.substring(0, 2) + ":" + str1.substring(2, 4) + ":" + str1.substring(4, 6);
                        std = newStr + " " + newStr1;
                    }


                    DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date;
                    if (std.equals("")) {
                        date = new Date();
                    } else {
                        date = utcFormat.parse(std);
                    }

                    DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                    String utcgmt = "";
                    if (std.equals("")) {
                        utcgmt = utcFormat.format(date);
                    } else {
                        utcgmt = pstFormat.format(date);
                    }
                    String[] sdate = utcgmt.split(" ");
                    utcDate = sdate[0];
                    utcTime = sdate[1];
                    if ((str[10].length() != 0) || (!str[10].equals("0000.0000"))) {
                        double lat = Double.parseDouble(str[10]);
                        lait = (((int) lat % 100 + lat - (int) lat) / 60.0F + (int) lat / 100);
                    } else {
                        lait = 0000.0000;
                    }

                    if ((str[11].length() != 0) || (!str[11].equals("0000.0000"))) {
                        double lon = Double.parseDouble(str[11]);
                        logit = (((int) lon % 100 + lon - (int) lon) / 60.0F + (int) lon / 100);
                    } else {
                        logit = 0000.0000;
                    }
                    dp.InsertResponseMessage(simno, messageID, eventID, msgResponse, utcDate, utcTime, lait, logit);
                }

            }

        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS Response Packet " + e.getMessage());
        }

    }
    //VTS RX & RT Packets

    public void VTSRxRtpacket(String datapackt) throws IOException {
        String messageID = "";
        String packettype = "";
        String simno = "";
        //VTS,03,0024,RT,8573036807,01067618736B5VTS05010677187CAB4VTS0501067918722B6VTS05010678187DEB7VTS050106801,1234

        //VTS,03,0068(Count),RT,simno,MSGID,,587D
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");

                    simno = str[4];
                    messageID = str[5];
                    packettype = str[3];
                    dp.InsertRxRTMessage(simno, messageID, packettype);
                }

            }

        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS RXRT Packet " + e.getMessage());
        }

    }
    //VTS Process

    public void VTSDataPacket(String datapacket) throws IOException {

        String simNo = "";
        String messageIdf = "";
        String gprSpack = "";
        String gsmSingal = "";
        String utcTime = "";
        String utcDate = "";
        String packValid = "";
        double lait = 0;
        double logit = 0;
        String speedInKn = "";
        String fuel = "";
        String ignition = "";
        String mainSupply = "";
        String batteryADC = "";
        String odometer = "";
        String tdate = "";
        String[] st = datapacket.split("VTS,");

        for (String dpackt : st) {
            //System.out.println("Converted String ----" + dpackt);
            if ((!"".equals(dpackt)) && (dpackt != null)) {
                String datapackt = "";
                datapackt = "VTS," + dpackt;
                String[] s = datapackt.split(",");

                int n = s.length;
                try {
                    for (int i = 0; i < n; i++) {
                        if (!s[i].equals("")) {
                            if (s[7].equals(s[i])) {
                                String std = "";
                                String newStr1 = "";
                                String newStr = "";
                                String str = s[15];
                                String str1 = s[7];

                                if (!str.equals("")) {
                                    newStr = str.substring(0, 2) + "-" + str.substring(2, 4) + "-20" + str.substring(4, 6);
                                }
                                if (!str1.equals("")) {
                                    newStr1 = str1.substring(0, 2) + ":" + str1.substring(2, 4) + ":" + str1.substring(4, 6);
                                    std = newStr + " " + newStr1;
                                }


                                DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date;
                                if (std.equals("") || std.equals(" 00:00:00")) {
                                    date = new Date();
                                } else {
                                    date = utcFormat.parse(std);
                                }

                                DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                                String utcgmt = "";
                                if (std.equals("")) {
                                    utcgmt = utcFormat.format(date);
                                } else {
                                    utcgmt = pstFormat.format(date);
                                }
                                packValid = s[8];

                                if (packValid.equals("V")) {
                                    utcgmt = pstFormat.format(date);
                                    // System.out.println("Date for invalid packet "+utcgmt);
                                }
                                //utcgmt = pstFormat.format(date);
                                String[] sdate = utcgmt.split(" ");

                                simNo = s[3];
                                messageIdf = s[4];
                                gprSpack = s[5];
                                gsmSingal = s[6];
                                utcTime = sdate[1].trim();

                                tdate = sdate[0].replace("-", "");
                                if ((s[9].length() != 0) || (!s[9].equals("0000.0000"))) {
                                    double lat = Double.parseDouble(s[9]);
                                    lait = (((int) lat % 100 + lat - (int) lat) / 60.0F + (int) lat / 100);
                                } else {
                                    lait = 0000.0000;
                                }

                                if ((s[11].length() != 0) || (!s[11].equals("0000.0000"))) {
                                    double lon = Double.parseDouble(s[11]);
                                    logit = (((int) lon % 100 + lon - (int) lon) / 60.0F + (int) lon / 100);
                                } else {
                                    logit = 0000.0000;
                                }
                                speedInKn = s[13];
                                utcDate = sdate[0];
                                if (n > 20) {
                                    fuel = s[16];
                                    ignition = s[17];
                                    odometer = s[18];
                                    mainSupply = s[19];
                                    batteryADC = s[20];
                                } else {
                                    fuel = "0";
                                    ignition = "0";
                                    odometer = "0";
                                    mainSupply = "0";
                                    batteryADC = "0";
                                }
                                //VTS,01,0081,8606097533,NM,GP,23,110356,A,1115.3219,N,07548.0304,E,10.89,260.78,121113,07,5471
                                // VTS,01,0098,9900061561,NM,GP,28,183124,A,1301.2127,N,07739.5491,E,0.0,73.9,081013,4095,00,4000,11,999,873,C0AF
                                if (packValid.equals("A")) {

                                    dp.InsertVTSPacket(tdate, simNo, lait, logit, messageIdf, Double.parseDouble(fuel), Double.parseDouble(speedInKn) * 1.852, mainSupply, ignition, utcTime, utcDate, Integer.parseInt(batteryADC), gprSpack, Integer.parseInt(gsmSingal));


                                } else {
                                    dp.InsertNoGPSPacket(simNo, std, messageIdf);
                                }
                                break;
                            }
                        } else {
                            // System.out.println("0");
                        }
                    }


                } catch (Exception e) {
                    LogMessage.errorfilecreation("VTS data Packet " + e.getMessage());
                }
            }
        }

    }
// VTS Info packets

    public void VTSInfoDatapacket(String datapackt) throws IOException {
        String ht_simcard_no = "";
        String ht_error_code = "";
        String ht_latitude = "";
        String ht_longitude = "";
        String ht_utc_time = "";
        String ht_utc_date = "";
        String medium = "";
        String gpgm_type = "";
        String timeformat = "";
        String dateformat = "";

        //VTS,51,0068,7259010917,NM,GP,INF05,A,31,052123,200813,1259.0516,N,07735.7899,E,587D
        //VTS,51,0071,9739807105,NM,GP,INF01,V,24,000000,0,0,0000.0000,N,00000.0000,E,94C8
        //VTS,51,0071,9739807105,NM,GP,INF01,V,27,090623,121213,0000.0000,N,00000.0000,E,6D1D
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");

                    ht_simcard_no = str[3];
                    ht_error_code = str[6];
                    ht_latitude = str[11];
                    ht_longitude = str[13];
                    ht_utc_time = str[9];
                    ht_utc_date = str[10];
                    medium = str[4];
                    gpgm_type = str[5];

                    timeformat = ht_utc_time.substring(0, 2) + ":" + ht_utc_time.substring(2, 4) + ":" + ht_utc_time.substring(4, 6);
                    dateformat = ht_utc_date.substring(0, 2) + "-" + ht_utc_date.substring(2, 4) + "-20" + ht_utc_date.substring(4, 6);
                    dp.InsertErrorMessage(ht_simcard_no, ht_error_code, Double.parseDouble(ht_latitude), Double.parseDouble(ht_longitude), timeformat, dateformat, medium, gpgm_type);
                }

            }

        } catch (Exception e) {

            LogMessage.errorfilecreation("VTS Info Packet " + e.getMessage());
        }

    }

    //VTS Fuel Packet
    public void VTSFuelDataPacket(String datapackt) throws IOException {

        //VTS,62,0045,7259010916,NM,0818,0.93,31,100036,200214,241E
        String flm_simcard_no = "";
        String flm_utc_time = "";
        String flm_utc_date = "";
        String flm_fueladc = "";
        String flm_speed = "";
        String flm_type = "";
        String flm_signal = "";
        String timeformat = "";
        String dateformat = "";

        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");
                    //System.out.println("Fuel Packet " + datapacket);
                    flm_simcard_no = str[3];
                    flm_utc_time = str[8];
                    flm_utc_date = str[9];
                    // System.out.println("Date time " + flm_utc_date + " " + flm_utc_time);
                    flm_fueladc = str[5];
                    flm_speed = str[6];
                    flm_type = str[4];
                    flm_signal = str[7];
                    // System.out.println("fuel ADC " + flm_fueladc + " " + flm_speed + " " + flm_type + " " + flm_signal);

                    timeformat = flm_utc_time.substring(0, 2) + ":" + flm_utc_time.substring(2, 4) + ":" + flm_utc_time.substring(4, 6);
                    dateformat = flm_utc_date.substring(0, 2) + "-" + flm_utc_date.substring(2, 4) + "-20" + flm_utc_date.substring(4, 6);

                    // System.out.println("\n\n debug 1 : "+dateformat+" "+timeformat);

                    // conversion UTC to IST
                    String current_date = dateformat + " " + timeformat;
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

                    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                    String ISTDate = sf1.format(sf.parse(current_date));

                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
                    dateformat = s.format(sf1.parse(ISTDate));
                    timeformat = sd.format(sf1.parse(ISTDate));

                    // System.out.println("debug 2 : "+dateformat+" "+timeformat);

                    dp.InsertFuelPacket(flm_simcard_no, timeformat, dateformat, Double.parseDouble(flm_fueladc), Double.parseDouble(flm_speed), flm_type, flm_signal);
                }
            }
        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS Fuel Packet " + e.getMessage());
        }
    }

    //VTS Ignition Packet
    public void VTSIgnitionDataPacket(String datapackt) throws IOException {
        //VTS,63,0038,7259010916,NM,11,31,060000,241213,BD8E
        String im_simcard_no = "";
        String im_utc_time = "";
        String im_utc_date = "";
        String im_value = "";
        String im_ptype = "";
        String im_signal_strength = "";
        String timeformat = "";
        String dateformat = "";
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");

                    im_simcard_no = str[3];
                    im_utc_time = str[7];
                    im_utc_date = str[8];
                    im_value = str[5];
                    im_ptype = str[4];
                    im_signal_strength = str[6];
                    timeformat = im_utc_time.substring(0, 2) + ":" + im_utc_time.substring(2, 4) + ":" + im_utc_time.substring(4, 6);
                    dateformat = im_utc_date.substring(0, 2) + "-" + im_utc_date.substring(2, 4) + "-20" + im_utc_date.substring(4, 6);

                    // conversion UTC to IST
                    String current_date = dateformat + " " + timeformat;
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

                    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                    String ISTDate = sf1.format(sf.parse(current_date));

                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
                    dateformat = s.format(sf1.parse(ISTDate));
                    timeformat = sd.format(sf1.parse(ISTDate));

                    dp.InsertIgnitionPacket(im_simcard_no, timeformat, dateformat, im_value, im_ptype, im_signal_strength);
                }

            }
        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS Ignition Packet " + e.getMessage());
        }
    }

    //VTS SOS Message
    //VTS,05,0042,9443774722,4001,100123,MSG61,290315,164028,12.9932027,77.5951877,838F
    public void VTSSOSMSG(String datapackt) throws IOException {
        String ht_simcard_no = "";
        String ht_utc_date = "";
        String ht_utc_time = "";
        String msgtype = "";
        String timeformat = "";
        String dateformat = "";
        String latitude="";
        String longitude="";
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");
                    ht_simcard_no = str[3];
                    ht_utc_date = str[7];
                    ht_utc_time = str[8];
                    latitude = str[9];
                    longitude = str[10];
                    timeformat = ht_utc_time.substring(0, 2) + ":" + ht_utc_time.substring(2, 4) + ":" + ht_utc_time.substring(4, 6);
                    //dateformat = ht_utc_date.substring(0, 2) + "-" + ht_utc_date.substring(2, 4) + "-20" + ht_utc_date.substring(4, 6);
                    dateformat = "20" + ht_utc_date.substring(4, 6)+"-"+ht_utc_date.substring(2, 4) + "-" + ht_utc_date.substring(0, 2);
                    msgtype = str[6];
                    System.out.println("Datetime for Alerts " + dateformat+" " +timeformat );
                    dp.InsertSOSMessage(ht_simcard_no, dateformat, timeformat, msgtype,latitude,longitude);

                }
            }

        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS SOS Packet " + e.getMessage());
        }
    }
    //MTS,05,0068,8573036807,000069,MSG51,111510,121213,838F

    public void MTSSOSMSG(String datapackt) throws IOException {
        String ht_simcard_no = "";
        String ht_utc_date = "";
        String ht_utc_time = "";
        String msgtype = "";
        String timeformat = "";
        String dateformat = "";
        try {
            String[] st = datapackt.split("MTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "MTS," + dpackt;
                    String[] str = datapacket.split(",");
                    ht_simcard_no = str[3];
                    ht_utc_date = str[7];
                    ht_utc_time = str[6];
                    timeformat = ht_utc_time.substring(0, 2) + ":" + ht_utc_time.substring(2, 4) + ":" + ht_utc_time.substring(4, 6);
                    dateformat = ht_utc_date.substring(0, 2) + "-" + ht_utc_date.substring(2, 4) + "-20" + ht_utc_date.substring(4, 6);
                    msgtype = str[5];
                   // dp.InsertSOSMessage(ht_simcard_no, dateformat, timeformat, msgtype);

                }
            }

        } catch (Exception e) {
            LogMessage.errorfilecreation("MTS SOS Packet " + e.getMessage());
        }
    }
    //VTS,05,0068,7760971088,000069,CFS003923,MSG63,09-12-2013,15:47:24,12.983419027918094,77.59805192673538
    //VTS,05,0042,9443774722,4001,100123,MSG62/MSG61,290315,164141,12.9932027,77.5951877,838F

    public void VTSACKMSG(String datapackt) throws IOException {
        String ht_simcard_no = "";
        String eventId = "";
        String msgCode = "";
        try {
            String[] st = datapackt.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");
                    ht_simcard_no = str[3];
                    eventId = str[5];
                    msgCode = str[6];
                    dp.InsertACKMessage(ht_simcard_no, eventId, msgCode);

                }
            }
        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS ACK Packet " + e.getMessage());
        }

    }
    ///VTS,52,0030,9739807105,LOG00,153400,121213,1259.0145,07735.8865,838F

    public void VTSLoginPacket(String datePacket) throws IOException {

        String simNo = "";
        double lat = 0.0;
        double lon = 0.0;
        String latitude = "";
        String longitude = "";
        String msgType = "";
        String utc_date = "";
        String ht_utc_time = "";
        try {
            String st[] = datePacket.split("VTS,");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "VTS," + dpackt;
                    String[] str = datapacket.split(",");
                    simNo = str[3];
                    latitude = str[7];
                    longitude = str[8];
                    msgType = str[4];
                    ht_utc_time = str[5];
                    utc_date = str[6];
                    ht_utc_time = ht_utc_time.substring(0, 2) + ":" + ht_utc_time.substring(2, 4) + ":" + ht_utc_time.substring(4, 6);
                    utc_date = utc_date.substring(0, 2) + "-" + utc_date.substring(2, 4) + "-20" + utc_date.substring(4, 6);
                    lat = (((int) Double.parseDouble(latitude) % 100 + Double.parseDouble(latitude) - (int) Double.parseDouble(latitude)) / 60.0F + (int) Double.parseDouble(latitude) / 100);
                    lon = (((int) Double.parseDouble(longitude) % 100 + Double.parseDouble(longitude) - (int) Double.parseDouble(longitude)) / 60.0F + (int) Double.parseDouble(longitude) / 100);
                    dp.InsertLoginDetails(simNo, lat, lon, msgType, utc_date, ht_utc_time);

                }
            }
        } catch (Exception e) {
            LogMessage.errorfilecreation("VTS Login Packet " + e.getMessage());
        }
    }

    //GET packet Process
    /*Split Page=>GET /skytrack/trackit/collarrxpvkscript.asp?strSimNo=+918606132488&s
    trtime=082701&strdate=13/11/12&strSms=LAT:1125.7210:1125.6883:1125.6970:1125.736
    2LON:07554.8558:07554.9207:07555.0025:07555.1338&V:41:38:35:44&Ver=1.90g_dk,TI=0
    0:00:20,K:,I=224,B=1,ADC=0 HTTP/1.1*/
   /* public void GETDataPacket(String datapackt) throws IOException {
        String simNo = "";
        String timeNow = "";
        String dateNow = "";
        String latitude1 = "";
        String latitude2 = "";
        String latitude3 = "";
        String latitude4 = "";
        String longitude1 = "";
        String longitude2 = "";
        String longitude3 = "";
        String longitude4 = "";
        String speed = "";
        String convertedDate = "";
        String std = "";
        String str = "";
        String utcgmt = "";
        String utctime = "";
        String utcdate = "";
        String tdate = "";
        try {
            String[] st = datapackt.split("GET");
            for (String dpackt : st) {
                if (!dpackt.equals("")) {
                    String datapacket = "";
                    datapacket = "GET" + dpackt;
                    //System.out.println("Split Page=>" + datapacket);
                    String regex = "[\\s,&:?=()-]";
                    String[] tokens = datapacket.split(regex);

                    for (int i = 0; i < tokens.length; i++) {

                        if (tokens[i].equals(tokens[10])) {
                            try {
                                // System.out.println("inisde the GET packet");
                                // simNo = tokens[2];
                                if (tokens[3].length() > 10) {
                                    simNo = tokens[3].replace("+91", "");
                                } else {
                                    simNo = tokens[3].replace("+", "");
                                }
                                if (simNo.length() > 0 && simNo.charAt(0) == '0') {
                                    simNo = "+" + simNo;
                                    simNo = simNo.replace("+0", "");
                                }
                                //timeNow = tokens[4];
                                timeNow = tokens[5];
                                //dateNow = tokens[6];
                                dateNow = tokens[7];
                                // latitude1 = tokens[10];
                                latitude1 = tokens[10];
                                longitude1 = tokens[14];
                                if (tokens.length >= 19) {
                                    speed = tokens[19];
                                } else {
                                    speed = "0";
                                }

                                str = timeNow.substring(0, 2) + ":" + timeNow.substring(2, 4) + ":" + timeNow.substring(4, 6);
                                std = dateNow.substring(6, 8) + "/" + dateNow.substring(3, 5) + "/20" + dateNow.substring(0, 2) + " " + str;
                                // System.out.println(std);
                                DateFormat utcFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = utcFormat.parse(std);
                                DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                                utcgmt = pstFormat.format(date);
                                String[] sdate = utcgmt.split(" ");
                                utctime = sdate[1];
                                utcdate = sdate[0];
                                //  System.out.println(std + "  "+utcdate + "  "+utctime);
                                tdate = utcdate.replace("-", "");
                                double latitude = (((int) Double.parseDouble(latitude1) % 100 + Double.parseDouble(latitude1) - (int) Double.parseDouble(latitude1)) / 60.0F + (int) Double.parseDouble(latitude1) / 100);
                                double longitude = (((int) Double.parseDouble(longitude1) % 100 + Double.parseDouble(longitude1) - (int) Double.parseDouble(longitude1)) / 60.0F + (int) Double.parseDouble(longitude1) / 100);
                                dp.InsertMTSPacket(tdate, simNo, latitude, longitude, "NM", "0", Double.parseDouble(speed), "11", "0", utctime, utcdate, 0, "GP", 20);

                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        }
                        if (tokens[i].equals(tokens[11])) {
                            try {
                                //System.out.println("inside the 11" + tokens[5]);
                                String timefo = tokens[5].substring(0, 2) + ":" + tokens[5].substring(2, 4) + ":" + tokens[5].substring(4, 6);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateFormat.parse(timefo));
                                cal.add(Calendar.SECOND, 20);
                                convertedDate = dateFormat.format(cal.getTime());
                                dateNow = tokens[7];
                                latitude2 = tokens[11];
                                longitude2 = tokens[15];
                                speed = tokens[20];
                                std = dateNow.substring(6, 8) + "/" + dateNow.substring(3, 5) + "/20" + dateNow.substring(0, 2) + " " + convertedDate;

                                DateFormat utcFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = utcFormat.parse(std);
                                DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                                utcgmt = pstFormat.format(date);
                                String[] sdate = utcgmt.split(" ");
                                utctime = sdate[1];
                                utcdate = sdate[0];
                                // System.out.println(std + "  "+utcdate + "  "+utctime);
                                double latitude = (((int) Double.parseDouble(latitude2) % 100 + Double.parseDouble(latitude2) - (int) Double.parseDouble(latitude2)) / 60.0F + (int) Double.parseDouble(latitude2) / 100);
                                double longitude = (((int) Double.parseDouble(longitude2) % 100 + Double.parseDouble(longitude2) - (int) Double.parseDouble(longitude2)) / 60.0F + (int) Double.parseDouble(longitude2) / 100);
                                dp.InsertMTSPacket(tdate, simNo, latitude, longitude, "NM", "0", Double.parseDouble(speed), "11", "0", utctime, utcdate, 0, "GP", 20);
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }

                        }
                        if (tokens[i].equals(tokens[12])) {
                            try {
                                String timefo = tokens[5].substring(0, 2) + ":" + tokens[5].substring(2, 4) + ":" + tokens[5].substring(4, 6);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateFormat.parse(timefo));
                                cal.add(Calendar.SECOND, 40);
                                convertedDate = dateFormat.format(cal.getTime());
                                dateNow = tokens[7];
                                latitude3 = tokens[12];
                                longitude3 = tokens[16];
                                speed = tokens[21];
                                std = dateNow.substring(6, 8) + "/" + dateNow.substring(3, 5) + "/20" + dateNow.substring(0, 2) + " " + convertedDate;

                                DateFormat utcFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = utcFormat.parse(std);
                                DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                                utcgmt = pstFormat.format(date);
                                String[] sdate = utcgmt.split(" ");
                                utctime = sdate[1];
                                utcdate = sdate[0];
                                //  System.out.println(std + "  "+utcdate + "  "+utctime);
                                double latitude = (((int) Double.parseDouble(latitude3) % 100 + Double.parseDouble(latitude3) - (int) Double.parseDouble(latitude3)) / 60.0F + (int) Double.parseDouble(latitude3) / 100);
                                double longitude = (((int) Double.parseDouble(longitude3) % 100 + Double.parseDouble(longitude3) - (int) Double.parseDouble(longitude3)) / 60.0F + (int) Double.parseDouble(longitude3) / 100);
                                dp.InsertMTSPacket(tdate, simNo, latitude, longitude, "NM", "0", Double.parseDouble(speed), "11", "0", utctime, utcdate, 0, "GP", 20);
                            } catch (Exception e) {
                                // TODO: handle exception
                                // e.printStackTrace();
                            }

                        }
                        if (tokens[i].equals(tokens[13])) {
                            try {
                                String timefo = tokens[5].substring(0, 2) + ":" + tokens[5].substring(2, 4) + ":" + tokens[5].substring(4, 6);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateFormat.parse(timefo));
                                cal.add(Calendar.SECOND, 60);
                                convertedDate = dateFormat.format(cal.getTime());
                                // simNo = tokens[3];
                                dateNow = tokens[7];
                                latitude4 = tokens[13];
                                latitude4 = latitude4.replace("LON", "");
                                longitude4 = tokens[17];
                                speed = tokens[22];
                                std = dateNow.substring(6, 8) + "/" + dateNow.substring(3, 5) + "/20" + dateNow.substring(0, 2) + " " + convertedDate;
                                DateFormat utcFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = utcFormat.parse(std);
                                DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
                                utcgmt = pstFormat.format(date);
                                String[] sdate = utcgmt.split(" ");
                                utctime = sdate[1];
                                utcdate = sdate[0];
                                //        System.out.println(std + "  "+utcdate + "  "+utctime);
                                double latitude = (((int) Double.parseDouble(latitude4) % 100 + Double.parseDouble(latitude4) - (int) Double.parseDouble(latitude4)) / 60.0F + (int) Double.parseDouble(latitude4) / 100);
                                double longitude = (((int) Double.parseDouble(longitude4) % 100 + Double.parseDouble(longitude4) - (int) Double.parseDouble(longitude4)) / 60.0F + (int) Double.parseDouble(longitude4) / 100);
                                dp.InsertMTSPacket(tdate, simNo, latitude, longitude, "NM", "0", Double.parseDouble(speed), "11", "0", utctime, utcdate, 0, "GP", 20);
                            } catch (Exception e) {
                                // TODO: handle exception
                                //e.printStackTrace();
                            }

                        }


                    }
                }
            }
        } catch (Exception e) {
            //dl.errorfilecreation(e.getMessage());
        }


    }*/

    //MTS packet Process
    public void MTSDataPacket(String datapacket) throws IOException {
        String simNo = "";
        String messageIdf = "";
        String gprSpack = "";
        String gsmSingal = "";
        String utcTime = "";
        String packValid = "";
        float lait = 0;
        float logit = 0;
        String speedInKn = "";
        String utcDate = "";
        String fuel = "";
        String ignition = "";
        String mainSupply = "";
        String batteryADC = "";
        String odometer = "";
        String tdate = "";
        String[] st = datapacket.split("MTS,");
//MTS,01,0099,9741124819,NM,GP,15,16:57:47,V,,,,,,,111013,3072,00,,11,38,090,0650
        //MTS,01,0099,9443774722,NM,GP,15,00:11:36,A,12.9932027,N,77.5951877,E,0.0,69.13,290315,3072,00,4000,11,50,90,0650
//MTS,01,0099,8573036807,NM,GP,25,11:22:47,A,12.982644715122273,N,77.59781489888393,E,0.0,69.13,121213,3072,00,4000,11,50,90,0650
        for (String dpackt : st) {
            if (!dpackt.equals("")) {
                String datapackt = "";
                datapackt = "MTS," + dpackt;
                String[] s = datapackt.split(",");

                int n = s.length;
                //System.out.println("MTS Packet Split    " + s.length);
                try {
                    for (int i = 0; i < n; i++) {
                        if (!s[i].equals("")) {
                            if (s[7].equals(s[i])) {
                                String std = "";
                                String newStr = "";
                                String str = s[15];
                                String str1 = s[7];
                                //    System.out.println("DATE   TIME " + str + "  " + str1);
                                if (!str.equals("")) {

                                    newStr = "20" + str.substring(4, 6) +  "-" + str.substring(2, 4) + "-" + str.substring(0, 2);
                                    std = newStr + " " + str1;
                                       System.out.println("Converted date " + std);
                                }

                                DateFormat utcFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date;

                                String[] sdate = std.split(" ");

                                simNo = s[3];
                                messageIdf = s[4];


                                gprSpack = s[5];
                                gsmSingal = s[6];


                                utcTime = sdate[1];
                                packValid = s[8];


                                tdate = sdate[0].replace("-", "");

                                System.out.println("LAT    LONG    "+s[9] +  "   "+s[11]);

                                if ((s[9].length() == 0) || (s[9].equals("")) || (s[9].equals("0000.0000"))) {
                                    lait = (float) 0000.0000;
                                } else {

                                    float lat = Float.parseFloat(s[9]);
                                    lait = lat; //(((int) lat % 100 + lat - (int) lat) / 60.0F + (int) lat / 100);

                                }

                                if ((s[11].length() == 0) || (s[11].equals("")) || (s[11].equals("0000.0000"))) {
                                    logit = (float) 0000.0000;
                                } else {
                                    float lon = Float.parseFloat(s[11]);
                                    logit = lon; //(((int) lon % 100 + lon - (int) lon) / 60.0F + (int) lon / 100);

                                }


                                speedInKn = s[13];
                                if (speedInKn.equals("")) {
                                    speedInKn = "0";
                                }

                                utcDate = sdate[0];

                                if (n > 20) {
                                    fuel = s[16];
                                    ignition = s[17];
                                    odometer = s[18];
                                    mainSupply = s[19];
                                    batteryADC = s[20];
                                } else {
                                    fuel = "0";
                                    ignition = "0";
                                    odometer = "0";
                                    mainSupply = "0";
                                    batteryADC = "0";
                                }


                                if (packValid.equals("A")) {
                                    System.out.println("Entered  GPS" + simNo + "       " + std );
                                    dp.InsertMTSPacket(simNo, lait, logit, Double.parseDouble(speedInKn), ignition,std);
                                } else {
                                     System.out.println("Entered No GPS" + simNo + "       " + utcTime + "         " + utcDate);
                                    dp.InsertNoGPSPacket(simNo, std, messageIdf);
                                }

                            }
                        } else {
                            // System.out.println("0");
                        }
                    }


                } catch (Exception e) {
                    LogMessage.errorfilecreation("MTS data Packet " + e.getMessage());
                }
            }
        }
    }

    //AC Packet060000
    /*public void VTSACDataPacket(String datapackt) throws IOException {
    //VTS,67,0038,7259010916,NM,11,31,060000,241213,0BBF
    String im_simcard_no = "";
    String im_utc_time = "";
    String im_utc_date = "";
    String im_value = "";
    String im_ptype = "";
    String im_signal_strength = "";
    String timeformat = "";
    String dateformat = "";
    try {
    String[] st = datapackt.split("VTS,");
    for (String dpackt : st) {
    if (!dpackt.equals("")) {
    String datapacket = "";
    datapacket = "VTS," + dpackt;
    String[] str = datapacket.split(",");

    im_simcard_no = str[3];
    im_utc_time = str[7];
    im_utc_date = str[8];
    im_value = str[5];
    im_ptype = str[4];
    im_signal_strength = str[6];
    timeformat = im_utc_time.substring(0, 2) + ":" + im_utc_time.substring(2, 4) + ":" + im_utc_time.substring(4, 6);
    dateformat = im_utc_date.substring(0, 2) + "-" + im_utc_date.substring(2, 4) + "-20" + im_utc_date.substring(4, 6);

    // conversion UTC to IST
    String current_date = dateformat + " " + timeformat;
    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
    String ISTDate = sf1.format(sf.parse(current_date));

    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
    dateformat = s.format(sf1.parse(ISTDate));
    timeformat = sd.format(sf1.parse(ISTDate));


    dp.InsertACPacket(im_simcard_no, timeformat, dateformat, im_value, im_ptype, im_signal_strength);
    }

    }
    } catch (Exception e) {
    dl.errorfilecreation("VTS Ignition Packet " + e.getMessage());
    }
    }

    //VTS bonnet packet
    public void VTSBonnetDataPacket(String datapackt) throws IOException {
    //VTS,64,0038,7259010916,NM,00,31,060000,170114,BD8E
    String um_simcard_no = "";
    String um_utc_time = "";
    String um_utc_date = "";
    String um_value = "";
    String um_ptype = "";
    String um_signal_strength = "";
    String timeformat = "";
    String dateformat = "";
    try {
    String[] st = datapackt.split("VTS,");
    for (String dpackt : st) {
    if (!dpackt.equals("")) {
    String datapacket = "";
    datapacket = "VTS," + dpackt;
    String[] str = datapacket.split(",");

    um_simcard_no = str[3];
    um_utc_time = str[7];
    um_utc_date = str[8];
    um_value = str[5];
    um_ptype = str[4];
    um_signal_strength = str[6];
    timeformat = um_utc_time.substring(0, 2) + ":" + um_utc_time.substring(2, 4) + ":" + um_utc_time.substring(4, 6);
    dateformat = um_utc_date.substring(0, 2) + "-" + um_utc_date.substring(2, 4) + "-20" + um_utc_date.substring(4, 6);

    // conversion UTC to IST
    String current_date = dateformat + " " + timeformat;
    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
    String ISTDate = sf1.format(sf.parse(current_date));

    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
    dateformat = s.format(sf1.parse(ISTDate));
    timeformat = sd.format(sf1.parse(ISTDate));

    dp.InsertBonnetPacket(um_simcard_no, timeformat, dateformat, um_value, um_ptype, um_signal_strength);
    }

    }
    } catch (Exception e) {
    dl.errorfilecreation("VTS Bonnet Packet " + e.getMessage());
    }
    }

    //VTS door packet
    public void VTSDoorDataPacket(String datapackt) throws IOException {
    //VTS,65,0038,7259010916,NM,11,31,060001,170114,BD8E
    String dm_simcard_no = "";
    String dm_utc_time = "";
    String dm_utc_date = "";
    String dm_value = "";
    String dm_ptype = "";
    String dm_signal_strength = "";
    String timeformat = "";
    String dateformat = "";
    try {
    String[] st = datapackt.split("VTS,");
    for (String dpackt : st) {
    if (!dpackt.equals("")) {
    String datapacket = "";
    datapacket = "VTS," + dpackt;
    String[] str = datapacket.split(",");

    dm_simcard_no = str[3];
    dm_utc_time = str[7];
    dm_utc_date = str[8];
    dm_value = str[5];
    dm_ptype = str[4];
    dm_signal_strength = str[6];
    timeformat = dm_utc_time.substring(0, 2) + ":" + dm_utc_time.substring(2, 4) + ":" + dm_utc_time.substring(4, 6);
    dateformat = dm_utc_date.substring(0, 2) + "-" + dm_utc_date.substring(2, 4) + "-20" + dm_utc_date.substring(4, 6);

    // conversion UTC to IST
    String current_date = dateformat + " " + timeformat;
    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
    String ISTDate = sf1.format(sf.parse(current_date));

    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
    dateformat = s.format(sf1.parse(ISTDate));
    timeformat = sd.format(sf1.parse(ISTDate));

    dp.InsertDoorPacket(dm_simcard_no, timeformat, dateformat, dm_value, dm_ptype, dm_signal_strength);
    }

    }
    } catch (Exception e) {
    dl.errorfilecreation("VTS Door Packet " + e.getMessage());
    }
    }

    //VTS Engine Immobilizer
    public void VTSEngineImmobilizerDataPacket(String datapackt) throws IOException {
    //VTS,02,0020,7259010916,ENOF,4218
    //VTS,66,0052,7259010916,NM,OF,+919986337594,31,060000,170114,F15A

    //SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    //SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
    String eim_simcard_no = "";
    String eim_utc_time = "";
    String eim_utc_date = "";
    String eim_value = "";
    String eim_ptype = "";
    String eim_sender = "";
    String eim_signal_strength = "";

    String timeformat = "";
    String dateformat = "";
    try {


    String[] st = datapackt.split("VTS,");
    for (String dpackt : st) {
    if (!dpackt.equals("")) {
    String datapacket = "";
    datapacket = "VTS," + dpackt;
    String[] str = datapacket.split(",");

    //eim_simcard_no = str[3];
    //eim_value = str[4];

    eim_simcard_no = str[3];
    eim_ptype = str[4];
    eim_value = str[5];
    eim_sender = str[6];
    eim_signal_strength = str[7];
    eim_utc_time = str[8];
    eim_utc_date = str[9];


    timeformat = eim_utc_time.substring(0, 2) + ":" + eim_utc_time.substring(2, 4) + ":" + eim_utc_time.substring(4, 6);
    dateformat = eim_utc_date.substring(0, 2) + "-" + eim_utc_date.substring(2, 4) + "-20" + eim_utc_date.substring(4, 6);



    // conversion UTC to IST
    String current_date = dateformat + " " + timeformat;
    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf.setTimeZone(TimeZone.getTimeZone("GMT"));

    SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    sf1.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
    String ISTDate = sf1.format(sf.parse(current_date));

    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
    dateformat = s.format(sf1.parse(ISTDate));
    timeformat = sd.format(sf1.parse(ISTDate));

    //dp.InsertEngineImmobilizerPacket(eim_simcard_no, timeformat, dateformat, eim_value );
    dp.InsertEngineImmobilizerPacket(eim_simcard_no, timeformat, dateformat, eim_value, eim_ptype, eim_signal_strength, eim_sender);

    }

    }
    } catch (Exception e) {
    dl.errorfilecreation("VTS Door Packet " + e.getMessage());
    }
    }*/
    //$loclBLTD,869988017271590,27022014,124735,0,1303.8816N,07735.0162E,0.07,22.276,13,0,00.30,0,1,A,1.0
    public void BOTODataPacket(String datapacket) throws IOException {
        String simNo = "";
        String packValid = "";
        double lait = 0;
        double logit = 0;
        String ignition = "";
        String gpsFix = "";
        String speed = "";
        String direction = "";

        String tamper = "";
        System.out.println(datapacket);
        String[] st = datapacket.split("$loc");

        System.out.println(st.length);

        for (String dpackt : st) {
            if (!dpackt.equals("")) {
                System.out.println(dpackt);
                String datapackt = "";
                datapackt = dpackt;

                System.out.println(datapackt);
                String[] s = datapackt.split(",");

                int n = s.length;
                try {

                    String std = "";
                    String newStr = "";
                    simNo = s[1];
                    String str = s[2];
                    String str1 = s[3];
                    String lattemp = "", lontemp = "";
                    System.out.println("DATE   TIME " + str + "  " + str1);
                    if (!str.equals("")) {

                        newStr = str.substring(4, 8) + "-" + str.substring(2, 4) + "-" + str.substring(0, 2);
                        String strtime = str1.substring(0, 2) + ":" + str1.substring(2, 4) + ":" + str1.substring(4, 6);
                        std = newStr + " " + strtime;
                        System.out.println("Converted date " + std);
                    }

                    gpsFix = s[4];
                    lattemp = s[5];
                    lattemp = lattemp.substring(0, lattemp.length() - 2);
                    lontemp = s[6];
                    lontemp = lontemp.substring(0, lontemp.length() - 2);

                    if ((s[5].length() == 0) || (s[5].equals("")) || (s[5].equals("0000.0000")) || (!gpsFix.equals("0"))) {
                        lait = 0000.0000;
                    } else {
                        double lat = Double.parseDouble(lattemp);
                        lait = (((int) lat % 100 + lat - (int) lat) / 60.0F + (int) lat / 100);
                    }
                    if ((s[6].length() == 0) || (s[6].equals("")) || (s[6].equals("0000.0000")) || (!gpsFix.equals("0"))) {
                        logit = 0000.0000;
                    } else {
                        double lon = Double.parseDouble(lontemp);
                        logit = (((int) lon % 100 + lon - (int) lon) / 60.0F + (int) lon / 100);
                    }
                    //$loclBOTO,869988017287885,22022014,185324,0,1316.8074N,07743.7172E,0.05,86.95,12,0,12.37,1,0,Z,1.0
                    speed = s[7];
                    direction = s[8];
                    ignition = s[10];
                    String bdis = s[12];
                    String version = s[15];

                    tamper = s[13];
                    packValid = s[14];

                    //if (packValid.equals("Z")) {

                    dp.InsertBOTOPacket(s[0], simNo, lait, logit, std, Double.parseDouble(speed), ignition, direction, packValid, tamper);
                    //} else {
                    // System.out.println("Entered No GPS" + simNo + "       " + utcTime + "         " + utcDate);
                    // dp.InsertNoGPSPacket(simNo, utcTime, utcDate, messageIdf);
                    //}




                } catch (Exception e) {
                    LogMessage.errorfilecreation("$loclBOTO Data Packet " + e.getMessage());

                    // return;
                }
            }
        }
    }
}
