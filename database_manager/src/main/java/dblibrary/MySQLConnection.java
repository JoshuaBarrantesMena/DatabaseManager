package dblibrary;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnection {

    private Connection connection;

    public MySQLConnection(String username, String password, String host, String port, String schema) throws SQLException {
        
        DatabaseConnect newConnection = new DatabaseConnect();
        this.connection = newConnection.connectWithMySQL(username, password, host, port, schema);
    }

    public MySQLConnection(Connection pConnection){

        this.connection = pConnection;
    }

    public void sendObject(Object pObject) {
        
        Class<?> objectClass = pObject.getClass();
        String tableName = objectClass.getSimpleName().toLowerCase();
        
        try {
            try{
                createNewTable(tableName, objectClass);
            }catch(SQLException e){
                System.out.println("[Error al crear la tabla '" + tableName + "' ]: " + e.getMessage());
            }
            
            insertObject(tableName, objectClass, pObject);
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("[Error al insertar el objeto en la tabla '" + tableName + "']: " + e.getMessage());
        }
    }

    public void updateObject(Object currentObject, Object newObject) {
                
        Class<?> objectClass = currentObject.getClass();
        String tableName = objectClass.getSimpleName().toLowerCase(); 
        
        try {
            if (!tableExist(tableName)) {
                System.out.println("[La tabla '" + tableName + "' no existe]");
                return;
            }

            if (!objectExists(tableName, objectClass, currentObject)) {
                System.out.println("[El objeto no se encuentra en la tabla '" + tableName + "']");
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
            }
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("[Error al actualizar los valores del objeto en la tabla '" + tableName + "']: " + e.getMessage());
        }
    }

    public void deleteObject(Object pObject) {
        
        Class<?> objectClass = pObject.getClass();
        String tableName = objectClass.getSimpleName().toLowerCase();
        
        try {
            if (!this.tableExist(tableName)) {
                System.out.println("[La tabla '" + tableName + "' no existe]");
                return;
            }

            if (!this.objectExists(tableName, objectClass, pObject)) {
                System.out.println("[El objeto no se encuentra en la tabla '" + tableName + "']");
                return;
            }

            StringBuilder query = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
            Field[] objAttributes = objectClass.getDeclaredFields();

            for (Field field : objAttributes) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(pObject);

                if (field.getType() == String.class) {
                    query.append(fieldName).append(" = '").append(fieldValue).append("' AND ");
                } else {
                    query.append(fieldName).append(" = ").append(fieldValue).append(" AND ");
                }
            }

            query.delete(query.length() - 5, query.length()); 
            String deleteQuery = query.toString();

            try (PreparedStatement statement = this.connection.prepareStatement(deleteQuery)) {
                statement.executeUpdate();
            }
        } catch (IllegalAccessException | SQLException e) {
            System.err.println("[Error al eliminar el objeto de la tabla '" + tableName + "'. Posible ausencia o exceso de variables definidas en la clase]: " + e.getMessage());
        }
    }

    public <T> List<T> getAllObjects(Class<T> objectClass) {
        List<T> objList = new ArrayList<>();
        String tableName = objectClass.getSimpleName().toLowerCase();
        try {
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        T object = buildObject(objectClass, resultSet);
                        objList.add(object);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Error al recuperar los objetos de la tabla '" + tableName + "']: " + e.getMessage());
        }
        return objList;
    }

    public <T> Object getObject(Class<T> objectClass, String attribute) throws SQLException{
        
        String tableName = objectClass.getSimpleName().toLowerCase();
        String primaryKey = null;
        String query = "SELECT * FROM " + tableName.toUpperCase() + " Where ";

        try{
            String queryPK = "SHOW KEYS FROM test.persona WHERE KEY_NAME = 'PRIMARY';";

            try(PreparedStatement statement = connection.prepareStatement(queryPK)){

                ResultSet resultSet = statement.executeQuery(queryPK);
                while(resultSet.next()){
                    primaryKey = (String)resultSet.getObject("Column_name");
                }
            }

            Object object = null;

            if(primaryKey != null){

                query += primaryKey + " = '" + attribute + "'";
                try(PreparedStatement statement = connection.prepareStatement(query)){

                    ResultSet resultSet2 = statement.executeQuery(query);
                    while(resultSet2.next()){
                        object = buildObject(objectClass, resultSet2);
                        break;
                    }

                    if(object != null){
                        return object;
                    }else{
                        System.out.println("[No existe un objeto con el parametro '" + attribute + "']");
                    }
                }
            }
            else{
                System.out.println("[Tabla sin llave primaria definida. Busqueda realizada a partir del primer atributo definido en la clase]");

                Field[] classAttributes = objectClass.getDeclaredFields();
                String defaultAttribute = classAttributes[0].getName();
                query += defaultAttribute + " = '" + attribute + "'";

                try(PreparedStatement statement = connection.prepareStatement(query)){

                    ResultSet resultSet2 = statement.executeQuery(query);
                    while(resultSet2.next()){
                        object = buildObject(objectClass, resultSet2);
                        break;
                    }

                    if(object != null){
                        return object;
                    }else{
                        System.out.println("[No existe un objeto con el parametro '" + attribute + "']");
                    }
                }
            }

        }catch(SQLException e){
            System.out.println("[Error al realizar la busqueda en la tabla '" + tableName + "' del objeto con el parametro '" + attribute + "']: " + e.getMessage());
        }
        return null;
    }





    private void createNewTable(String pTableName, Class<?> pObjectClass) throws SQLException {
        
        if (tableExist(pTableName)) {
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
        
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            statement.executeUpdate();
            System.out.println("[Tabla " + pTableName + " creada correctamente]");
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
                System.out.println("[Datos ingresados correctamente en la tabla '" + pTableName + "']");
            }catch(SQLException e){
                System.out.println("[Error al ingresar el objeto en la tabla '" + pTableName + "']: " + e.getMessage());
            }
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
            System.out.println("[Error al comprobar si el objeto existe en la tabla '" + pTableName + "'. Posible ausencia o exceso de variables definidas en la clase]: " + e.getMessage());
        }

        return false;
    }

    private <T> T buildObject(Class<T> objectClass, ResultSet resultSet) throws SQLException {
        try {
            Constructor<T> constructor = objectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            Field[] objAttributes = objectClass.getDeclaredFields();

            for (Field objAttribute : objAttributes) {
                
                objAttribute.setAccessible(true);
                String variableName = objAttribute.getName();
                Object newObject = resultSet.getObject(variableName);

                if (newObject != null) {
                    
                    if (objAttribute.getType() == int.class || objAttribute.getType() == Integer.class) {
                        newObject = ((Number) newObject).intValue();
                    } else if (objAttribute.getType() == double.class || objAttribute.getType() == Double.class) {
                        newObject = ((Number) newObject).doubleValue();
                    } else if (objAttribute.getType() == float.class || objAttribute.getType() == Float.class) {
                        newObject = ((Number) newObject).floatValue();
                    } else if (objAttribute.getType() == long.class || objAttribute.getType() == Long.class) {
                        newObject = ((Number) newObject).longValue();
                    } else if (objAttribute.getType() == boolean.class || objAttribute.getType() == Boolean.class) {
                        newObject = (newObject instanceof Number) ? ((Number) newObject).intValue() != 0 : Boolean.parseBoolean(newObject.toString());
                    }

                    objAttribute.set(instance, newObject);
                }
            }

            return instance;
        } catch (NoSuchMethodException e) {
            throw new SQLException("[No existe un constructor por defecto definido en la clase '" + objectClass.getSimpleName() + "']: " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new SQLException("[Error al instanciar la clase '" + objectClass.getSimpleName() + "'. Posible ausencia o exceso de variables definidas en la clase]: " + e.getMessage());
        }
    }
}