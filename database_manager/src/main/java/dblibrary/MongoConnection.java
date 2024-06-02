package dblibrary;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
            System.out.println("\n\ntest1\n\n");
            collection.insertOne(doc);
            System.out.println("\n\ntest2\n\n");
            System.out.println("Objeto insertado en la colección " + collectionName + " correctamente.");
        } catch (Exception e) {
            System.err.println("Error al insertar el objeto en la tabla: //" + e.getMessage());
        }
    }

    private Document toJSON(Object pObject, Class<?> pObjectClass) throws IllegalAccessException {
        Document doc = new Document();
        String idFieldName = pObjectClass.getSimpleName().toLowerCase() + "_id";

        for (Field field : pObjectClass.getDeclaredFields()) {
            field.setAccessible(true); // Permitir acceso a campos privados
            String fieldName = field.getName();
            Object fieldValue = field.get(pObject);

            if (fieldName.equals(idFieldName)) {
                doc.append("_id", fieldValue); // Si el campo es el id, se añade como _id
            } else {
                doc.append(fieldName, fieldValue);
            }
        }
        return doc;
    }
}
