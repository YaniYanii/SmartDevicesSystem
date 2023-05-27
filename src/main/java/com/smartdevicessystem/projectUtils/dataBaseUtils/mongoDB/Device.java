package com.smartdevicessystem.projectUtils.dataBaseUtils.mongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;

public class Device extends Product{
    protected final String serialNumber;
    public Device(MongoClient mongoClient, JSONObject productInfo) {
        super(mongoClient, productInfo);
        serialNumber = productInfo.getString("serial_number");
    }

    @Override
    public void write(String nameDB) {

       MongoDatabase db = mongoClient.getDatabase(nameDB);
        Document product = db.getCollection("products").find(new Document("model", model)).first();
        ArrayList devices = (ArrayList)product.get("devices");

        Document curDevice = new Document().append("serial_number",serialNumber).append("updates" ,new Document());
        

    }
}
