package com.smartdevicessystem.projectUtils.dataBaseUtils.mongoDB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import  com.smartdevicessystem.projectUtils.dataBaseUtils.IGenericDB;

public class MongoDB implements IGenericDB{

    MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
            .build());
    @Override
    public void createDB(String nameDB, Object infoForCreateDB) {
    }

    @Override
    public void deleteDB(String nameDB) {

    }

    @Override
    public void writeToDB(String nameDB, Object infoWrite) {
        MongoDatabase database = mongoClient.getDatabase(nameDB);




    }

    @Override
    public <T> T readFromDB(String ftomDB, Object infoRead) {
        return null;
    }

    @Override
    public void close() {

    }
}
