package com.my.Octopus.database.mongo;

import com.my.war.gvg.gvgLogic.database.DatabaseManager;
import com.my.war.gvg.gvgLogic.database.config.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidqian on 2017/6/21.
 */
public class MongoManager {
    private static final MongoManager instance = new MongoManager();
    private static Map<String, MongoUtil> mongoMap = new HashMap<>();
    private static Map<String, AsyncMongoUtil> asyncMongoMap = new HashMap<>();
    private static Map<String, AsyncMongoUtil> asyncLogMongoMap = new HashMap<>();

    public static MongoManager getInstance() {
        return instance;
    }

    public void addMongo(String sec, String uri) {
        MongoUtil mongo = new MongoUtil(uri);
        mongoMap.put(sec, mongo);
    }

    public MongoUtil getMongo(String sec) {
        if (!mongoMap.containsKey(sec)) {
            DatabaseConfig databaseConfig = DatabaseManager.getDatabaseConfig(sec);
            if (databaseConfig == null) {
                return null;
            }

            this.addMongo(sec, databaseConfig.getMongo().getUsers().getCstr());
        }
        return mongoMap.get(sec);
    }

    public void addAsyncMongo(String sec, String uri) {
        AsyncMongoUtil asyncMongoUtil = new AsyncMongoUtil(uri);
        asyncMongoMap.put(sec, asyncMongoUtil);
    }

    public AsyncMongoUtil getAsyncMongo(String sec) {
        if (!asyncMongoMap.containsKey(sec)) {
            DatabaseConfig databaseConfig = DatabaseManager.getDatabaseConfig(sec);
            if (databaseConfig == null) {
                return null;
            }

            this.addAsyncMongo(sec, databaseConfig.getMongo().getUsers().getCstr());
        }
        return asyncMongoMap.get(sec);
    }


    public void addAsyncLogMongo(String sec, String uri) {
        AsyncMongoUtil asyncMongoUtil = new AsyncMongoUtil(uri);
        asyncLogMongoMap.put(sec, asyncMongoUtil);
    }

    public AsyncMongoUtil getAsyncLogMongo(String sec) {
        if (!asyncLogMongoMap.containsKey(sec)) {
            DatabaseConfig databaseConfig = DatabaseManager.getDatabaseConfig(sec);
            if (databaseConfig == null) {
                return null;
            }

            this.addAsyncLogMongo(sec, databaseConfig.getMongo().getLogs().getCstr());
        }
        return asyncLogMongoMap.get(sec);
    }
}
