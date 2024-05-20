package code;

import java.sql.SQLException;

import dblibrary.OracleConnection;

public class Main {
    public static void main(String[] args) throws SQLException {

        //Persona persona1 = new Persona("Pepe", 32);
        Perro perro1 = new Perro("Husky", "Cariberto", "Brocoly", 7);

        OracleConnection myConnection = new OracleConnection("GLOBALDB", "1234", "MSI", "1522", "XE");

        //myConnection.sendObject(persona1);
        myConnection.sendObject(perro1);

        Perro perro2 = new Perro("Husky", "Tobi", "Brocoly", 7);

        myConnection.updateObject(perro1, perro2);
    }
}