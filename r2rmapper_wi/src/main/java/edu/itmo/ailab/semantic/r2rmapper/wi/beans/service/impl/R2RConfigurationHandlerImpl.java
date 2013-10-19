package edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl;

import edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize.R2ROntologySettings;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize.R2RRedisSettings;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize.R2RSettings;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.IR2RConfigurationHandler;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;


import javax.inject.Inject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 16.10.13
 */

public class R2RConfigurationHandlerImpl implements IR2RConfigurationHandler {

    public static final Logger LOGGER=Logger.getLogger(R2RConfigurationHandlerImpl.class);

    public String settingsPath = System.getProperty("jboss.server.data.dir") + "/settings.yaml";

    public String configPath = System.getProperty("jboss.server.data.dir") + "/config.yaml";

    @Inject
    private RedisCacheImpl redisCache;

    @Override
    public void defineSettingsFile(String path){
        this.settingsPath = path;
    }

    @Override
    public void defineConfigFile(String path){
        this.configPath = path;
    }

    @Override
    public Map getAllConfigs(){
        return null;
    }

    @Override
    public Map getAllSettings(){
        Yaml yaml = new Yaml();
        InputStream inputSettings = readFile(settingsPath);

        LOGGER.info("[R2RMapper] Loading settings from: " + settingsPath);
        Map allSettings = (Map) yaml.load(inputSettings);
        return allSettings;
    }

    @Override
    public void setAllSettings(){
        R2RSettings appSet = new R2RSettings();
        R2RRedisSettings redisSettings = new R2RRedisSettings();
        redisSettings.setPort(Integer.parseInt(redisCache.fetchValueFromGroup("settings","redis.port")));
        redisSettings.setHostname(redisCache.fetchValueFromGroup("settings","redis.hostname"));
        appSet.setRedisServer(redisSettings);

        R2ROntologySettings ontologySettings = new R2ROntologySettings();
        ontologySettings.setStructureOntologyName(redisCache.fetchValueFromGroup("settings","ontology.structureOntologyName"));
        ontologySettings.setDataOntologyName(redisCache.fetchValueFromGroup("settings","ontology.dataOntologyName"));
        ontologySettings.setOntologyFormat(redisCache.fetchValueFromGroup("settings","ontology.ontologyFormat"));
        ontologySettings.setOutputFolder(redisCache.fetchValueFromGroup("settings","ontology.outputFolder"));
        appSet.setOutputOntologies(ontologySettings);
        String output = new Yaml().dumpAsMap(appSet);
        writeFile(settingsPath, output);
    }

    @Override
    public Map getRedisSettings()
            throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputSettings = readFile(settingsPath);

        LOGGER.info("[R2RMapper] Loading settings from: " + settingsPath);
        Map allSettings = (Map) yaml.load(inputSettings);
        Map redisSettings = (Map) allSettings.get("redisServer");
        inputSettings.close();
        return redisSettings;
    }

    @Override
    public Map getOntologySettings()
            throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputSettings = readFile(settingsPath);

        LOGGER.info("[R2RMapper] Loading settings from: " + settingsPath);
        Map allSettings = (Map) yaml.load(inputSettings);
        Map ontologySettings = (Map) allSettings.get("outputOntologies");
        inputSettings.close();
        return ontologySettings;
    }


    private InputStream readFile(String path){
        InputStream input = null;
        try {
            input = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        return  input;
    }

    private void writeFile(String path, String output){
        try {
            FileWriter fw = new FileWriter(path);
            fw.write(output);
            fw.close();
            LOGGER.info("R2RMapper: File " + path + "was updated");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
