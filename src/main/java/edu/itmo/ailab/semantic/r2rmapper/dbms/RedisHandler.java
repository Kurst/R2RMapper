package edu.itmo.ailab.semantic.r2rmapper.dbms;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 *
 * Wrapper around jedis for mappinf of classes to tables
 * Author: Ilya Semerhanov
 * Date: 07.08.13
 */
public class RedisHandler {

    public static final Logger LOGGER = Logger.getLogger(RedisHandler.class);

    private static volatile RedisHandler instance;
    public static String settings;
    private static JedisPool pool;
    private static Jedis jedis;

    private RedisHandler(){

    }

    public static RedisHandler getInstance(String filePath) {
        RedisHandler localInstance = instance;
        if (localInstance == null) {
            synchronized (RedisHandler.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RedisHandler();
                    settings = filePath;
                }
            }
        }
        return localInstance;
    }

    public static void connect()
            throws FileNotFoundException {
        InputStream input = new FileInputStream(new File(settings));
        Yaml yaml = new Yaml();
        LOGGER.info("[RedisHandler] Loading Redis settings: " + settings);

        Map allSettings = (Map) yaml.load(input);
        Map redisSettings = (Map) allSettings.get("RedisServer");
        String hostname = redisSettings.get("hostname").toString();
        Integer port = (Integer) redisSettings.get("port");

        LOGGER.info("[RedisHandler] Connecting to Redis server " + hostname + ":" + port.toString());
        pool = new JedisPool(new JedisPoolConfig(), hostname, port);
        jedis = pool.getResource();
        pool.returnResource(jedis);
        LOGGER.info("[RedisHandler] Connection established");
    }

    public static void addClassMap(String key,String tableName, String className){
        jedis = pool.getResource();
        try {
            jedis.hset(key,tableName,className);
            LOGGER.info("[RedisHandler] New key added: " + key);
            LOGGER.debug("[RedisHandler] key: " + key + " field: " + tableName + " value: " + className);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static void addDataTypeProperty(String key,String columnName, String DataTypeProperty){
        jedis = pool.getResource();
        try {
            jedis.hset(key,columnName,DataTypeProperty);
        } finally {
            pool.returnResource(jedis);
        }
    }

}
