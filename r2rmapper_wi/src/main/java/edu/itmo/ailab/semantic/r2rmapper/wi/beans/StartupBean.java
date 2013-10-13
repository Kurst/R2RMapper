package edu.itmo.ailab.semantic.r2rmapper.wi.beans;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 * Startup Bean for reading and caching settings and config files
 *
 * Author: Ilya Semerhanov
 * Date: 13.10.13
 */
@Stateless(name = "StartupBeanEJB")
@Singleton
@Startup
public class StartupBean {
    public static final Logger LOGGER=Logger.getLogger(StartupBean.class);

    public StartupBean() {
    }

    @Inject
    private RedisCacheImpl redisCache;

    @PostConstruct
    private void startup() {
        LOGGER.info("R2RMapper: ***Initializing configuration and settings***");

        String settingsPath = System.getProperty("jboss.server.data.dir") + "/settings.yaml";
        InputStream inputSettings = null;
        Yaml yaml = new Yaml();

        try {
            inputSettings = new FileInputStream(new File(settingsPath));
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("[R2RMapper] Loading settings from: " + settingsPath);
        Map allSettings = (Map) yaml.load(inputSettings);
        Map redisSettings = (Map) allSettings.get("RedisServer");
        String hostname = redisSettings.get("hostname").toString();
        Integer port = (Integer) redisSettings.get("port");
        try {
            redisCache.connect(hostname,port);
        } catch (R2RMapperException e) {
            LOGGER.error(e.getMessage());
        }
        redisCache.clear();
        redisCache.storeGroup("settings","redis.hostname",hostname);
        redisCache.storeGroup("settings","redis.port",port.toString());


        System.out.println("##################R2RMAPPER STARTED######################");

    }

    @PreDestroy
    private void shutdown() {

    }
}
