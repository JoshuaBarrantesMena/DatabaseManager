package dblibrary;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
public class MongoDBConnect {
    private static String MONGO_URI;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDatabase connectionMongo(String URI, String dataBName) {
        MONGO_URI = URI;
        mongoClient = MongoClients.create(MONGO_URI);
        database = mongoClient.getDatabase(dataBName);
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
