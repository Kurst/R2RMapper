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

import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

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

        LOGGER.info("R2RMapper: Loading Redis settings");
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
            redisCache.clear();
            redisCache.storeGroup("settings","redis.hostname",hostname);
            redisCache.storeGroup("settings","redis.port",port.toString());
        } catch (Exception e) {
            try {
                throw new R2RMapperException("Caching server not working. Caused by: " + getStackTrace(e));
            } catch (R2RMapperException e1) {
                LOGGER.error(e.getMessage());
            }
        }

        LOGGER.info("R2RMapper: Loading Ontology settings");
        Map ontologySettings = null;
        try {
            ontologySettings = r2rConfigHandler.getOntologySettings();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        String structureOntologyName = ontologySettings.get("structureOntologyName").toString();
        String dataOntologyName = ontologySettings.get("dataOntologyName").toString();
        String ontologyFormat = ontologySettings.get("ontologyFormat").toString();
        String outputFolder = ontologySettings.get("outputFolder").toString();

        redisCache.storeGroup("settings","ontology.structureOntologyName",structureOntologyName);
        redisCache.storeGroup("settings","ontology.dataOntologyName",dataOntologyName);
        redisCache.storeGroup("settings","ontology.ontologyFormat",ontologyFormat);
        redisCache.storeGroup("settings","ontology.outputFolder",outputFolder);

        System.out.println("##################R2RMAPPER STARTED######################");
    }

    @PreDestroy
    private void shutdown() {
        redisCache.clear();
    }
}
