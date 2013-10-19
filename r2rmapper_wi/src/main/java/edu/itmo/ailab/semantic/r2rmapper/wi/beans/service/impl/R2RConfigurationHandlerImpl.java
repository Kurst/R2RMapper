package edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl;

import edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize.R2RRedisSettings;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize.R2RSettings;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.IR2RConfigurationHandler;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;


import java.io.*;
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

    @Override
    public void defineSettingsFile(String path){
        this.settingsPath = path;
    }

    @Override
    public void defineConfigFile(String path){
        this.configPath = path;
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
    public Map getRedisSettings() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputSettings = readFile(settingsPath);

        LOGGER.info("[R2RMapper] Loading settings from: " + settingsPath);
        Map allSettings = (Map) yaml.load(inputSettings);
        Map redisSettings = (Map) allSettings.get("redisServer");
        inputSettings.close();
        return redisSettings;
    }

    @Override
    public Map getAllConfigs(){
        return null;
    }

    @Override
    public void setRedisSettings(String hostname, Integer port){
        R2RRedisSettings settings = new R2RRedisSettings();
        settings.setPort(port);
        settings.setHostname(hostname);
        R2RSettings appSet = new R2RSettings();
        appSet.setRedisServer(settings);
        String output = new Yaml().dumpAsMap(appSet);
        writeFile(settingsPath, output);

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
