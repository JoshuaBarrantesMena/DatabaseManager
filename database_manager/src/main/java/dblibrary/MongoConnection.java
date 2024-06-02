package dblibrary;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception e) {
            System.err.println("Error al actualizar el documento en la colección: " + e.getMessage()); //update
        }
    }

    public <T> void deleteObject(Class<T> clase, Object id) {
        try {
            String collectionName = clase.getSimpleName().toLowerCase();
            MongoCollection<Document> collection = database.getCollection(collectionName);
            Document findDoc = new Document("_id", id);
    
            collection.deleteOne(findDoc);
            System.out.println("Documento eliminado de la colección " + collectionName + " correctamente."); //delete
        } catch (Exception e) {
            System.err.println("Error al eliminar el objeto de MongoDB: " + e.getMessage()); //update
        }
    }

    public <T> List<T> getAllObject(Class<T> pObjectClass) {
        List<T> allObjects = new ArrayList<>();
        String collectionName = pObjectClass.getSimpleName().toLowerCase();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                T instance = buildObject(pObjectClass, doc);
                allObjects.add(instance);
            }
        } catch (Exception e) {
            System.err.println("Error al recuperar objetos de la colección: " + e.getMessage()); //update
        }
        return allObjects;
    }

    public <T> T getObject(Class<T> objectClass, Object id) {
        String collectionName = objectClass.getSimpleName().toLowerCase();
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document query = new Document("_id", id);

        try {
            Document doc = collection.find(query).first();
            if (doc != null) {
                return buildObject(objectClass, doc);
            }
        } catch (Exception e) {
            System.err.println("Error al recuperar el objeto de la colección: " + e.getMessage()); //update
        }
        return null;
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

    private <T> T buildObject(Class<T> objectClass, Document doc) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Constructor<T> constructor = objectClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        T instance = constructor.newInstance();

        for (Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            String variableName = field.getName();
            Object newObject = doc.get(variableName);

            if (newObject == null && variableName.equals(objectClass.getSimpleName().toLowerCase() + "_id")) {
                newObject = doc.get("_id");
            }

            field.set(instance, newObject);
        }

        return instance;
    }
}
