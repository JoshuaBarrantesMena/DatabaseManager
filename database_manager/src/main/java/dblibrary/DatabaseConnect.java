package dblibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
    // ORACLE
    private static String ORACLE_USER;
    private static String ORACLE_PASSWORD;
    private static String URL;

    public static Connection conectarOracleXE(String pUser, String pPass, String pHost, String pPort, String pEdition) throws SQLException {
        ORACLE_USER = pUser;
        ORACLE_PASSWORD = pPass;
        URL = "jdbc:oracle:thin:@" + pHost + ":" + pPort + ":" + pEdition;
        return DriverManager.getConnection(URL, ORACLE_USER, ORACLE_PASSWORD);
    }
}
