package com.smartdevicessystem.projectUtils.dataBaseUtils.mongoDB;

import com.mongodb.client.MongoClient;
import org.json.JSONObject;

public class Update extends Device{

    public Update(MongoClient mongoClient, JSONObject productInfo) {
        super(mongoClient, productInfo);
    }

    @Override
    public void write(String nameDB) {

    }
}
