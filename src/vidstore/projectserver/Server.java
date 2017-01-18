/*------------------------------------------------------------------------------
 * Author:              Craig Gibson
 * Project:             Movie Store
 * File name:           Server Class
 * Date created:        16/11/2016
 * Operating System:    -Windows 7
 *                      -64 bit OS
 * 
 * Class Description : This is the Server Class where all of the methods that 
 *                     I've created to use through other or the same package.
 *                     Allowing functionality to be used over the server.
 -----------------------------------------------------------------------------*/
package vidstore.projectserver;

import java.util.*;
import java.sql.*;
import org.apache.xmlrpc.WebServer;

/**
 * Creating the main class to run the server main method of the server class
 * that will run all the methods that need to be accessed by the Client or any
 * GUIs that run off the Server class.
 *
 * @author MB2013-0157 (Craig Gibson)
 */
public class Server {

    static Connection connection;
    String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
    String sourceURL = "jdbc:odbc:vidStore";
    WebServer server;
    static Server sr = new Server();

    /**
     * The method to connect to the database which is used throughout this class
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void Connect() throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        connection = DriverManager.getConnection(sourceURL);
    }

    /**
     * Creating the method to return the database info every time. this method
     * will return the result set of whatever the query is, as a vector to the
     * caller of the method.
     *
     * @param String Query
     * @return boolean
     */
    public boolean Connect(String Query) {

        try {
            Connect();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(Query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method does the same as the above code, except it uses Vectors to
     * allow cross package usage of the method and takes a query that checks the
     * details of adminUsers on the database.
     *
     * @param String Query
     * @return Vector det
     */
    public Vector detCheck(String Query) {

        Vector det = new Vector();
        try {
            Connect();
            Statement state = connection.createStatement();
            ResultSet set = state.executeQuery(Query);

            if (set.next()) {
                det.add(true);
                return det;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        det.add(false);
        return det;
    }

    /**
     * This method will return the description depending on what is selected
     * under the JTable on the ClientStartupGUI
     *
     * @param int select
     * @return Vector tempDesc
     */
    public Vector selectConnect(int select) {
        Vector tempDesc = new Vector();
        String desc;
        try {
            //This query gets the description that corresponds to the records ID
            String query = "SELECT description "
                    + "     FROM Movies "
                    + "     WHERE movie_id = " + select + ""
                    + "     ORDER BY movie_id";
            Connect();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                desc = rs.getString(1);
            } else {
                desc = "NA";
            }
            tempDesc.add(desc);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tempDesc;
    }

    /**
     * This method adds records to the Vector tempTable for the ClientStartupGUI
     * and populates it according to the records in the database.
     *
     * @param tableCols
     * @return
     */
    public Vector tableBuild(int tableCols) {
        Vector tempTable = new Vector();

        //Building the table before inserting it into the Client GUI
        try {
            String query = "SELECT movie_id, movie_name, genre_name "
                    + "FROM Movies, Genre "
                    + "WHERE Movies.genre_id = Genre.genre_id "
                    + "ORDER BY Movies.movie_name";
            Connect();
            Statement statement = connection.createStatement();
            ResultSet movies = statement.executeQuery(query);

            //Creating the string array to store the info row by row
            while (movies.next()) {
                String[] record = new String[tableCols];
                for (int j = 0; j < 3; j++) {
                    record[j] = movies.getString(j + 1);
                }
                tempTable.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tempTable;
    }

    /**
     * Method to search and display the table and populate it according to what
     * the String value received from the search bar is.
     *
     * @param String st
     * @param String Option
     * @return Vector search
     */
    public Vector schdTable(String st, String Option) {
        Vector search = new Vector();

        try {
            String query;

            //The following if statement checks to see what value has beens sent
            //to the method so that the refresh button can work using less
            //processing power, and also which search option has been ticked.
            if (st.equals("???")) {
                query = "SELECT movie_id, movie_name, genre_name "
                        + "FROM Movies, Genre "
                        + "WHERE Movies.genre_id = Genre.genre_id "
                        + "ORDER BY Movies.movie_name";
            } else {
                //Then if the computer establishes that it isn't the refresh 
                //button that has been clicked it enters an if to see which
                //RadioButton search option has been clicked.
                if (Option.equals("Movie")) {
                    query = "SELECT movie_id, movie_name, genre_name"
                            + "     FROM Movies, Genre"
                            + "     WHERE Movies.genre_id = Genre.genre_id"
                            + "        AND (Movies.movie_name LIKE '%" + st + "'"
                            + "        OR Movies.movie_name LIKE '%" + st + "%'"
                            + "        OR Movies.movie_name LIKE '" + st + "%')";
                } else {
                    query = "SELECT movie_id, movie_name, genre_name"
                            + "     FROM Movies, Genre"
                            + "     WHERE Movies.genre_id = Genre.genre_id"
                            + "        AND (Genre.genre_name LIKE '%" + st + "'"
                            + "        OR Genre.genre_name LIKE '%" + st + "%'"
                            + "        OR Genre.genre_name LIKE '" + st + "%')";
                }
            }
            Connect();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            //This is the code that populates the vector, but does so depending
            //on which radio button is selected(Movie/Genre), by changing the
            //query.
            while (rs.next()) {
                String[] rows = new String[3];
                for (int i = 0; i < 3; i++) {
                    rows[i] = rs.getString(i + 1);
                }
                search.add(rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    /**
     * The following method inserts movies into the database when the admin
     * insertion option is used.
     *
     * @param String movName
     * @param int genNum
     * @param String Desc
     * @return
     */
    public Vector insertMovies(String movName, int genNum, String Desc) {
        Vector insertStatus = new Vector();
        //Insert query that will only be executed last
        String query = "INSERT INTO Movies (movie_name, description, genre_id)"
                + "Values('" + movName + "', '" + Desc + "', " + genNum + ")";
        //Check query which is used to check if the movie and genre ID exist
        String valueCheck = "SELECT * FROM Movies WHERE movie_name = "
                + "'" + movName + "' AND genre_id = " + genNum;
        //Check query to see if the genre ID exists where it is supposed to be
        String genCheck = "SELECT * FROM Genre WHERE genre_id = " + genNum;
        try {
            Connect();
            Statement statement = connection.createStatement();
            ResultSet eg = statement.executeQuery(genCheck);

            //These if statements check to see which values exist and which
            //don't:

            //Checks if there is a genre that exists
            if (eg.next()) {
                ResultSet ev = statement.executeQuery(valueCheck);

                //Checks to see if there is a dupicate value and does not allow
                //that.
                if (ev.next()) {
                    insertStatus.add(1);
                } else {
                    statement.executeUpdate(query);
                    insertStatus.add(0);
                }
            } else {
                insertStatus.add(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return insertStatus;
    }

    /**
     * The following method inserts genres into the database when the admin
     * insert genre is used.
     *
     * @param String genName
     * @return Vector insertStatus
     */
    public Vector insertGenres(String genName) {
        Vector insertStatus = new Vector();

        //Query to insert
        String query = "INSERT INTO Genre (genre_name)"
                + "Values ('" + genName + "')";
        //Query to check
        String genCheck = "SELECT * FROM Genre WHERE genre_name = '" + genName
                + "'";
        try {
            Connect();
            Statement statement = connection.createStatement();
            ResultSet eg = statement.executeQuery(genCheck);

            //If the genre exists then prevent the duplicate value by sending 
            //a false boolean back to the caller.
            if (eg.next()) {
                insertStatus.add(false);
            } else {
                statement.executeUpdate(query);
                insertStatus.add(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return insertStatus;
    }

    /**
     * This code allows a record to be deleted from the database by taking the
     * specified ID of the movie. and deleting the records with that specific
     * ID which is an automatic number.
     *
     * @param int sID
     * @return Vector dltStatus
     */
    public Vector deleteMovies(int sID) {
        Vector dltStatus = new Vector();
        //This is the delete query which will only be used after the check query
        String query = "DELETE FROM Movies WHERE movie_id = " + sID;

        //This is teh check query that is used to check if the movie does exist.
        String chkRec = "SELECT * FROM Movies WHERE movie_id = " + sID;
        try {
            Connect();
            Statement statement = connection.createStatement();
            ResultSet ev = statement.executeQuery(chkRec);
            if (ev.next()) {
                dltStatus.add(true);
                statement.executeUpdate(query);
            } else {
                dltStatus.add(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dltStatus;
    }

    /**
     * Method to startup the ServerGUI.
     */
    public void sStartGUI() {
        ServerStartupGUI stGUI = new ServerStartupGUI(sr);
        stGUI.setVisible(true);
    }

    /**
     * Method to startup the Server.
     */
    public void sStart() {
        try {
            System.out.println("Server Starting...");

            //Assigning the XML a port number
            server = new WebServer(8080);
            server.addHandler("sample", new Server());

            //Starting server
            server.start();
            System.out.println("Started Server Successfully");
            System.out.println("Accepting requests");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to shutdown the Server and close the program
     */
    public void sStop() {
        server.shutdown();
        System.out.println("Server shutting down...");
    }

    //
    /**
     * Creating the main method to connect to the database
     * 
     * @param String[] args
     */
    public static void main(String[] args) {
        adminLogonGUI alg = new adminLogonGUI(sr);
        alg.setVisible(true);
    }
}