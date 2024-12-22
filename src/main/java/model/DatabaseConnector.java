package model;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


class DatabaseConnector {
    private static final String connString = "<STRING DELETED FOR SECURITY REASONS>";
    private static MongoDatabase database;
    private static MongoClient client;

    private static void instantiate() {
        if(database != null) {
            return;
        }
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try {
            client = MongoClients.create(settings);
            // Send a ping to confirm a successful connection
            database = client.getDatabase("shelfshare");
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static MongoDatabase getDatabase() {
        instantiate();
        return database;
    }

    public static MongoCollection<Document> getCollection(String name) {
        instantiate();
        return database.getCollection(name);
    }
}
