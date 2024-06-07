package dblibrary;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {

    private Connection connection;

    public MySQLConnection(String username, String password, String host, String port, String edition) throws SQLException {
        
        DatabaseConnect newConnection = new DatabaseConnect();
        this.connection = newConnection.connectWithMySQL(username, password, host, port, edition);
    }

    public MySQLConnection(Connection pConnection){

        this.connection = pConnection;
    }
}
