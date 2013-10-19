package edu.itmo.ailab.semantic.r2rmapper.wi.beans;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.R2RConfigurationHandlerImpl;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;
import org.apache.log4j.Logger;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
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

    @Inject
    private R2RConfigurationHandlerImpl r2rConfigHandler;

    @PostConstruct
    private void startup() {
        LOGGER.info("##################R2RMAPPER Initializing configuration and settings##################");

        Map redisSettings = null;
        try {
            redisSettings = r2rConfigHandler.getRedisSettings();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
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
        redisCache.clear();
    }
}
