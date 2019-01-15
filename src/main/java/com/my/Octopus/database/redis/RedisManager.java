package com.my.Octopus.database.redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisManager {
    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);
    private static final RedisManager instance = new RedisManager();
    private static Map<String, RedisUtil> redisMap = new HashMap<String, RedisUtil>();

    public static RedisManager getInstance() {
        return instance;
    }

    public RedisUtil addRedis(String index, int maxIdle, int maxTotal, int maxWaitMillis, String host, int port, String password) {
        RedisUtil redis = new RedisUtil();
        redis.init(maxIdle, maxTotal, maxWaitMillis, host, port, password);
        redisMap.put(index, redis);
        return redis;
    }

    public void addRedis(String index, RedisUtil redisUtil) {
        redisMap.put(index, redisUtil);
    }

    public RedisUtil getRedis(String index) {
        if (redisMap == null) {
            return null;
        }
        return redisMap.get(index);
    }


    /**
     * 随机返回一个redis实例，需要区分redis index的模块禁止使用
     *
     * @return 随机redis实例
     */
    public RedisUtil getRandomRedis() {
        if (redisMap == null) {
            return null;
        }

        Iterator<RedisUtil> iterator = redisMap.values().iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }
}
