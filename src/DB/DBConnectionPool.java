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
import avlslistening.Main;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/** A class for preallocating, recycling, and managing
 * JDBC connections.
 *
 * © 2013 sai.
 */
public class DBConnectionPool {

    private static String driver, url, username, password;
    private int maxConnections;
    private boolean waitIfBusy;
    private List availableConnections, busyConnections;
    private boolean connectionPending = false;
    private int initialConnections;
    private static DBConnectionPool singleton = new DBConnectionPool();
    private static boolean status = true;

    private DBConnectionPool()  {


        this.waitIfBusy = true;

        this.driver = Main.driver;
        this.url = Main.url;
        this.initialConnections = Main.initialConnections;
        this.maxConnections = Main.maxConnections;
        this.username = Main.username;
        this.password = Main.password;

        //System.out.println("USERNAME & PASSWORD" + url + "              "+ username + "        " + password);


        if (initialConnections > maxConnections) {
            initialConnections = maxConnections;
        }
        availableConnections = new ArrayList(initialConnections);
        busyConnections = new ArrayList();
        for (int i = 0; i < initialConnections; i++) {
            try {
                availableConnections.add(makeNewConnection());
                System.out.println("Connection   --- " + i);
            } catch (SQLException se) {
                try {
                    LogMessage.errorfilecreation("Error in getting connection:- " + se.getMessage());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static DBConnectionPool getInstance() {
        return singleton;
    }

    public synchronized Connection getConnection() throws SQLException {


        if (!availableConnections.isEmpty()) {
            Connection existingConnection = (Connection) availableConnections.get(availableConnections.size() - 1);
            int lastIndex = availableConnections.size() - 1;
            availableConnections.remove(lastIndex);

            //System.out.println("availableConnections Size New Connection "+availableConnections.size());
            // If connection on available list is closed (e.g.,
            // it timed out), then remove it from available list
            // and repeat the process of obtaining a connection.
            // Also wake up threads that were waiting for a
            // connection because maxConnection limit was reached.

            if (existingConnection.isClosed()) {
                notifyAll(); // Freed up a spot for anybody waiting
                return (getConnection());
            } else {
                busyConnections.add(existingConnection);
                return (existingConnection);
            }
        } else {

            // Three possible cases:
            // 1) You haven't reached maxConnections limit. So
            // establish one in the background if there isn't
            // already one pending, then wait for
            // the next available connection (whether or not
            // it was the newly established one).
            // 2) You reached maxConnections limit and waitIfBusy
            // flag is false. Throw SQLException in such a case.
            // 3) You reached maxConnections limit and waitIfBusy
            // flag is true. Then do the same thing as in second
            // part of step 1: wait for next available connection.

            if ((totalConnections() < maxConnections) && !connectionPending) {
                makeBackgroundConnection();
            } else if (!waitIfBusy) {
                throw new SQLException("Connection limit reached");

            }


            // Wait for either a new connection to be established
            // (if you called makeBackgroundConnection) or for
            // an existing connection to be freed up.
            try {
                wait();
            } catch (InterruptedException ie) {
                // ie.printStackTrace();
            }
            // Someone freed up a connection, so try again.
            return (getConnection());
        }
    }

    // You can't just make a new connection in the foreground
    // when none are available, since this can take several
    // seconds with a slow network connection. Instead,
    // start a thread that establishes a new connection,
    // then wait. You get woken up either when the new connection
    // is established or if someone finishes with an existing
    // connection.
    private void makeBackgroundConnection() {
        try {
            Connection connection = makeNewConnection();
            synchronized (this) {
                availableConnections.add(connection);
                connectionPending = false;
                notifyAll();
            }

        } catch (Exception e) {
            // SQLException or OutOfMemory
            // Give up on new connection and wait for existing one
            // to free up.
        }
    }

    public int availableCount() {
        return availableConnections.size();
    }

    public boolean getStatus() {
        if (busyConnections.size() >= maxConnections) {
            status = false;
        } else {
            status = true;
        }
        //System.out.println("Total busy connections    "+busyConnections.size());
        return status;
    }
    // This explicitly makes a new connection. Called in
    // the foreground when initializing the ConnectionPool,
    // and called in the background when running.

    private Connection makeNewConnection() throws SQLException {
        try {
            // Load database driver if not already loaded
            Class.forName(driver);
            // Establish network connection to database
            Connection connection = DriverManager.getConnection(url, username, password);

            return (connection);
        } catch (ClassNotFoundException cnfe) {
            // Simplify try/catch blocks of people using this by
            // throwing only one exception type.
            throw new SQLException("Can't find class for driver: " + driver);
        }
    }

    public synchronized void free(Connection connection) {
        try {
            busyConnections.remove(connection);
            availableConnections.add(connection);
            while (availableConnections.size() > this.initialConnections) {
                // Clean up extra available connections.
                // System.out.println("Connection is getting closed     "+availableCount());
                //Connection c = (Connection)availableConnections.get(availableConnections.size() - 1);
                Connection c = (Connection) availableConnections.get(0);
                availableConnections.remove(c);

                // Close the connection to the database.

                c.close();
            }
            if (totalConnections() < initialConnections) {
                availableConnections.add(makeNewConnection());
            }
        } catch (SQLException se) {
            //System.out.println("Free Connection   "+ se.getMessage());
        }
        // Wake up threads that are waiting for a connection
        notifyAll();
    }

    public synchronized int totalConnections() {
        return (availableConnections.size() + busyConnections.size());
    }

    /** Close all the connections. Use with caution:
     * be sure no connections are in use before
     * calling. Note that you are not <I>required to
     * call this when done with a ConnectionPool, since
     * connections are guaranteed to be closed when
     * garbage collected. But this method gives more control
     * regarding when the connections are closed.
     */
    public synchronized void closeAllConnections() {
        closeConnections(availableConnections);
        availableConnections = new ArrayList();
        closeConnections(busyConnections);
        busyConnections = new ArrayList();
    }

    private void closeConnections(List connections) {
        try {
            for (int i = 0; i < connections.size(); i++) {
                Connection connection = (Connection) connections.get(i);
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
        } catch (SQLException sqle) {
            // Ignore errors; garbage collect anyhow
        }
    }

    public synchronized String toString() {
        String info = "DBConnectionPool(" + url + "," + username + ")" + ", available=" + availableConnections.size() + ", busy=" + busyConnections.size() + ", max=" + maxConnections;
        return (info);
    }
}
