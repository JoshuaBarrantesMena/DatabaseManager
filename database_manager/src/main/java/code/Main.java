package code;

import java.sql.SQLException;
import java.util.List;

import dblibrary.OracleConnection;

public class Main {
    public static void main(String[] args) throws SQLException {

        //Persona persona1 = new Persona("001", "Pepe", 32);
        //Perro perro1 = new Perro("Cariberto", "husky", "Brocoly", 7);

        OracleConnection myConnection = new OracleConnection("GLOBALDB", "1234", "MSI", "1522", "XE");

        Perro nuevoPerro = (Perro) myConnection.getObject(Perro.class, "Cariberto");

        System.out.println(nuevoPerro.getNombre());
        System.out.println(nuevoPerro.getRaza());
        System.out.println(nuevoPerro.getComidaFavorita());
        System.out.println(nuevoPerro.getEdad());
        System.out.println("\n");

        Perro nuevoPerro2 = nuevoPerro;

        nuevoPerro = (Perro) myConnection.getObject(Perro.class, "Tobi");

        System.out.println(nuevoPerro.getNombre());
        System.out.println(nuevoPerro.getRaza());
        System.out.println(nuevoPerro.getComidaFavorita());
        System.out.println(nuevoPerro.getEdad());
        System.out.println("\n");

        System.out.println(nuevoPerro2.getNombre());
        System.out.println(nuevoPerro2.getRaza());
        System.out.println(nuevoPerro2.getComidaFavorita());
        System.out.println(nuevoPerro2.getEdad());
        System.out.println("\n");

        

        //myConnection.sendObject(persona1);
        //myConnection.sendObject(perro1);

        //Perro perro2 = new Perro("Tobi", "Chihuahua", "Cordero", 3);

        //myConnection.sendObject(perro2);

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