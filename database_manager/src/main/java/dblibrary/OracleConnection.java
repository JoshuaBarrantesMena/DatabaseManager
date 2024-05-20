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

    public OracleConnection(String username, String password, String host, String port, String edition) throws SQLException {
        
        DatabaseConnect newConnection = new DatabaseConnect();
        this.connection = newConnection.connectWithOracle(username, password, host, port, edition);
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

    public void updateObject(Object currentObject, Object newObject) {
        try {
            // Obtener la clase del objeto existente
            Class<?> objectClass = currentObject.getClass();
            // Obtener el nombre de la tabla basado en el nombre de la clase
            String tableName = objectClass.getSimpleName().toLowerCase(); 
    
            // Verificar si la tabla existe
            if (!tableExist(tableName)) {
                System.out.println("La tabla " + tableName + " no existe.");
                return;
            }
    
            // Verificar si el objeto existente está presente en la tabla
            if (!objectExists(tableName, objectClass, currentObject)) {
                System.out.println("El objeto existente no se encuentra en la tabla.");
                return;
            }
    
            // Actualizar el objeto existente en la tabla
            StringBuilder query = new StringBuilder("UPDATE ").append(tableName).append(" SET ");      //string
        
            Field[] currentObjAttributes = objectClass.getDeclaredFields();

            for (Field objAttribute : currentObjAttributes) {

                objAttribute.setAccessible(true);
                String nombreCampo = objAttribute.getName();
                Object valorCampoActualizado = objAttribute.get(newObject);
    
                // Agregar el campo y su nuevo valor a la consulta de actualización
                if (objAttribute.getType() == String.class) {
                    query.append(nombreCampo).append(" = '").append(valorCampoActualizado).append("', ");
                } else {
                    query.append(nombreCampo).append(" = ").append(valorCampoActualizado).append(", ");
                }
            }
    
            // Eliminar la coma y el espacio extra al final de la consulta de actualización
            query.delete(query.length() - 2, query.length());
    
            // Construir la condición WHERE para identificar el objeto a actualizar
            query.append(" WHERE ");
            for (Field campo : currentObjAttributes) {
                campo.setAccessible(true);
                String nombreCampo = campo.getName();
                Object valorCampoExistente = campo.get(currentObject);
    
                // Agregar el campo y su valor correspondiente a la condición WHERE
                if (campo.getType() == String.class) {
                    query.append(nombreCampo).append(" = '").append(valorCampoExistente).append("' AND ");
                } else {
                    query.append(nombreCampo).append(" = ").append(valorCampoExistente).append(" AND ");
                }
            }
    
            // Eliminar la última "AND" de la condición WHERE
            query.delete(query.length() - 5, query.length());
    
            // Ejecutar la consulta de actualización
            try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                statement.executeUpdate();
                System.out.println("Objeto actualizado en la tabla " + tableName + " correctamente.");
            }
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Error al actualizar el objeto en la tabla: " + e.getMessage());
        }
    }

    public <T> List<T> getAllObjects(Class<T> objectClass) {
        List<T> objList = new ArrayList<>();
        String tableName = objectClass.getSimpleName().toLowerCase(); // Derivar el nombre de la tabla del nombre de la clase
        try {
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) { // construir cada objeto obtenido en un objeto de la clase
                        T object = buildObject(objectClass, resultSet);
                        objList.add(object);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar objetos de la tabla: " + e.getMessage());
        }
        return objList;
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
                    // Convertir tipos si es necesario
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
            throw new SQLException("No se pudo encontrar el constructor predeterminado para la clase " + objectClass.getSimpleName() + ": " + e.getMessage(), e);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new SQLException("Error al instanciar la clase " + objectClass.getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}
