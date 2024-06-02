package dblibrary;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document; 
import java.lang.reflect.Field;

public class MongoConnection {

    private MongoDatabase database;

    public MongoConnection(String pHost, String pPort, String dataBName) {
        DatabaseConnect connect = new DatabaseConnect();
        this.database = connect.connectionMongo(pHost, pPort, dataBName);
    }

    public void sendObject(Object object) {
        try {
            Class<?> objectClass = object.getClass();
            String collectionName = objectClass.getSimpleName().toLowerCase();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            Document doc = toJSON(object, objectClass);
            collection.insertOne(doc);
            System.out.println("Objeto insertado en la colección " + collectionName + " correctamente."); //delete
        } catch (Exception e) {
            System.err.println("Error al insertar el objeto en la tabla: //" + e.getMessage()); //update
        }
    }

    public void updateObject(Object pNewObject) throws NoSuchFieldException, SecurityException {
        try {
            
            Class<?> objectClass = pNewObject.getClass();
            String collectionName = objectClass.getSimpleName().toLowerCase();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            Document newDoc = toJSON(pNewObject, objectClass);
            String idFiledName = objectClass.getSimpleName().toLowerCase() + "_id";
            Field idField = objectClass.getDeclaredField(idFiledName);
            idField.setAccessible(true);
            Object id = idField.get(pNewObject);

            if (id == null) {
                throw new IllegalArgumentException("El campo ID no puede ser nulo"); //update
            }

            collection.replaceOne(Filters.eq("_id", id), newDoc);
            System.out.println("Objeto actualizado en la colección " + collectionName + " correctamente."); //delete
        } catch (IllegalAccessException e) {
            System.err.println("Error al actualizar el documento en la colección: " + e.getMessage()); //update
        }
    }

    private Document toJSON(Object pObject, Class<?> pObjectClass) throws IllegalAccessException {
        Document doc = new Document();
        String idFieldName = pObjectClass.getSimpleName().toLowerCase() + "_id";

        for (Field field : pObjectClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(pObject);

            if (fieldName.equals(idFieldName)) {
                doc.append("_id", fieldValue);
            } else {
                doc.append(fieldName, fieldValue);
            }
        }
        return doc;
    }
}
