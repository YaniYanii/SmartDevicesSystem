package com.smartdevicessystem.projectUtils.dataBaseUtils.mongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Product {
    protected final MongoClient mongoClient;
    protected final String model;
    public Product(MongoClient mongoClient, JSONObject productInfo){
        this.mongoClient = mongoClient;
        model = productInfo.getString("model");
    }
    public void write(String nameDB){

        MongoDatabase db = mongoClient.getDatabase(nameDB);
        MongoCollection<Document> productCollection = db.getCollection("products");
        Document productDoc = new Document();
        
        productDoc.append("model",model);
        productDoc.append("devices" ,new HashMap<String,Document>());
        
        productCollection.insertOne(productDoc);
    }
}
