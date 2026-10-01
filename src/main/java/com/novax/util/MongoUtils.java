package com.novax.util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoUtils {
    static Logger logger = Logger.getLogger(MongoUtils.class);


    public static MongoCollection<Document> getMongoConnection(String database, String collection) {
        String host = PropertyUtil.getMongoHost();
        String port = PropertyUtil.getMongoPort();
        String username = PropertyUtil.getMongoUser();
        String password = PropertyUtil.getMongoPassword();
        String connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
        logger.info("Connected to the database successfully");
        return mongoCollection;
    }

    public static List<String> getRows(MongoCollection<Document> collection, BasicDBObject query) {
        FindIterable<Document> result = collection.find(query);
        ArrayList<String> rows = new ArrayList<>();
        for (Document document : result) {
            rows.add(document.toJson());
        }
        return rows;
    }

    public static void main(String[] args) {
        // MongoCollection<Document> collection = getMongoConnection("superex_grid_trading_dev", "grid_spot_bot");
        // FindIterable<Document> iterable = collection.find();
        // for (Document document : iterable) {
        //     System.out.println(document.toJson());
        // }

        // MongoCollection<Document> collection2 = MongoUtils.getMongoConnection("grid-trading","grid_spot_event");
        // BasicDBObject query3 = new BasicDBObject();
        // query3.put("grid_spot_order_id", 367750267117232128L);
        // System.out.println(getRows(collection2,query3));

        MongoCollection<Document> collection = MongoUtils.getMongoConnection("swaps-copy-trading", "project_info");
        BasicDBObject query = new BasicDBObject();
        query.put("project_id", 3899);
        query.put("leader_id", 20222055);
        List<String> list = getRows(collection, query);
        for (String row : list) {
            System.out.println("row" + row);
        }
    }

}
