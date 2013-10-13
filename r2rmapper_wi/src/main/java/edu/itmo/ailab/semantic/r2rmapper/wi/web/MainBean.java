package edu.itmo.ailab.semantic.r2rmapper.wi.web;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 06.10.13
 */
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.rdf.impl.WebMapperImpl;
import edu.itmo.ailab.semantic.r2rmapper.wi.beans.service.impl.RedisCacheImpl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.*;
import java.sql.SQLException;

@ManagedBean
@SessionScoped
public class MainBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String test;

    @Inject
    private RedisCacheImpl redisCache;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public void  saveTest(){
           test = "BlaBla";

    }

    public void  readTest() throws IOException, R2RMapperException, ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
        String settingsPath = System.getProperty("jboss.server.data.dir") + "/settings.yaml";
        String configPath = System.getProperty("jboss.server.data.dir") + "/config.yaml";
        //InputStream input = new FileInputStream(new File(path));
        //String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        //test = inputStreamString;
        PropertyLoader loader;
        WebMapperImpl wm;
        String outputFileNamePhase1 = "integrated_ontology_phase_1.owl";
        String outputFileNamePhase2 = "integrated_ontology_phase_2.owl";
        String ontologyFormat = "TURTLE";
        String host = redisCache.fetchValueFromGroup("settings","redis.hostname");
        Integer port = Integer.parseInt(redisCache.fetchValueFromGroup("settings","redis.port"));
        MatchingDBHandler.getInstance();
        MatchingDBHandler.connect(host,port);

        loader = new PropertyLoader(configPath);
        wm = new WebMapperImpl(loader.properties);

        MatchingDBHandler.flushDB();
        wm.createStructureMap();
        wm.printModelToFile(ontologyFormat,outputFileNamePhase1,System.getProperty("jboss.server.data.dir")+"/");



    }
}