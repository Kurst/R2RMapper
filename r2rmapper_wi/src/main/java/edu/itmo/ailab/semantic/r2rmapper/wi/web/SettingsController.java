package edu.itmo.ailab.semantic.r2rmapper.wi.web;

import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 13.10.13
 */
@ManagedBean(name = "settingsController", eager = true)
@RequestScoped
public class SettingsController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RedisCacheImpl redisCache;

    @ManagedProperty(value="#{param.redis_hostname}")
    private String redis_hostname;

    @ManagedProperty(value="#{param.redis_port}")
    private String redis_port;

    @PostConstruct
    public void readSettingsFromCache(){
        redis_hostname = redisCache.fetchValueFromGroup("settings","redis.hostname");
        redis_port = redisCache.fetchValueFromGroup("settings","redis.port");
    }

    public String getRedis_hostname() {

        return redis_hostname;
    }

    public void setRedis_hostname(String redis_hostname) {
        this.redis_hostname = redis_hostname;
    }

    public String getRedis_port() {

        return redis_port;
    }

    public void setRedis_port(String redis_port) {
        this.redis_port = redis_port;
    }


}