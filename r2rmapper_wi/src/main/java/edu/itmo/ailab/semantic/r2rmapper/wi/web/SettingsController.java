package edu.itmo.ailab.semantic.r2rmapper.wi.web;

import com.hp.hpl.jena.tdb.base.objectfile.StringFile;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.R2RConfigurationHandlerImpl;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

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

    @Inject
    private R2RConfigurationHandlerImpl r2rConfigHandler;

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

    public void updateRedisSettings(ActionEvent actionEvent)
            throws R2RMapperException {
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
        try{
            redisCache.storeGroup("settings","redis.hostname",this.redis_hostname);
            redisCache.storeGroup("settings","redis.port",this.redis_port);
            r2rConfigHandler.setRedisSettings(this.redis_hostname, Integer.parseInt(this.redis_port));
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Redis Settings were updated","" ));
            navigationHandler.handleNavigation(fc, null, "settings?faces-redirect=true");
            fc.renderResponse();
        }catch (Exception e){
            throw new R2RMapperException(getStackTrace(e));
        }



    }


}