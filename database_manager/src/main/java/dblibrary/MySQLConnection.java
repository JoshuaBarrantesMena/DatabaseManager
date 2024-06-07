package dblibrary;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void sendObject(Object pObject) {
        try {
            Class<?> objectClass = pObject.getClass();
            String tableName = objectClass.getSimpleName().toLowerCase();
            try{
                
                createNewTable(tableName, objectClass);
            }catch(SQLException e){
                System.out.println(e.getMessage()); //modify
            }
            
            insertObject(tableName, objectClass, pObject);
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error al mapear la clase a la tabla: " + e.getMessage()); //modify
        }
    }

    public void updateObject(Object currentObject, Object newObject) {
        try {
            
            Class<?> objectClass = currentObject.getClass();
            String tableName = objectClass.getSimpleName().toLowerCase(); 

            if (!tableExist(tableName)) {
                System.out.println("La tabla " + tableName + " no existe."); //delete
                return;
            }

            if (!objectExists(tableName, objectClass, currentObject)) {
                System.out.println("El objeto existente no se encuentra en la tabla."); //modify
                return;
            }
    
            StringBuilder query = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            Field[] currentObjAttributes = objectClass.getDeclaredFields();
            for (Field objAttribute : currentObjAttributes) {

                objAttribute.setAccessible(true);
                String variableName = objAttribute.getName();
                Object updatedVariableValue = objAttribute.get(newObject);

                if (objAttribute.getType() == String.class) {
                    query.append(variableName).append(" = '").append(updatedVariableValue).append("', ");
                } else {
                    query.append(variableName).append(" = ").append(updatedVariableValue).append(", ");
                }
            }
    
            query.delete(query.length() - 2, query.length());
            query.append(" WHERE ");
            
            for (Field currentObjAttribute : currentObjAttributes) {

                currentObjAttribute.setAccessible(true);
                String variableName = currentObjAttribute.getName();
                Object existingVariableValue = currentObjAttribute.get(currentObject);
    
                if (currentObjAttribute.getType() == String.class) {
                    query.append(variableName).append(" = '").append(existingVariableValue).append("' AND ");
                } else {
                    query.append(variableName).append(" = ").append(existingVariableValue).append(" AND ");
                }
            }
    
            query.delete(query.length() - 5, query.length());
    
            try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                statement.executeUpdate();
                System.out.println("Objeto actualizado en la tabla " + tableName + " correctamente."); //delete
            }
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error al actualizar el objeto en la tabla: " + e.getMessage()); //modify
        }
    }





    private void createNewTable(String pTableName, Class<?> pObjectClass) throws SQLException {
        
        if (tableExist(pTableName)) {
            System.out.println("La tabla " + pTableName + " ya existe."); //delete
            return;
        }
        
        StringBuilder query = new StringBuilder("CREATE TABLE ")
                .append(pTableName)
                .append(" (");

        Field[] objAttributes = pObjectClass.getDeclaredFields();
        boolean primaryKey = false;

        for (Field objAttribute : objAttributes) { 

            objAttribute.setAccessible(true);
            String variableName = objAttribute.getName();
            String variableType = getTypeObjectString(objAttribute.getType());
            query.append(variableName).append(" ").append(variableType);
        
            if (variableName.equals(pTableName + "_id") && primaryKey == false) {
                query.append(" PRIMARY KEY");
                primaryKey = true;
            }
            query.append(", ");
        }
        
        query.delete(query.length() - 2, query.length());
        query.append(")");

        if (!primaryKey) {
            System.out.println("La clase " + pTableName + " no tiene un campo definido para clave primaria."); //delete
        }
        
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            statement.executeUpdate();
            System.out.println("Tabla " + pTableName + " creada correctamente."); //delete
        }
    }

    private void insertObject(String pTableName, Class<?> pObjectClass, Object pObject) throws SQLException, IllegalAccessException {

        if(!objectExists(pTableName, pObjectClass, pObject)){

            StringBuilder query = new StringBuilder("INSERT INTO ").append(pTableName).append(" (");
            StringBuilder values = new StringBuilder("VALUES (");

            Field[] objAttributes = pObjectClass.getDeclaredFields();

            for (Field objAttribute : objAttributes) {
                objAttribute.setAccessible(true);
                String variableName = objAttribute.getName();
                Object variableValue = objAttribute.get(pObject);

                query.append(variableName).append(", ");
                values.append("'").append(variableValue).append("', ");
            }
        
            query.delete(query.length() - 2, query.length());
            values.delete(values.length() - 2, values.length());

            query.append(") ");
            values.append(")");

            String insertQuery = query.toString() + values.toString();

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.executeUpdate();
                System.out.println("Datos insertados en la tabla " + pTableName + " correctamente."); //delete
            }catch(SQLException e){
                System.out.println("El objeto no pudo ser ingresado: " + e.getMessage()); //modify
            }
        }else{
            System.out.println("El dato ingresado ya existe en su base de datos"); //delete
        }
    }

    private boolean tableExist(String pTableName) throws SQLException {
        String query = "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";

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
            
        }catch(SQLException e){
            System.out.println("Error al comprobar si el objeto existe en la tabla: " + e.getMessage()); //modify
        }

        return false;
    }
}