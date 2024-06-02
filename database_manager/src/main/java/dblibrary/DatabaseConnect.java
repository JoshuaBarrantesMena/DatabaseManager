package dblibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnect {
    // ORACLE
    private static String ORACLE_USER;
    private static String ORACLE_PASSWORD;
    private static String URL;

    public Connection connectWithOracle(String pUser, String pPass, String pHost, String pPort, String pEdition) throws SQLException {
        ORACLE_USER = pUser;
        ORACLE_PASSWORD = pPass;
        URL = "jdbc:oracle:thin:@" + pHost + ":" + pPort + ":" + pEdition;
        return DriverManager.getConnection(URL, ORACLE_USER, ORACLE_PASSWORD);
    }
    
    //MongoDB
}
