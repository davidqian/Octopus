package com.my.Octopus.database.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Redis工具
 *
 * @author davidqian
 */
public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private JedisPool pool = null;

    /**
     * 初始化redis
     *
     * @param maxIdle       最大空闲连接数
     * @param maxTotal      最大连接数
     * @param maxWaitMillis
     * @param host
     * @param port
     * @param password
     */
    public void init(int maxIdle, int maxTotal, int maxWaitMillis, String host, int port, String password) {
        logger.debug("Initialized RedisUtil with host: " + host + ", port: " + String.valueOf(port) + ", maxIdle: " + String.valueOf(maxIdle) + ", maxTotal: " + String.valueOf(maxTotal) + ", maxWaitMillis: " + String.valueOf(maxWaitMillis));
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);

        if (StringUtils.isEmpty(password)) {
            pool = new JedisPool(poolConfig, host, port, maxWaitMillis);
        } else {
            pool = new JedisPool(poolConfig, host, port, maxWaitMillis, password);
        }
    }

    private Jedis getClient() {
        if (pool == null) {
            logger.debug("Uninitialize RedisUtil!!!!!");
            return null;
        }
        return pool.getResource();
    }

    /**
     * key operations
     *
     * @throws Exception
     */
    public String get(String redisKey) throws Exception {
        return get0(redisKey);
    }

    public String get0(String redisKey) throws Exception {
        Jedis jedis = null;
        String aesKey = null;
        try {
            jedis = getClient();
            aesKey = jedis.get(redisKey);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return aesKey;
    }

    public byte[] get(byte[] redisKey) {
        Jedis jedis = null;

        byte[] aesKey = new byte[0];
        try {
            jedis = getClient();
            aesKey = jedis.get(redisKey);
        } catch (Exception e) {
            logger.error("byte get error, error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return aesKey;
    }

    public boolean exists(String key) {
        Jedis jedis = null;
        boolean exists = false;
        try {
            jedis = getClient();
            exists = jedis.exists(key);
        } catch (Exception e) {
            logger.error("exists error, error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return exists;
    }

    public void set(String key, String value) {
        try {
            set0(key, value);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        }
    }

    public void set0(String key, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.set(key, value);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public void set(byte[] key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public boolean setex(String redisKey, int time, String stamp) {
        Jedis jedis = null;
        boolean ret = false;
        try {
            jedis = getClient();
            jedis.setex(redisKey, time, stamp);
            ret = true;
        } catch (Exception e) {
            logger.error("redis setex data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return ret;
    }

    public void setex(byte[] redisKey, int time, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.setex(redisKey, time, value);
        } catch (Exception e) {
            logger.error("redis setex data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public Long setnx(String key, String value) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getClient();
            res = jedis.setnx(key, value);
        } catch (Exception e) {
            logger.error("redis setnx data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.del(key);
        } catch (Exception e) {
            logger.error("redis del data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public Set<String> keys(String keys) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getClient();
            res = jedis.keys(keys);
        } catch (Exception e) {
            logger.error("redis keys data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public long incr(String key) {
        Jedis jedis = null;
        long res = 0;
        try {
            jedis = getClient();
            res = jedis.incr(key);
        } catch (Exception e) {
            logger.error("redis incr data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public long incrBy(String key, long num) {
        Jedis jedis = null;
        long res = 0;
        try {
            jedis = getClient();
            res = jedis.incrBy(key, num);
        } catch (Exception e) {
            logger.error("redis incr data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public void expire(String key, int time) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.expire(key, time);
        } catch (Exception e) {
            logger.error("redis expire data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public void expire(byte[] key, int time) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.expire(key, time);
        } catch (Exception e) {
            logger.error("redis expire data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    /**
     * list operations
     */
    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        List<String> list = null;
        try {
            jedis = getClient();
            list = jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("redis lrange data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return list;
    }

    public void lrem(String key, int pos, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.lrem(key, pos, value);
        } catch (Exception e) {
            logger.error("redis lrem data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public void lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("redis lpush data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public long llen(String key) {
        Jedis jedis = null;
        long num = 0;
        try {
            jedis = getClient();
            num = jedis.llen(key);
        } catch (Exception e) {
            logger.error("redis lpush data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return num;
    }

    public void rpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.rpush(key, value);
        } catch (Exception e) {
            logger.error("redis rpush data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    /**
     * hash operations
     */
    public Set<String> hkeys(String key) {
        Jedis jedis = null;
        Set<String> res = Sets.newHashSet();
        try {
            jedis = getClient();
            res = jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("redis hkeys error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Set<String> hkeysExcept(String key) throws JedisException {
        Jedis jedis;
        Set<String> res = Sets.newHashSet();

        jedis = getClient();

        if (jedis == null) {
            throw new JedisConnectionException("cannot get connection from pool");
        }

        try {
            res = jedis.hkeys(key);
        } catch (JedisException e) {
            throw e;
        } finally {
            jedis.close();
        }

        return res;
    }

    public byte[] hget(byte[] key, byte[] field) {
        Jedis jedis = null;
        byte[] res = null;
        try {
            jedis = getClient();
            res = jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("redis hget data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        Map<String, String> res = Maps.newHashMap();
        try {
            jedis = getClient();
            res = jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("redis hgetAll error, {}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        String res = "";
        try {
            jedis = getClient();
            res = jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("redis hget data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public void hset(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("redis hset data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("redis hset data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public void hsetnx(String key, String field, String value) throws JedisException {
        Jedis jedis = null;
        jedis = getClient();

        if (jedis == null) {
            throw new JedisConnectionException("cannot get connection from pool");
        }
        try {
            jedis.hsetnx(key, field, value);
        } catch (JedisException e) {
            logger.error("redis hset data error:{}", e);
            throw e;
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public void hdel(String key, String fields) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.hdel(key, fields);
        } catch (Exception e) {
            logger.error("redis hdel data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        List<String> l = Lists.newArrayList();
        try {
            jedis = getClient();
            l = jedis.hmget(key, fields);
        } catch (Exception e) {
            logger.error("redis hmget data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return l;
    }

    public List<String> hmgetExcept(String key, String... fields) throws JedisException {
        Jedis jedis;
        List<String> l = Lists.newArrayList();

        jedis = getClient();

        if (jedis == null) {
            throw new JedisConnectionException("cannot get connection from pool");
        }

        try {
            l = jedis.hmget(key, fields);
        } catch (JedisException e) {
            throw e;
        } finally {
            jedis.close();
        }

        return l;
    }

    public void hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.hmset(key, hash);
        } catch (Exception e) {
            logger.error("redis hmset data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public boolean hexists(String key, String field) {
        Jedis jedis = null;
        boolean exists = false;
        try {
            jedis = getClient();
            exists = jedis.hexists(key, field);
        } catch (Exception e) {
            logger.error("redis hexists data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return exists;
    }

    public long hlen(String key) {
        Jedis jedis = null;
        long len = 0;
        try {
            jedis = getClient();
            len = jedis.hlen(key);
        } catch (Exception e) {
            logger.error("redis hlen data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return len;
    }

    public long hincrBy(String key, String format, long num) {
        Jedis jedis = null;
        long res = 0;
        try {
            jedis = getClient();
            res = jedis.hincrBy(key, format, num);
        } catch (Exception e) {
            logger.error("redis hincrBy data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    /**
     * sorted set operation
     */
    public void zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error("redis zadd data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public void zadd(String key, Map<String, Double> map) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.zadd(key, map);
        } catch (Exception e) {
            logger.error("redis zadd data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public Long zcard(String key) {
        Jedis jedis = null;
        long count = 0;
        try {
            jedis = getClient();
            count = jedis.zcard(key);
        } catch (Exception e) {
            logger.error("redis zcard data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return count;
    }

    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        Set<String> sets = null;
        try {
            jedis = getClient();
            sets = jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrange data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return sets;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        Set<String> sets = null;
        try {
            jedis = getClient();
            sets = jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrevrange data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return sets;
    }

    public Double zscore(String key, String id) {
        Jedis jedis = null;
        Double score = null;
        try {
            jedis = getClient();
            score = jedis.zscore(key, id);
        } catch (Exception e) {
            logger.error("redis zscore data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return score;
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        Jedis jedis = null;
        Set<Tuple> res = null;
        try {
            jedis = getClient();
            res = jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrangeWithScores error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        Jedis jedis = null;
        Set<Tuple> res = null;
        try {
            jedis = getClient();
            res = jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrevrangeWithScores data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, int start, int end) {
        Jedis jedis = null;
        Set<Tuple> res = null;
        try {
            jedis = getClient();
            res = jedis.zrevrangeByScoreWithScores(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrevrangeByScoreWithScores data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, int start, int end) {
        Jedis jedis = null;
        Set<Tuple> res = null;
        try {
            jedis = getClient();
            res = jedis.zrangeByScoreWithScores(key, start, end);
        } catch (Exception e) {
            logger.error("redis zrangeByScoreWithScores data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public void zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("redis zrem data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public Long zrank(String key, String field) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getClient();
            // logger.debug("key {} field {}", key, field);
            res = jedis.zrank(key, field);
        } catch (Exception e) {
            logger.error("redis zrank data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public Long zrevrank(String key, String field) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getClient();
            // logger.debug("key {} field {}", key, field);
            res = jedis.zrevrank(key, field);
        } catch (Exception e) {
            logger.error("redis zrevrank data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public double zincrby(String key, int value, String field) {
        Jedis jedis = null;
        double res = 0;
        try {
            jedis = getClient();
            res = jedis.zincrby(key, value, field);
        } catch (Exception e) {
            logger.error("redis zincrby data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    public double zincrby(String key, double value, String field) {
        Jedis jedis = null;
        double res = 0;
        try {
            jedis = getClient();
            res = jedis.zincrby(key, value, field);
        } catch (Exception e) {
            logger.error("redis zincrby data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return res;
    }

    /**
     * set
     */
    public void sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public Long srem(String key, String value) {
        Jedis jedis = null;
        Long ret = 0L;
        try {
            jedis = getClient();
            ret = jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return ret;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        boolean ret = false;
        try {
            jedis = getClient();
            ret = jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return ret;
    }

    public long scard(String key) {
        Jedis jedis = null;
        long ret = 0;
        try {
            jedis = getClient();
            ret = jedis.scard(key);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return ret;
    }

    public Set<String> smembers(String key) {
        Jedis jedis = null;
        Set<String> ret = null;
        try {
            jedis = getClient();
            ret = jedis.smembers(key);
        } catch (Exception e) {
            logger.error("redis set data error:{}", e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return ret;
    }

    /**
     * 保存任意对象到redis
     *
     * @param key        key关键字
     * @param value      要保存的对象
     * @param expireTime 生存期 (秒),没有生存期请传0
     */
    public void setObject(String key, Object value, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            String lastValue = "";
            if (value instanceof String) {
                lastValue = (String) value;
            } else {
                lastValue = JSON.toJSONString(value);
            }
            if (expireTime > 0) {
                jedis.setex(key, expireTime, lastValue);
            } else {
                jedis.set(key, lastValue);
            }
        } catch (Exception e) {
            logger.error("redis set data error,key:{},error:{}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 保存任意对象到redis
     *
     * @param id         唯一ID
     * @param preKey     key关键字
     * @param value      要保存的对象
     * @param expireTime 生存期 (秒),没有生存期请传0
     */
    public void setObject(Object id, String preKey, Object value, int expireTime) {
        String key = buildKey(id, preKey);
        setObject(key, value, expireTime);
    }

    /**
     * 从redis中获取对象
     *
     * @param key  key关键字
     * @param type 放进去的是什么，取得时候用什么形式取，如 new TypeReference<Map<Integer,Test>>() {}
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, TypeReference<T> type) {
        Jedis jedis = null;
        T t = null;
        try {
            jedis = getClient();
            String value = jedis.get(key);
            if (value != null) {
                if (String.class.equals(type.getType())) {
                    t = (T) value;
                } else {
                    t = JSON.parseObject(value, type);
                }
                return t;
            }
        } catch (Exception e) {
            logger.error("redis get data error,key:{}, error:{}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 从redis中获取对象
     *
     * @param id     唯一ID
     * @param preKey key关键字
     * @param type   放进去的是什么，取得时候用什么形式取，如 new TypeReference<Map<Integer,Test>>() {}
     */
    public <T> T getObject(Object id, String preKey, TypeReference<T> type) {
        String key = buildKey(id, preKey);
        return getObject(key, type);
    }

    /**
     * 从redis中获取对象
     *
     * @param key  key关键字
     * @param type 放进去的是什么，取得时候用什么形式，如 Test.class
     */

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        Jedis jedis = null;
        T t = null;
        try {
            jedis = getClient();
            String value = jedis.get(key);
            if (value != null) {
                if (String.class.equals(clazz)) {
                    t = (T) value;
                } else {
                    t = JSON.parseObject(value, clazz);
                }
                return t;
            }
        } catch (Exception e) {
            logger.error("redis get data error,key:{}, error:{}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 从redis中获取对象
     *
     * @param id     唯一ID
     * @param preKey key关键字
     * @param type   放进去的是什么，取得时候用什么形式，如 Test.class
     */
    public <T> T getObject(Object id, String preKey, Class<T> clazz) {
        String key = buildKey(id, preKey);
        return getObject(key, clazz);
    }

    private String buildKey(Object id, String preKey) {
        return preKey + ":" + id;
    }
}