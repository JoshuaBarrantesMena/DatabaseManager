package dblibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnect {
    // ORACLE
    private static String ORACLE_URL;
    private static String ORACLE_USER;
    private static String ORACLE_PASSWORD;

    public Connection connectWithOracle(String pUser, String pPass, String pHost, String pPort, String pEdition) throws SQLException {
        ORACLE_USER = pUser;
        ORACLE_PASSWORD = pPass;
        ORACLE_URL = "jdbc:oracle:thin:@" + pHost + ":" + pPort + ":" + pEdition;
        return DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
    }
    
    //MongoDB
    private static String MONGO_URL;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDatabase connectWithMongo(String pHost, String pPort, String databaseName) {
        MONGO_URL = "mongodb://" + pHost + ":" + pPort;
        mongoClient = MongoClients.create(MONGO_URL);
        database = mongoClient.getDatabase(databaseName);
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    //MySQL
    private static String URL_MYSQL;
    private static String MYSQL_USER;
    private static String MYSQL_PASSWORD;

    public Connection connectWithMySQL(String pUser, String pPass, String pHost, String pPort, String pSchema) throws SQLException {
        MYSQL_USER = pUser;
        MYSQL_PASSWORD = pPass;
        URL_MYSQL = "jdbc:mysql://" + pHost + ":" + pPort + "/" + pSchema;
        return DriverManager.getConnection(URL_MYSQL, MYSQL_USER, MYSQL_PASSWORD);
    }
}
