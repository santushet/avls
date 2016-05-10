/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package avlslistening;

import DB.DBManager;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author sai
 */
public class DataPusher {

    Connection con_avls = null;
    private final Object LOCK = true;
    

    public DataPusher() throws IOException {
        DBManager.setDBPool();
        

    }

    public void InsertVTSPacket(String tdate, String simno, double latitude, double longitude, String medium, double fuel, double speed, String battery,
            String ignition, String utc_time, String utc_date, int internal_battery, String gpgm_type, int signalstrength) throws IOException, SQLException {
        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_normal_packet_new('" + tdate + "','" + simno + "',"
                    + "" + latitude + ","
                    + "" + longitude + ","
                    + "'" + medium + "'," + fuel + ","
                    + "" + speed + ","
                    + "'" + battery + "',"
                    + "'" + ignition + "',"
                    + "'" + utc_time + "','" + utc_date + "',"
                    + "" + internal_battery + ",'" + gpgm_type + "'," + signalstrength + ") }");

            synchronized (LOCK) {
                //System.out.println(insertStatement);
                insertStatement.execute();

            }

        } catch (Exception se) {
            LogMessage.errorfilecreation("Error inside the InsertVTSPacket:- " + se.getMessage());
                  } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertRxRTMessage(String simno, String messageID, String pType) throws SQLException, IOException {
        try {
            con_avls = DBManager.getConnection();
            //messageID=messageID.substring(0, messageID.length()-1);
            //dl.filecreation("inside the InsertRxRTMessage sim no:"+simno+"messageID:"+messageID+"pType:"+pType);
            
            CallableStatement insertStatement = this.con_avls.prepareCall("{? = call f_avls_text_message_send_new(" + Integer.parseInt(messageID) + " ,'" + Long.parseLong(simno) + "' ,'" + pType + "')}");
            insertStatement.registerOutParameter(1, Types.VARCHAR);           
            synchronized (LOCK) {
                insertStatement.execute();
            }
            String value = insertStatement.getString(1);
            //dl.filecreation("response message from simno"+simno+"for message ID==="+messageID);
            if(pType.equalsIgnoreCase("RT")){
            DataDispatch.sendToVMU(value, simno);
            }
            
        } catch (Exception e) {
            LogMessage.errorfilecreation("Error inside the InsertRxRTMessage:- " + e.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertResponseMessage(String simno, String messageID, String eventID, String msgResponse, String utcDate, String utcTime, double latit, double logit) throws IOException {
        try {
            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{f_avls_text_message_response_new(" + messageID + " ,'" + simno + "' ,'" + eventID + "','" + msgResponse + "','" + utcDate + "','" + utcTime + "'," + latit + "," + logit + ")}");

            synchronized (LOCK) {
                insertStatement.execute();
            }
            String value = insertStatement.getString(1);
            LogMessage.filecreation("Sending message to to simno"+simno+"mwssage with message Id"+messageID+"message IS:"+value);
            DataDispatch.sendToVMU(value, simno);


        } catch (Exception e) {
             LogMessage.errorfilecreation("Error inside the InsertResponseMessage:- " + e.getMessage());
           
        } finally {
            DBManager.freeConnection(con_avls);
        }
    }
    public void InsertTMAPacket(String unitNo,double lait,double logit,String dt,String speed,String heading,int alerttype) throws IOException, SQLException {
        try {

            con_avls = DBManager.getConnection();
            
            
            CallableStatement insertStatement = this.con_avls.prepareCall("{call ListenerTrackMateAlert('" + unitNo + "',"
                    + "" + lait + ","
                    + "" + logit + ","
                    + "'" + dt + "','" + speed + "',"
                    + "'" + heading + "',"
                    + "" + alerttype + ") }");

            synchronized (LOCK) {
               // System.out.println(insertStatement);
                insertStatement.execute();

            }

        } catch (Exception se) {
            LogMessage.errorfilecreation("Error inside the InsertVTSPacket:- " + se.getMessage());
                  } finally {
            DBManager.freeConnection(con_avls);
        }
    } 
    
//dp.InsertTMPacket( unitNo,lait,logit,dt,speed,heading,ignition,dinput1,dinput2,analog,gOdometer,pOdometer,mainSupply,gValid,pStatus);
//dp.InsertTMNoGPSPacket(unitNo, date, "Not valid packet");
     public void InsertTMPacket(String unitNo,double lait,double logit,String dt,String speed,String heading,int ignition,int dinput1,
             int dinput2,String analog,String gOdometer,String pOdometer,int mainSupply,int gValid,int pStatus) throws IOException, SQLException {
        try {

            con_avls = DBManager.getConnection();
            
            
            CallableStatement insertStatement = this.con_avls.prepareCall("{call ListenerTrackMate('" + unitNo + "',"
                    + "" + lait + ","
                    + "" + logit + ","
                    + "'" + dt + "','" + speed + "',"
                    + "'" + heading + "',"
                    + "" + ignition + ","
                    + "" + dinput1 + ","
                    + "" + dinput2 + ","
                    + "'" + analog + "',"
                    + "'" + gOdometer + "',"
                    + "'" + pOdometer + "',"
                    + "" + mainSupply+ ","
                    + "" + gValid + ","
                    + "" + pStatus + ") }");

            synchronized (LOCK) {
               // System.out.println(insertStatement);
                insertStatement.execute();

            }

        } catch (Exception se) {
            LogMessage.errorfilecreation("Error inside the InsertVTSPacket:- " + se.getMessage());
                  } finally {
            DBManager.freeConnection(con_avls);
        }
    } 
    
    public void InsertTMNoGPSPacket(String simno, String dt,String medium) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call ListenerTrackMatenongps('" + simno + "','" + dt + "','"+medium+"')}");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {

            LogMessage.errorfilecreation("Error inside the InsertNoGps:- " + se.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertNoGPSPacket(String simno, String dt,String medium) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_non_gps_packet_new('" + simno + "','" + dt + "','"+medium+"')}");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {

            LogMessage.errorfilecreation("Error inside the InsertNoGps:- " + se.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertSOSMessage(String simno, String utc_date, String utc_time, String msgtype,String latitude,String longitude) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();
            
            

            CallableStatement insertStatement = this.con_avls.prepareCall("{call Panic_Alerts('" + simno + "','"+latitude + "','" +longitude+ "','"+ utc_date + "','" + utc_time + "','" + msgtype + "')}");
                //dl.errorfilecreation("insert stattement============"+insertStatement);
            synchronized (LOCK) {
                insertStatement.execute();

            }
           
        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertSOSMessage:- " + se.getMessage());
            
        } finally {
            DBManager.freeConnection(con_avls);
        }
    }
    
      public void InsertACKMessage(String simno,String eventId,String msgCode) throws SQLException, IOException {
        try {
            con_avls = DBManager.getConnection();
            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_ack_message('" + simno + "',"
                    + "'" + eventId + "','" + msgCode + "')}");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertACKMessage:- " + se.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertErrorMessage(String simno, String error_code, double latitude, double longitude, String utc_time, String utc_date, String medium, String gpgm_type) throws SQLException, IOException {

        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_error_message_new('" + simno + "','" + error_code + "',"
                    + "" + latitude + ","
                    + "" + longitude + ","
                    + "'" + utc_time + "','" + utc_date + "',"
                    + "'" + medium + "','" + gpgm_type + "') }");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertErrorMessage:- " + se.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertIgnitionPacket(String simno, String utc_time, String utc_date, String ignition, String ptype, String signalstrength) throws SQLException, IOException {

        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_ignition_packet_new('" + simno + "',"
                    + "'" + utc_time + "','" + utc_date + "',"
                    + "'" + ignition + "',"
                    + "'" + ptype + "','" + signalstrength + "') }");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertIgnitionMessage:- " + se.getMessage());

        } finally {
            DBManager.freeConnection(con_avls);
        }
    }

    public void InsertFuelPacket(String simno, String utc_time, String utc_date, double fueladc, double speed, String ptype, String signalstrength) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();

            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_fuel_packet_new('" + simno + "',"
                    + "'" + utc_time + "','" + utc_date + "',"
                    + "'" + fueladc + "',"
                    + "'" + speed + "',"
                    + "'" + ptype + "','" + signalstrength + "') }");

            synchronized (LOCK) {
                insertStatement.execute();

            }

        } catch (SQLException se) {

            LogMessage.errorfilecreation("Error inside the Insertfuel packet:- " + se.getMessage());
        } finally {
            DBManager.freeConnection(con_avls);
        }
    }
//simNo, lait, logit, Double.parseDouble(speedInKn), ignition,std
    public void InsertMTSPacket(String simno, float latitude, float longitude, double speed,String ignition,String dt) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();
         //   System.out.println("Connection Object " +con_avls);
            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_normal_packet_new('" + simno + "',"
                    + "" + latitude + ","
                    + "" + longitude + ","
                    + "" + speed + ","
                    + "'" + ignition + "',"
                    + "'" +dt+"' ) }");

            synchronized (LOCK) {
               // System.out.println(insertStatement);
                insertStatement.execute();


            }

        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertMTSPacket:- " + se.getMessage());
        } finally {
            DBManager.freeConnection(con_avls);
        }
    }
    //InsertBOTOPacket(s[0],simNo, lait, logit,date, Double.parseDouble(speed), ignition, direction, packValid,tamper);
    public void InsertBOTOPacket(String ptype, String simno, double latitude, double longitude, String date,double speed,String ignition,
            String direction, String pvalid, String tamper) throws SQLException, IOException {
        try {

            con_avls = DBManager.getConnection();
            System.out.println( ptype + "," + simno + "," + latitude + "," + longitude + "," + date + "," + speed + "," + ignition + "," + direction + "," + pvalid + "," + tamper );
            CallableStatement insertStatement = this.con_avls.prepareCall("{call Listenerjava_v3novus('" + ptype + "','" + simno + "',"
                    + "'" + latitude + "',"
                    + "'" + longitude + "',"
                    + "'" + date + "',"
                    + "'" + speed + "',"
                    + "'" + ignition + "',"
                    + "'" + direction + "','" + pvalid + "',"
                    + "'" + tamper + "') }");

            synchronized (LOCK) {
                //System.out.println(insertStatement);
                insertStatement.execute();


            }

        } catch (SQLException se) {
            LogMessage.errorfilecreation("Error inside the InsertBOTOpacket:- " + se.getMessage());
        } finally {
            DBManager.freeConnection(con_avls);
        }
    }
    public void InsertLoginDetails(String simno, double latitude, double longitude, String status, String date, String time) throws IOException {

        String status1 = null;
        switch (status) {
            case "LOG11":
                status1 = "LogIn";
                break;
            case "LOG00":
                status1 = "LogOut";
                break;
        }
        try {
           // System.out.println(" Before insert to login details");
            CallableStatement insertStatement = this.con_avls.prepareCall("{call f_insert_login_details_new('" + simno + "',"
                    + "'" + status1 + "',"
                    + "" + latitude + ","
                    + "" + longitude + ","
                    + "'" + time + "','" + date + "') }");
            insertStatement.execute();
            
        } catch (Exception e) {
            LogMessage.errorfilecreation("Error inside the InsertLogindetails:- " + e.getMessage());
        }

    }
    
    
}
