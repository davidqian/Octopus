package com.my.Octopus.database.mongo;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by davidqian on 2017/6/17.
 */
public class MongoUtil {

    private static final Logger logger = LoggerFactory.getLogger(MongoUtil.class);

    private MongoClient mongoClient;

    public MongoUtil(String ip, int port, List<MongoCredential> mongoCredentials) {

//		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
//		build.connectionsPerHost(50);   //与目标数据库能够建立的最大connection数量为50
//		build.threadsAllowedToBlockForConnectionMultiplier(50); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
//		build.connectTimeout(1000 * 60 * 1);    //与数据库建立连接的timeout设置为1分钟
//		MongoClientOptions myOptions = build.build();

//		MongoCredential credential = MongoCredential.createCredential("siteRootAdmin", "admin", "mongodb_password".toCharArray());
//		this.mongoClient = new MongoClient(new ServerAddress("192.168.5.207", 31000), Arrays.asList(credential), myOptions);

        this.mongoClient = new MongoClient(new ServerAddress(ip, port), mongoCredentials);
    }

    public MongoUtil(String uri) {
//		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://siteRootAdmin:mongodb_password@192.168.5.207:31000,192.168.5.207:32000?replicaSet=replset_1");
        MongoClientURI mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
    }

    public String insert(String dbName, String collectionName, String jsonValue) {
        if (jsonValue == null || jsonValue.length() == 0) {
            logger.error("MongoDBUtil INSERT ERROR:values is null");
            return null;
        }

        try {
            DB db = this.mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            DBObject insertObj = (DBObject) com.mongodb.util.JSON.parse(jsonValue);
            WriteResult result = dbCollection.insert(insertObj);

            if (result.wasAcknowledged()) {
                return insertObj.get("_id").toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            logger.error("MongoDBUtil INSERT ERROR", e);
        }

        return null;
    }

    public <T extends MongoModel> String insert(String dbName, String collectionName, T value) {
        String jsonValue = new Gson().toJson(value);
        return this.insert(dbName, collectionName, jsonValue);
    }

    public void delete(String dbName, String collectionName, Map<String, Object> query) {
        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil DELETE ERROR:query is null");
            return;
        }

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            BasicDBObject doc = new BasicDBObject();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (entry.getKey().equals("_id")) {
                    String id = entry.getValue().toString();
                    doc.put(entry.getKey(), new ObjectId(id));
                } else {
                    doc.put(entry.getKey(), entry.getValue());
                }
            }

            dbCollection.remove(doc);
        } catch (Exception e) {
            logger.error("MongoDBUtil DELETE ERROR", e);
            e.printStackTrace();
        }
    }

    public void drop(String dbName, String collectionName) {
        DB db = mongoClient.getDB(dbName);
        db.getCollection(collectionName).drop();
    }

    public void update(String dbName, String collectionName,
                       Map<String, Object> query, String jsonValue) {

        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (jsonValue == null || jsonValue.length() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        BasicDBObject oldValue = new BasicDBObject();
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (entry.getKey().equals("_id")) {
                oldValue.put(entry.getKey(), new ObjectId(entry.getValue().toString()));
            } else {
                oldValue.put(entry.getKey(), entry.getValue());
            }
        }

        DBObject newValue = (DBObject) com.mongodb.util.JSON.parse(jsonValue);

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);
            dbCollection.update(oldValue, newValue);

        } catch (Exception e) {
            logger.error("MongoDBUtil UPDATE ERROR", e);
        }
    }


    public void update(String dbName, String collectionName,
                       String query, String newValues) {

        if (query == null || query.length() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (newValues == null || newValues.length() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        BasicDBObject oldValue = (BasicDBObject) com.mongodb.util.JSON.parse(query);
        BasicDBObject newValue = (BasicDBObject) com.mongodb.util.JSON.parse(newValues);

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);
            dbCollection.update(oldValue, newValue);
        } catch (Exception e) {
            logger.error("MongoDBUtil UPDATE ERROR", e);
        }
    }

    public void updateColumn(String dbName, String collectionName,
                             Map<String, Object> query, Map<String, Object> newValues) {
        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (newValues == null || newValues.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        BasicDBObject oldValue = new BasicDBObject();
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            oldValue.put(entry.getKey(), entry.getValue());
        }

        BasicDBObject newValue = new BasicDBObject();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            newValue.put(entry.getKey(), entry.getValue());
        }

        BasicDBObject doc = new BasicDBObject();
        doc.put("$set", newValue);

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);
            dbCollection.update(oldValue, doc, true, false);
        } catch (Exception e) {
            logger.error("MongoDBUtil UPDATE ERROR", e);
        }
    }


    public String find(String dbName, String collectionName, String[] findColumns,
                       Map<String, Object> query) {
        String result = "";

        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil FIND ERROR:query is null");
            return result;
        }

        if (findColumns == null || findColumns.length == 0) {
            logger.error("MongoDBUtil FIND ERROR:findColumns is null");
            return result;
        }

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            //构建查询条件
            BasicDBObject queryObj = new BasicDBObject();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (entry.getKey().equals("_id")) {
                    queryObj.put(entry.getKey(), new ObjectId(entry.getValue().toString()));
                } else {
                    queryObj.put(entry.getKey(), entry.getValue());
                }
            }

            //构建查询列
            BasicDBObject keyObj = new BasicDBObject();
            for (String key : findColumns) {
                keyObj.put(key, true);
            }

            //查询获取数据

            DBCursor cursor = dbCollection.find(queryObj, keyObj);
            result = cursor.toString();
        } catch (Exception e) {
            logger.error("MongoDBUtil FIND ERROR", e);
        }

        return result;
    }

    public String findAll(String dbName, String collectionName, String[] findColumns) {
        String result = "";

        if (findColumns == null || findColumns.length == 0) {
            logger.error("MongoDBUtil FIND ERROR:findColumns is null");
            return result;
        }

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            //构建查询列
            BasicDBObject keyObj = new BasicDBObject();
            for (String key : findColumns) {
                keyObj.put(key, true);
            }

            //查询获取数据
            DBCursor cursor = dbCollection.find(null, keyObj);
            List<DBObject> dbObjects = cursor.toArray();
            return com.mongodb.util.JSON.serialize(dbObjects);
        } catch (Exception e) {
            logger.error("MongoDBUtil FIND_ALL ERROR", e);
        }

        return result;
    }

    public String findOne(String dbName, String collectionName, String[] findColumns,
                          Map<String, Object> query) {
        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil FIND_ONE ERROR:values is null");
            return null;
        }

        if (findColumns == null || findColumns.length == 0) {
            logger.error("MongoDBUtil FIND ERROR:findColumns is null");
            return null;
        }

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            //构建查询条件
            BasicDBObject queryObj = new BasicDBObject();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof ObjectId) {
                    queryObj.put(entry.getKey(), new ObjectId(entry.getValue().toString()));
                } else {
                    queryObj.put(entry.getKey(), entry.getValue());
                }
            }

            //构建查询列
            BasicDBObject keyObj = new BasicDBObject();
            for (String key : findColumns) {
                keyObj.put(key, true);
            }

            //查询获取数据
            return dbCollection.findOne(queryObj, keyObj).toString();

        } catch (Exception e) {
            logger.error("MongoDBUtil FIND_ONE ERROR", e);
            e.printStackTrace();
        }

        return null;
    }

    public Map findOneObj(String dbName, String collectionName, String[] findColumns,
                          Map<String, Object> query) {
        if (query == null || query.size() == 0) {
            logger.error("MongoDBUtil FIND_ONE ERROR:values is null");
            return null;
        }

        if (findColumns == null || findColumns.length == 0) {
            logger.error("MongoDBUtil FIND ERROR:findColumns is null");
            return null;
        }

        try {
            DB db = mongoClient.getDB(dbName);
            DBCollection dbCollection = db.getCollection(collectionName);

            //构建查询条件
            BasicDBObject queryObj = new BasicDBObject();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof ObjectId) {
                    queryObj.put(entry.getKey(), new ObjectId(entry.getValue().toString()));
                } else {
                    queryObj.put(entry.getKey(), entry.getValue());
                }
            }

            //构建查询列
            BasicDBObject keyObj = new BasicDBObject();
            for (String key : findColumns) {
                keyObj.put(key, true);
            }

            //查询获取数据
            DBObject one = dbCollection.findOne(queryObj, keyObj);
            return one.toMap();

        } catch (Exception e) {
            logger.error("MongoDBUtil FIND_ONE ERROR", e);
            e.printStackTrace();
        }

        return null;
    }

    public long count(String dbName, String collectionName) {
        DBCollection collection = mongoClient.getDB(dbName).getCollection(collectionName);
        return collection.count();
    }

}
