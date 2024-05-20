package code;

import java.sql.SQLException;
import java.util.List;

import dblibrary.OracleConnection;

public class Main {
    public static void main(String[] args) throws SQLException {

        Persona persona1 = new Persona("001", "Pepe", 32);
        //Perro perro1 = new Perro("Cariberto", "husky", "Brocoly", 7);

        OracleConnection myConnection = new OracleConnection("GLOBALDB", "1234", "MSI", "1522", "XE");

        myConnection.sendObject(persona1);
        //myConnection.sendObject(perro1);

        //Perro perro2 = new Perro("Tobi", "Husky", "Brocoly", 7);

        //myConnection.updateObject(perro1, perro2);

        /*List<Perro> list = myConnection.getAllObjects(Perro.class);

        int i = 1;
        for(Perro element : list){
            System.out.println("Perro #" + i);
            System.out.println(element.getRaza());
            System.out.println(element.getNombre());
            System.out.println(element.getComidaFavorita());
            System.out.println(element.getEdad());
            System.out.println("\n");
            i++;
        }*/
    }
}