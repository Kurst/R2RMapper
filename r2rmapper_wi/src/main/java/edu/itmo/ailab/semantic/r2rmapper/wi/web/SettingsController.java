package edu.itmo.ailab.semantic.r2rmapper.wi.web;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.R2RConfigurationHandlerImpl;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;
import org.apache.log4j.Logger;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static final Logger LOGGER=Logger.getLogger(SettingsController.class);

    @Inject
    private RedisCacheImpl redisCache;

    @Inject
    private R2RConfigurationHandlerImpl r2rConfigHandler;

    @ManagedProperty(value="#{param.redis_hostname}")
    private String redis_hostname;

    @ManagedProperty(value="#{param.redis_port}")
    private String redis_port;

    @ManagedProperty(value="#{param.structureOntologyName}")
    private String structureOntologyName;

    @ManagedProperty(value="#{param.dataOntologyName}")
    private String dataOntologyName;

    @ManagedProperty(value="#{param.ontologyFormat}")
    private String ontologyFormat;

    @ManagedProperty(value="#{param.outputFolder}")
    private String outputFolder;


    public ArrayList<String> allOntologyFormats(){
            ArrayList<String> formats = new ArrayList<>();
            formats.add("TURTLE");
            formats.add("RDF/XML");
            return formats;
    }

    @PostConstruct
    public void readSettingsFromCache(){
        this.redis_hostname = redisCache.fetchValueFromGroup("settings","redis.hostname");
        this.redis_port = redisCache.fetchValueFromGroup("settings","redis.port");
        this.structureOntologyName = redisCache.fetchValueFromGroup("settings","ontology.structureOntologyName");
        this.dataOntologyName = redisCache.fetchValueFromGroup("settings","ontology.dataOntologyName");
        this.ontologyFormat = redisCache.fetchValueFromGroup("settings","ontology.ontologyFormat");
        this.outputFolder = redisCache.fetchValueFromGroup("settings","ontology.outputFolder");
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
            r2rConfigHandler.setAllSettings();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Redis Settings were updated","" ));
            navigationHandler.handleNavigation(fc, null, "settings?faces-redirect=true");
            fc.renderResponse();
        }catch (Exception e){
            throw new R2RMapperException(getStackTrace(e));
        }
    }


    public String getStructureOntologyName() {
        return structureOntologyName;
    }

    public void setStructureOntologyName(String structureOntologyName) {
        this.structureOntologyName = structureOntologyName;
    }

    public String getDataOntologyName() {
        return dataOntologyName;
    }

    public void setDataOntologyName(String dataOntologyName) {
        this.dataOntologyName = dataOntologyName;
    }

    public String getOntologyFormat() {
        return ontologyFormat;
    }

    public void setOntologyFormat(String ontologyFormat) {
        this.ontologyFormat = ontologyFormat;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void updateOntologySettings(ActionEvent actionEvent)
            throws R2RMapperException {
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
        try{
            redisCache.storeGroup("settings","ontology.structureOntologyName",this.structureOntologyName);
            redisCache.storeGroup("settings","ontology.dataOntologyName",this.dataOntologyName);
            redisCache.storeGroup("settings","ontology.ontologyFormat",this.ontologyFormat);
            redisCache.storeGroup("settings","ontology.outputFolder",this.outputFolder);

            r2rConfigHandler.setAllSettings();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ontology Settings were updated","" ));
            navigationHandler.handleNavigation(fc, null, "settings?faces-redirect=true");
            fc.renderResponse();
        }catch (Exception e){
            throw new R2RMapperException(getStackTrace(e));
        }
    }
}