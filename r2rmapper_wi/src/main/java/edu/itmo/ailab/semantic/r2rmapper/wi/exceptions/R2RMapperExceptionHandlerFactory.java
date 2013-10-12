package edu.itmo.ailab.semantic.r2rmapper.wi.exceptions;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 08.10.13
 */
public class R2RMapperExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    public R2RMapperExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = new R2RMapperExceptionHandler(parent.getExceptionHandler());
        return result;
    }
}
