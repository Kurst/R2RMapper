package edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.ICacheService;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

/**
 * R2R Mapper. It is a free software.
 * RedisCaching implementation
 *
 * Author: Ilya Semerhanov
 * Date: 13.10.13
 */
public class RedisCacheImpl implements ICacheService{

    public static final Logger LOGGER=Logger.getLogger(RedisCacheImpl.class);

    private static JedisPool pool;
    private static Jedis redis;


    public void connect(String hostname, Integer port) throws R2RMapperException {

        try{
            LOGGER.info("[R2RMapperCaching] Connecting to Redis server " + hostname + ":" + port.toString());
            pool = new JedisPool(new JedisPoolConfig(), hostname, port);
            redis = pool.getResource();
            pool.returnResource(redis);
            LOGGER.info("[R2RMapperCaching] Connection established");
        }catch (Exception e){
            throw new R2RMapperException("Connection to Redis server could not be established. Caused by: " + getStackTrace(e));
        }

    }

    @Override
    public void store(String key, String value){
        redis = pool.getResource();
        redis.select(2); //Pool 2 for caching of web app
        try {
            redis.set(key,value);
            LOGGER.info("[R2RMapperCaching] key=" + key + " | value=" + value + " stored");
        } finally {
            pool.returnResource(redis);
        }

    }

    public void storeGroup(String group, String key, String value){
        redis = pool.getResource();
        redis.select(2); //Pool 2 for caching of web app
        try {
            redis.hset(group,key,value);
            LOGGER.info("[R2RMapperCaching] group=" + group + " | key=" + key + " | value=" + value + " stored");
        } finally {
            pool.returnResource(redis);
        }

    }

    @Override
    public String fetchValue(String key){
        redis = pool.getResource();
        redis.select(2);
        try {
            String value = redis.get(key);
            LOGGER.info("[R2RMapperCaching] Read value from key=" + key);
            return value;
        } finally {
            pool.returnResource(redis);
        }
    }

    public String fetchValueFromGroup(String group, String key){
        redis = pool.getResource();
        redis.select(2);
        try {
            String value = redis.hget(group, key);
            LOGGER.info("[R2RMapperCaching] Read value from group=" + group + " | key=" + key);
            return value;
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public  void clear(){
        redis = pool.getResource();
        redis.select(2);
        try {
            LOGGER.info("[R2RMapperCaching] Clearing Redis cache");
            redis.flushDB();
        } finally {
            pool.returnResource(redis);
        }
    }

    public  void clearPool(Integer id){
        redis = pool.getResource();
        redis.select(id);
        try {
            LOGGER.info("[R2RMapperCaching] Clearing Redis cache from pool: " + id);
            redis.flushDB();
        } finally {
            pool.returnResource(redis);
        }
    }


}
