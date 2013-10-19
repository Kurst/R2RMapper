package edu.itmo.ailab.semantic.r2rmapper.wi.beans.service;

import java.io.IOException;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 16.10.13
 */
public interface IR2RConfigurationHandler {

    String settingPath = null;
    String configPath = null;

    public void defineSettingsFile(String path);

    public void defineConfigFile(String path);

    public Map getAllSettings();

    public Map getRedisSettings() throws IOException;

    public Map getAllConfigs();

    public void setRedisSettings(String hostname, Integer port);



}
