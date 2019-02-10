/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.hello1;

/*

   Derby - Class SimpleApp

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * DJBC database access
 */
@Startup
@Singleton
public class DbAccess {

    private static final String SQL_SELECT_ALL_RESERVATIONS = "SELECT res.ID, res.DATE, res.TIME, res.NUMBEROFPERSONS, res.ROOMID, res.SPECIALGUEST, res.SPECIALWAITER, room.NAME, res.GUESTNAME FROM RESERVATIONS res LEFT JOIN ROOMS room ON res.ROOMID = room.ID ORDER BY res.ID";
    private static final String SQL_SELECT_ALL_RESERVATIONS_WHERE_GUESTNAME = "SELECT res.ID, res.DATE, res.TIME, res.NUMBEROFPERSONS, res.ROOMID, res.SPECIALGUEST, res.SPECIALWAITER, room.NAME, res.GUESTNAME FROM RESERVATIONS res LEFT JOIN ROOMS room ON res.ROOMID = room.ID WHERE ^SEARCH_TERM^ LIKE '%^SEARCH_VALUE^%' ORDER BY res.ID";

    /* the default framework is embedded */
    private String framework = "embedded";
    private String protocol = "jdbc:derby:";

    @PostConstruct
    public void init() {
        setup(new String[]{"derbyclient"});
        System.out.println("DbAccess initialized.");
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<Room>();

        Connection conn = createConnection();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;

        try {
            s = conn.createStatement();
            statements.add(s);

            rs = s.executeQuery(
                    "SELECT room.ID, room.NAME, room.CAPACITY FROM ROOMS room ORDER BY room.ID");

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt(1));
                room.setName(rs.getString(2));
                room.setCapacity(rs.getInt(3));
                rooms.add(room);
            }

        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            closeConnection(conn, rs, statements);
            conn = null;
            rs = null;
            statements = null;
        }

        if (rooms.isEmpty()) {
            reportFailure("No rows in ResultSet");
        }

        return rooms;
    }

    /**
     * Gets all reservations.
     *
     * @return The list of reservations
     */
    public List<Reservation> getAllReservations() {

        Connection conn = createConnection();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        List<Reservation> reservations = null;

        try {
            s = conn.createStatement();
            statements.add(s);

            rs = s.executeQuery(SQL_SELECT_ALL_RESERVATIONS);

            reservations = decodeReservations(rs);

        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            closeConnection(conn, rs, statements);
            conn = null;
            rs = null;
            statements = null;
        }

        if (reservations.isEmpty()) {
            reportFailure("No rows in ResultSet");
        }

        return reservations;
    }

    /**
     * Gets all reservations based on the search term(s).
     *
     * @return The list of searched reservations
     */
    public List<Reservation> searchReservations(String searchTerm, String searchValue) {

        Connection conn = createConnection();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;

        List<Reservation> reservations = null;

        try {
            s = conn.createStatement();
            statements.add(s);

            String query = SQL_SELECT_ALL_RESERVATIONS_WHERE_GUESTNAME.replace("^SEARCH_TERM^", searchTerm).replace("^SEARCH_VALUE^", searchValue);
            rs = s.executeQuery(query);

            reservations = decodeReservations(rs);

        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            closeConnection(conn, rs, statements);
            conn = null;
            rs = null;
            statements = null;
        }

        if (reservations == null || reservations.isEmpty()) {
            reportFailure("No rows in ResultSet");
        }

        return reservations;
    }

    /**
     * Inserts the given reservation into the database.
     *
     * @param reservation The new reservation
     */
    public void insert(Reservation reservation) {

        Connection conn = createConnection();
        ArrayList<Statement> statements = new ArrayList<Statement>();

        try {
            PreparedStatement psInsert = conn.prepareStatement("insert into RESERVATIONS (DATE, TIME, NUMBEROFPERSONS, ROOMID, SPECIALGUEST, SPECIALWAITER, GUESTNAME) values (?, ?, ?, ?, ?, ?, ?)");
            statements.add(psInsert);

            //psInsert.setInt(1, 2000);
            psInsert.setDate(1, reservation.getDate());
            psInsert.setTime(2, reservation.getTime());
            psInsert.setInt(3, reservation.getNumberOfPersons());

            if (reservation.getRoom() != null) {
                psInsert.setInt(4, reservation.getRoom().getId());
            } else {
                psInsert.setNull(4, Types.INTEGER);
            }
            psInsert.setBoolean(5, reservation.isIsSpecialGuest());
            psInsert.setBoolean(6, reservation.isIsSpecialWaiter());
            psInsert.setString(7, reservation.getGuestName());
            psInsert.executeUpdate();
            System.out.println("Inserted 2000");

            conn.commit();

        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            closeConnection(conn, null, statements);
            conn = null;
            statements = null;
        }
    }

    private List<Reservation> decodeReservations(ResultSet rs) throws SQLException {

        List<Reservation> reservations = new ArrayList<Reservation>();
        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setId(rs.getInt(1));
            reservation.setDate(rs.getDate(2));
            reservation.setTime(rs.getTime(3));
            reservation.setNumberOfPersons(rs.getInt(4));
            reservation.setRoom(new Room());
            reservation.getRoom().setId(rs.getInt(5));
            reservation.setIsSpecialGuest(rs.getBoolean(6));
            reservation.setIsSpecialWaiter(rs.getBoolean(7));
            reservation.getRoom().setName(rs.getString(8));
            reservation.setGuestName(rs.getString(9));
            reservations.add(reservation);
        }
        return reservations;
    }

    private void closeConnection(Connection conn, ResultSet rs, List<Statement> statements) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }

        // Statements and PreparedStatements
        int i = 0;
        while (!statements.isEmpty()) {
            // PreparedStatement extend Statement
            Statement st = (Statement) statements.remove(i);
            try {
                if (st != null) {
                    st.close();
                    st = null;
                }
            } catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }

        //Connection
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    private void setup(String[] args) {
        /* parse the arguments to determine which framework is desired*/
        parseArguments(args);
        System.out.println("SimpleApp starting in " + framework + " mode");

    }

    private Connection createConnection() {
        Connection conn = null;

        try {
            Properties props = new Properties();
            props.put("user", "project5");
            props.put("password", "project5");

            String dbName = "Project5";
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=false", props);

            System.out.println("Connected to and created database " + dbName);

            conn.setAutoCommit(false);

        } catch (SQLException sqle) {
            printSQLException(sqle);
        }

        return conn;
    }

    public void load(String[] args) {


        /* It is recommended to use PreparedStatements when you are
             * repeating execution of an SQL statement. PreparedStatements also
             * allows you to parameterize variables. By using PreparedStatements
             * you may increase performance (because the Derby engine does not
             * have to recompile the SQL statement each time it is executed) and
             * improve security (because of Java type checking).
         */
        // parameter 1 is num (int), parameter 2 is addr (varchar)
//            psInsert = conn.prepareStatement(
//                    "insert into RESERVATIONS values (?, ?, ?, ?, ?, ?, ?)");
//            statements.add(psInsert);
//
//            psInsert.setInt(1, 2000);
//            psInsert.setDate(2, new Date(2018, 11, 26));
//            psInsert.setTime(3, new Time(10, 30, 00));
//            psInsert.setInt(4, 5);
//            psInsert.setInt(5, 3000);
//            psInsert.setBoolean(6, true);
//            psInsert.setBoolean(7, true);
//            psInsert.executeUpdate();
//            System.out.println("Inserted 2000");
//
//            psInsert.setInt(1, 1910);
//            psInsert.setString(2, "Union St.");
//            psInsert.executeUpdate();
//            System.out.println("Inserted 1910 Union");
//            // Let's update some rows as well...
//            // parameter 1 and 3 are num (int), parameter 2 is addr (varchar)
//            psUpdate = conn.prepareStatement(
//                    "update location set num=?, addr=? where num=?");
//            statements.add(psUpdate);
//
//            psUpdate.setInt(1, 180);
//            psUpdate.setString(2, "Grand Ave.");
//            psUpdate.setInt(3, 1956);
//            psUpdate.executeUpdate();
//            System.out.println("Updated 1956 Webster to 180 Grand");
//            psUpdate.setInt(1, 300);
//            psUpdate.setString(2, "Lakeshore Ave.");
//            psUpdate.setInt(3, 180);
//            psUpdate.executeUpdate();
//            System.out.println("Updated 180 Grand to 300 Lakeshore");
//        conn.commit();
//        System.out.println("Committed the transaction");
    }

    /**
     * Reports a data verification failure to System.err with the given message.
     *
     * @param message A message describing what failed.
     */
    private void reportFailure(String message) {
        System.err.println("\nData verification failed:");
        System.err.println('\t' + message);
    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }

    /**
     * Parses the arguments given and sets the values of this class's instance
     * variables accordingly - that is, which framework to use, the name of the
     * JDBC driver class, and which connection protocol to use. The protocol
     * should be used as part of the JDBC URL when connecting to Derby.
     * <p>
     * If the argument is "embedded" or invalid, this method will not change
     * anything, meaning that the default values will be used.</p>
     * <p>
     * @param args JDBC connection framework, either "embedded" or
     * "derbyclient". Only the first argument will be considered, the rest will
     * be ignored.
     */
    private void parseArguments(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("derbyclient")) {
                framework = "derbyclient";
                protocol = "jdbc:derby://localhost:1527/";
            }
        }
    }
}
