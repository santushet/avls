/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avlslistening;

/**
 *
 * @author sai
 */
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
public class LogMessage {


    public static void filecreation(String StrResponse, int portNum) throws IOException {
        String day, month, year, FileName1 = "";
        try {
            Calendar calendar = Calendar.getInstance();
            day = "" + calendar.get(5);
            month = "" + (calendar.get(2) + 1);
            year = "" + calendar.get(1);
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (month.length() == 1) {
                month = "0" + month;
            }
            String currDate = day + month + year;
            File file1 = new File("/log/" + currDate);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileName1 = "Trinity" + portNum + "-" + month + day + "Complete.log";
            FileOutputStream DebugFile = new FileOutputStream(file1.getAbsolutePath()+"/"+FileName1, true);
            PrintStream DebugDataStream = new PrintStream(DebugFile);
            if (!StrResponse.equals("")) {
                DebugDataStream.println("Data Recieved :" + StrResponse + " at : " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            }
            DebugDataStream.close();
            DebugFile.close();
        } catch (IOException ie) {
            //Main.gc();
        }
    }
    public static void filecreation(String StrResponse) throws IOException {
        String day, month, year, FileName1 = "";
        try {
            Calendar calendar = Calendar.getInstance();
            day = "" + calendar.get(5);
            month = "" + (calendar.get(2) + 1);
            year = "" + calendar.get(1);
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (month.length() == 1) {
                month = "0" + month;
            }
            String currDate = day + month + year;
            File file1 = new File("/log/" + currDate);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileName1 = "Trinity-" + month + day + "Dispatch.log";
            FileOutputStream DebugFile = new FileOutputStream(file1.getAbsolutePath()+"/"+FileName1, true);
            PrintStream DebugDataStream = new PrintStream(DebugFile);
            if (!StrResponse.equals("")) {
                DebugDataStream.println("Data to send :" + StrResponse + " at : " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            }
            DebugDataStream.close();
            DebugFile.close();
        } catch (IOException ie) {
            //Main.gc();
        }
    }

    public static void errorfilecreation(String StrResponse) throws IOException {
        String day, month, year, FileName1 = "";
        try {
            Calendar calendar = Calendar.getInstance();
            day = "" + calendar.get(5);
            month = "" + (calendar.get(2) + 1);
            year = "" + calendar.get(1);
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (month.length() == 1) {
                month = "0" + month;
            }
            String currDate = day + month + year;
            File file1 = new File("/log/" + currDate);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileName1 = "TrinityError-" + month + day + ".log";
            FileOutputStream DebugFile = new FileOutputStream(file1.getAbsolutePath()+"/"+FileName1, true);

            PrintStream DebugDataStream = new PrintStream(DebugFile);
            if (!StrResponse.equals("")) {
                DebugDataStream.println(StrResponse);
            }
            DebugDataStream.close();
            DebugFile.close();
        } catch (IOException ie) {
            // Main.gc();
        }
    }

}
