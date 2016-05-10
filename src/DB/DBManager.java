/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

/**
 *
 * @author sai
 */
import avlslistening.LogMessage;
import java.io.IOException;
import java.sql.Connection;

public class DBManager {

    private static DBConnectionPool avlsPool = null;

    public static void setDBPool() throws IOException {

        try {
            avlsPool = DBConnectionPool.getInstance();

        } catch (Exception se) {
            LogMessage.errorfilecreation("Error in getting connection:- " + se.getMessage());
        }
    }

    public static boolean getStatus() {
        return avlsPool.getStatus();
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {

            conn = avlsPool.getConnection();
        } catch (Exception ee) {
            //Main.gc();
        } finally {
            return conn;
        }
    }

    /*public static boolean isConnectionAlive(Connection conn)
    {
    return avlsPool.isConnectionAlive(conn);
    }*/
    public static void freeConnection(Connection conn) {
        avlsPool.free(conn);
    }

    public static boolean execute(String Query) {
        Connection con = null;
        boolean resQuery = false;
        try {
            con = avlsPool.getConnection();
            con.createStatement().execute(Query);
            resQuery = true;
        } catch (Exception se) {
            // Main.gc();
        } finally {
            if (con != null) {
                avlsPool.free(con);
            }

            return resQuery;
        }
    }
}
