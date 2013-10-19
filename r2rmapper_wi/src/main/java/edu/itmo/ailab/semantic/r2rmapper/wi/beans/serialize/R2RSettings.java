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
public class R2RSettings implements Serializable {

    private R2RRedisSettings RedisServer;

    public R2RRedisSettings getRedisServer() {
        return RedisServer;

    }

    public void setRedisServer(R2RRedisSettings RedisServer) {
        this.RedisServer = RedisServer;
    }
}
