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
import java.util.HashSet;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 *
 * Wrapper around jedis for mapping of classes to tables
 * Author: Ilya Semerhanov
 * Date: 07.08.13
 */
public class MatchingDBHandler {

    public static final Logger LOGGER = Logger.getLogger(MatchingDBHandler.class);

    private static volatile MatchingDBHandler instance;
    public static String settings;
    private static JedisPool pool;
    private static Jedis redis;

    private MatchingDBHandler(){

    }

    public static MatchingDBHandler getInstance(String filePath) {
        MatchingDBHandler localInstance = instance;
        if (localInstance == null) {
            synchronized (MatchingDBHandler.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MatchingDBHandler();
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
        LOGGER.info("[MatchingDBHandler] Loading Redis settings: " + settings);

        Map allSettings = (Map) yaml.load(input);
        Map redisSettings = (Map) allSettings.get("RedisServer");
        String hostname = redisSettings.get("hostname").toString();
        Integer port = (Integer) redisSettings.get("port");

        LOGGER.info("[MatchingDBHandler] Connecting to Redis server " + hostname + ":" + port.toString());
        pool = new JedisPool(new JedisPoolConfig(), hostname, port);
        redis = pool.getResource();
        pool.returnResource(redis);
        LOGGER.info("[MatchingDBHandler] Connection established");
    }

    public static void addClassTable(String key,String tableName, String className){
        redis = pool.getResource();
        redis.select(0);
        try {
            redis.hset(key,tableName,className);
            LOGGER.info("[MatchingDBHandler] New key/value pair added: " + key + " -> " + tableName);
            LOGGER.debug("[MatchingDBHandler] key: " + key + " field: " + tableName + " value: " + className);
        } finally {
            pool.returnResource(redis);
        }
    }

    public static void addDataTypeProperty(String key,String columnName, String DataTypeProperty){
        redis = pool.getResource();
        redis.select(0);
        try {
            redis.hset(key,columnName,DataTypeProperty);
            LOGGER.info("[MatchingDBHandler] New key/value pair added: " + key + " -> " + columnName);
            LOGGER.debug("[MatchingDBHandler] key: " + key + " field: " + columnName + " value: " + DataTypeProperty);
        } finally {
            pool.returnResource(redis);
        }
    }

    public static void addIndividualToStorage(String key,String individName, String value){
        redis = pool.getResource();
        redis.select(0);
        try {
            redis.hset(key,individName,value);
            LOGGER.debug("[MatchingDBHandler] Individual: " +  individName+ " was added to key: " + key);
        } finally {
            pool.returnResource(redis);
        }
    }

    public static String getClassTableName(String key,String tableName){
        redis = pool.getResource();
        redis.select(0);
        try {
            String className = redis.hget(key,tableName);
            LOGGER.debug("[MatchingDBHandler] Value for key " + key + " retrieved: " + className);
            return className;
        } finally {
            pool.returnResource(redis);
        }
    }

    public static String getPropertyName(String key,String fieldName){
        redis = pool.getResource();
        redis.select(0);
        try {
            String propertyName = redis.hget(key,fieldName);
            LOGGER.debug("[MatchingDBHandler] Value for key " + key + " retrieved: " + propertyName);
            return propertyName;
        } finally {
            pool.returnResource(redis);
        }
    }

    public static Map<String, String> getAllIndividuals(String key){
        redis = pool.getResource();
        redis.select(0);
        try {
            Map<String, String> allIndividuals = redis.hgetAll(key);
            LOGGER.debug("[MatchingDBHandler] Get all values for key " + key);
            return allIndividuals;
        } finally {
            pool.returnResource(redis);
        }
    }

    public static void addIndividualSimilarity(String key, String field, String value){
        redis = pool.getResource();
        redis.select(1); // Index 1 for Similarity
        try {
            redis.hset(key,field,value);
            LOGGER.debug("[MatchingDBHandler] Individual: " +  field + " similarity was added");
        } finally {
            pool.returnResource(redis);
        }
    }

    public static HashSet<String> getAllSimilarIndividuals(){
        redis = pool.getResource();
        redis.select(1);
        try {
            HashSet<String> allIndividuals = (HashSet) redis.keys("*");
            LOGGER.debug("[MatchingDBHandler] Get all similar individuals");
            return allIndividuals;
        } finally {
            pool.returnResource(redis);
        }
    }

    public static Map<String, String> getSingleSimilarIndividual(String key){
        redis = pool.getResource();
        redis.select(1);
        try {
            Map<String, String> singleIndividuals = redis.hgetAll(key);
            LOGGER.debug("[MatchingDBHandler] Get all single individual for key " + key);
            return singleIndividuals;
        } finally {
            pool.returnResource(redis);
        }
    }



    public static void flushSimilarityDB(){
        redis = pool.getResource();
        redis.select(1);
        try {
            LOGGER.info("[MatchingDBHandler] Cleaning Redis similarity dataset");
            redis.flushDB();
        } finally {
            pool.returnResource(redis);
        }
    }

    public static void saveDatasetToDisk(){
        redis = pool.getResource();
        redis.select(0);
        try {
            redis.save();
            LOGGER.info("[MatchingDBHandler] Redis dataset was saved to disk");
        } finally {
            pool.returnResource(redis);
        }
    }
    public static void flushDB(){
        redis = pool.getResource();
        redis.select(0);
        try {
            LOGGER.info("[MatchingDBHandler] Cleaning Redis dataset");
            redis.flushDB();
        } finally {
            pool.returnResource(redis);
        }
    }

}
