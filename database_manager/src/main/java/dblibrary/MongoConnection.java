package dblibrary;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document; 
import java.lang.reflect.Field;

public class MongoConnection {
    private MongoDatabase database;

    public MongoConnection(String pHost, String pPort, String dataBName) {
        MongoDBConnect connect = new MongoDBConnect();
        this.database = connect.connectionMongo(pHost, pPort, dataBName);
    }

    public void sendObject(Object object) {
        try {
            Class<?> objectClass = object.getClass();
            String collectionName = objectClass.getSimpleName().toLowerCase();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            Document doc = new Document();
            for (Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);
                doc.append(field.getName(), field.get(object));
            }

            collection.insertOne(doc);
            System.out.println("Documento insertado en la colección " + collectionName + " correctamente.");
        } catch (IllegalAccessException e) {
            System.err.println("Error al mapear la clase al documento: " + e.getMessage());
        }
    }
}
