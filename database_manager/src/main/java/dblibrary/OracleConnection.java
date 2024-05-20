package dblibrary;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleConnection {

    private Connection connection;

    public OracleConnection(String pUser, String pPass, String pHost, String pPort, String pEdition) throws SQLException {
        
        DatabaseConnect newConnection = new DatabaseConnect();
        this.connection = newConnection.connectWithOracle(pUser, pPass, pHost, pPort, pEdition);
    }

    public OracleConnection(Connection pConnection){

        this.connection = pConnection;
    }

    public void sendObject(Object pObject) {
        try {
            Class<?> objectClass = pObject.getClass();
            String tableName = objectClass.getSimpleName().toLowerCase(); // El nombre de la tabla es igual al nombre de la clase en minúsculas
            try{
                
                createNewTable(tableName, objectClass); // Crear la tabla si no existe
            }catch(SQLException e){

            }
            
            insertObject(tableName, objectClass, pObject); // Insertar el objeto en la tabla
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error al mapear la clase a la tabla: " + e.getMessage());
        }
    }



    private void createNewTable(String pTableName, Class<?> pObjectClass) throws SQLException {
        
        if (tableExist(pTableName)) {
            System.out.println("La tabla " + pTableName + " ya existe.");
            return;
        }

        StringBuilder query = new StringBuilder("CREATE TABLE ")
                .append(pTableName)
                .append(" (");

        Field[] objAttributes = pObjectClass.getDeclaredFields();
        
        for (Field objAttribute : objAttributes) { 

            objAttribute.setAccessible(true); // Permitir acceso a campos privados
            String variableName = objAttribute.getName();
            String variableType = getTypeObjectString(objAttribute.getType());
            query.append(variableName).append(" ").append(variableType).append(", ");           //string
        }

        // Eliminar la coma y el espacio extra al final de la definición de la tabla
        query.delete(query.length() - 2, query.length());
        query.append(")");

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            statement.executeUpdate();
            System.out.println("Tabla " + pTableName + " creada correctamente.");
        }
    }

    private void insertObject(String pTableName, Class<?> pObjectClass, Object pObject) throws SQLException, IllegalAccessException {

        if(!objectExists(pTableName, pObjectClass, pObject)){

            StringBuilder query = new StringBuilder("INSERT INTO ").append(pTableName).append(" (");  //string
            StringBuilder values = new StringBuilder("VALUES (");

            Field[] objAttributes = pObjectClass.getDeclaredFields();

            for (Field objAttribute : objAttributes) {
                objAttribute.setAccessible(true);
                String variableName = objAttribute.getName();
                Object variableType = objAttribute.get(pObject);

                query.append(variableName).append(", ");
                values.append("'").append(variableType).append("', ");
            }
        
            query.delete(query.length() - 2, query.length());
            values.delete(values.length() - 2, values.length());

            query.append(") ");
            values.append(")");

            String insertQuery = query.toString() + values.toString();

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.executeUpdate();
                System.out.println("Datos insertados en la tabla " + pTableName + " correctamente.");
            }
        }else{
            System.out.println("El dato ingresado ya existe en su base de datos");
        }
    }

    private boolean tableExist(String pTableName) throws SQLException {
        String query = "SELECT count(*) FROM user_tables WHERE table_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, pTableName.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    private String getTypeObjectString(Class<?> pAttributeType) {
        if (pAttributeType == String.class) {
            return "VARCHAR(255)";
        } else if (pAttributeType == int.class || pAttributeType == Integer.class) {
            return "INT";
        } else if (pAttributeType == double.class || pAttributeType == Double.class) {
            return "DOUBLE";
        } else if (pAttributeType == float.class || pAttributeType == Float.class) {
            return "FLOAT";
        } else if (pAttributeType == boolean.class || pAttributeType == Boolean.class) {
            return "BOOLEAN";
        } else {
            return "VARCHAR(255)";
        }
    }

    private boolean objectExists(String pTableName, Class<?> objectClass,  Object pObjeto) throws IllegalArgumentException, IllegalAccessException, SQLException{

        String query = "SELECT count(*) FROM " + pTableName.toUpperCase() + " WHERE ";

        Field[] objAttributes = objectClass.getDeclaredFields();

        for(Field objAttribute : objAttributes){

            objAttribute.setAccessible(true);
            Object valorCampo = objAttribute.get(pObjeto);

            if(objAttribute.getType() == String.class){
                
                query += objAttribute.getName().toUpperCase() + " = " + "'" + valorCampo +  "' AND ";

            }else{

                query += objAttribute.getName().toUpperCase() + " = " + valorCampo + " AND ";

            }
        }

        query = query.substring(0, query.length() - 5);

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int count = resultSet.getInt(1);
                return count > 0;
            }
            
        }

        return false;
    }
}
