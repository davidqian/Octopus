package com.my.war.match2P.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.database.redis.RedisManager;
import com.my.Octopus.database.redis.RedisUtil;
import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.msgdispatch.HttpDispacth;
import com.my.war.match2P.user.UserData;

public class ThreadQueue {
    private static Logger logger = LoggerFactory.getLogger(ThreadQueue.class);
    public static Map<Integer, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>>> threadMap = new HashMap<Integer, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>>>();
    public static LinkedBlockingDeque<UserData> callbackQueue = new LinkedBlockingDeque<UserData>();
    public static ConcurrentHashMap<String, UserData> UsersMap = new ConcurrentHashMap<String, UserData>();
    public static AtomicInteger matcheIn = new AtomicInteger(0);
    public static AtomicInteger matcheCanNotIn = new AtomicInteger(0);
    public static AtomicInteger matchSucess = new AtomicInteger(0);
    public static AtomicInteger matchTimeout = new AtomicInteger(0);
    public static AtomicInteger matchCallbackOk = new AtomicInteger(0);
    public static AtomicInteger matchCallbackOther = new AtomicInteger(0);
    public static AtomicInteger matchCancelOk = new AtomicInteger(0);
    public static AtomicInteger matchCancelFailed = new AtomicInteger(0);
    private static final ThreadQueue instance = new ThreadQueue();

    public static ThreadQueue getInstance() {
        return instance;
    }

    public static void resetAtomic() {
        matcheIn = new AtomicInteger(0);
        matchSucess = new AtomicInteger(0);
        matchTimeout = new AtomicInteger(0);
        matchCallbackOk = new AtomicInteger(0);
        matchCallbackOther = new AtomicInteger(0);
        matchCancelOk = new AtomicInteger(0);
        matchCancelFailed = new AtomicInteger(0);
    }

    public static void printAtomic() {
        if (matcheIn.get() > 0) {
            String printStr = "matchIn nums:" + matcheIn.get();
            printStr += ", matchSucess nums:" + matchSucess.get();
            printStr += ", matchTimeout nums:" + matchTimeout.get();
            printStr += ", matchCallbackOk nums:" + matchCallbackOk.get();
            printStr += ", matchCallbackOther nums:" + matchCallbackOther.get();
            printStr += ", matchCancelOk nums:" + matchCancelOk.get();
            printStr += ", matchCancelFailed nums:" + matchCancelFailed.get();
            logger.info(printStr);
        }
    }

    public boolean addMatchingUser(String uid) {
        RedisUtil redis = RedisManager.getInstance().getRedis(ServerConfig.instance.getRedis().getString("name"));
        String value;
        try {
            String redisKey = getMatchUserKey(uid);
            value = redis.get(redisKey);
            if (value == null) {
                return redis.setex(redisKey, 125, "in");
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public UserData getUserFromUsersMap(String uid) {
        return UsersMap.get(uid);
    }

    public boolean addUsersMap(UserData userData) {
        String uid = userData.getUid();
        if (UsersMap.get(uid) != null) {
            return false;
        } else {
            UsersMap.put(uid, userData);
        }
        return true;
    }

    public void lessUsersMap(String uid) {
        UsersMap.remove(uid);
    }

    public String getMatchUserKey(String uid) {
        return "match_user_" + uid;
    }

    public void lessMatchingUser(String uid) {
        RedisUtil redis = RedisManager.getInstance().getRedis(ServerConfig.instance.getRedis().getString("name"));
        redis.del(getMatchUserKey(uid));
    }

    public void addCallbackQueue(UserData userData) {
        callbackQueue.addFirst(userData);
    }

    public UserData takeCallbackQueue() throws InterruptedException {
        return callbackQueue.takeLast();
    }

    public void initMap() {
        int threadCount = ServerConfig.instance.getThreadCount();
        for (int i = 0; i < threadCount; i++) {
            ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>> linkQueue = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>>();
            threadMap.put(i, linkQueue);
        }
    }

    public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>> getIndexMap(int threadNum) {
        return threadMap.get(threadNum);
    }

    public void addIndexLinkQueue(int threadNum, int index, UserData udata) {
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>> indexMap = getIndexMap(threadNum);
        ConcurrentLinkedQueue<UserData> indexLinkedQueue = indexMap.get(index);
        if (indexLinkedQueue == null) {
            synchronized (String.valueOf(index)) {
                indexLinkedQueue = new ConcurrentLinkedQueue<UserData>();
                ConcurrentLinkedQueue<UserData> tmp = indexMap.putIfAbsent(index, indexLinkedQueue);
                if (tmp != null) {
                    indexLinkedQueue = tmp;
                }
            }
        }
        indexLinkedQueue.add(udata);
    }
}