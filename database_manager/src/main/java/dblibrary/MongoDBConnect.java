package dblibrary;
/*import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
public class MongoDBConnect {
    private static String MONGO_URI;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDatabase connectionMongo(String pHost, String pPort, String databaseName) {
        MONGO_URI = "mongodb://" + pHost + ":" + pPort;
        mongoClient = MongoClients.create(MONGO_URI);
        database = mongoClient.getDatabase(databaseName);
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}*/
