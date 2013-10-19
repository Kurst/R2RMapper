package edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize;

import java.io.Serializable;
import java.util.Map;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 19.10.13
 */
public class R2RRedisSettings implements Serializable {


    private String hostname;
    private Integer port;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


}
