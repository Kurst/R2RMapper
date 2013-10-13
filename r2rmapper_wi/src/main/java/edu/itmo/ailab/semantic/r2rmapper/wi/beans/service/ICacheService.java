package edu.itmo.ailab.semantic.r2rmapper.wi.beans.service;

/**
 * R2R Mapper. It is a free software.
 *
 *
 * Author: Ilya Semerhanov
 * Date: 13.10.13
 */
public interface ICacheService {


    void store(String key, String value);

    String fetchValue(String key);

    void clear();
}
