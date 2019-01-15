package com.my.Octopus.database.mongo;

import com.google.gson.Gson;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by davidqian on 2017/7/8.
 */
public class AsyncMongoUtil {
    private static final Logger logger = LoggerFactory.getLogger(AsyncMongoUtil.class);

    private MongoClient mongoClient;

    public AsyncMongoUtil(String uri) {
        this.mongoClient = MongoClients.create(uri);
    }

    public void insert(String dbName, String collectionName, String jsonValue) {
        if (jsonValue == null || jsonValue.length() == 0) {
            logger.error("AsyncMongoDBUtil INSERT ERROR:values is null");
            return;
        }

        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document document = Document.parse(jsonValue);
        collection.insertOne(document, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void aVoid, Throwable throwable) {
                if (throwable != null) {
                    logger.error(throwable.getMessage(), throwable);
                }
            }
        });
    }

    public <T extends MongoModel> void insert(String dbName, String collectionName, T value) {
        String jsonValue = new Gson().toJson(value);
        this.insert(dbName, collectionName, jsonValue);
    }

    public void updateColumn(String dbName, String collectionName,
                             Map<String, Object> querys, Map<String, Object> newValues) {
        if (querys == null || querys.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (newValues == null || newValues.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        List<Bson> queryBsonList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : querys.entrySet()) {
            Bson eq = Filters.eq(entry.getKey(), entry.getValue());
            queryBsonList.add(eq);
        }
        Bson query = Filters.and(queryBsonList);

        List<Bson> updateBsonList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            Bson set = Updates.set(entry.getKey(), entry.getValue());
            updateBsonList.add(set);
        }
        Bson newValue = Updates.combine(updateBsonList);

        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.updateOne(query, newValue, new SingleResultCallback<UpdateResult>() {
            @Override
            public void onResult(final UpdateResult result, final Throwable t) {
                if (t != null) {
                    logger.error(t.getMessage(), t);
                    return;
                }
            }
        });
    }

    public void updateColumn(String dbName, String collectionName,
                             Map<String, Object> querys, Map<String, Object> newValues, SingleResultCallback<UpdateResult> callback) {
        if (querys == null || querys.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (newValues == null || newValues.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        List<Bson> queryBsonList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : querys.entrySet()) {
            Bson eq = Filters.eq(entry.getKey(), entry.getValue());
            queryBsonList.add(eq);
        }
        Bson query = Filters.and(queryBsonList);

        List<Bson> updateBsonList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            Bson set = Updates.set(entry.getKey(), entry.getValue());
            updateBsonList.add(set);
        }
        Bson newValue = Updates.combine(updateBsonList);

        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.updateOne(query, newValue, callback);
    }

    public void updateColumn(String dbName, String collectionName,
                             Map<String, Object> querys, Bson newValue) {
        if (querys == null || querys.size() == 0) {
            logger.error("MongoDBUtil UPDATE ERROR: query is null");
            return;
        }

        if (newValue == null) {
            logger.error("MongoDBUtil UPDATE ERROR: newValues is null");
            return;
        }

        List<Bson> queryBsonList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : querys.entrySet()) {
            Bson eq = Filters.eq(entry.getKey(), entry.getValue());
            queryBsonList.add(eq);
        }
        Bson query = Filters.and(queryBsonList);

        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.updateOne(query, newValue, new SingleResultCallback<UpdateResult>() {
            @Override
            public void onResult(final UpdateResult result, final Throwable t) {
                if (t != null) {
                    logger.error(t.getMessage(), t);
                    return;
                }
            }
        });
    }

    public void drop(String dbName, String collectionName) {
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.drop(new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void aVoid, Throwable throwable) {

            }
        });

    }

    public void drop(String dbName, String collectionName, SingleResultCallback<Void> callback) {
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.drop(callback);
    }

    public <A extends Collection<Document>> void find(String dbName, String collectionName, Bson bson, A
            var1, SingleResultCallback<A> var2) {
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.find(bson).into(var1, var2);
    }

    public void findOne(String dbName, String collectionName, Bson bson, SingleResultCallback<Document> callback) {
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.find(bson).first(callback);
    }

}
