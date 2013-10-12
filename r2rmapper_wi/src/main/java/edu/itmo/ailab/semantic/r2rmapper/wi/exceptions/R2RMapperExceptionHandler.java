package edu.itmo.ailab.semantic.r2rmapper.wi.exceptions;

import org.apache.log4j.Logger;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.*;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;

import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 08.10.13
 */
public class R2RMapperExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(R2RMapperExceptionHandler.class);

    private ExceptionHandler wrapped;


    public R2RMapperExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

        while (iterator.hasNext()) {
            ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext)event.getSource();

            Throwable throwable = context.getException();

            FacesContext fc = FacesContext.getCurrentInstance();

            try {
                Flash flash = fc.getExternalContext().getFlash();

                flash.put("errorDetails", getStackTrace(throwable));
                LOGGER.info("R2RMapper Exception occured: " + getStackTrace(throwable));
                NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(fc, null, "error?faces-redirect=true");
                fc.renderResponse();
            } finally {
                iterator.remove();
            }
        }

        // Let the parent handle the rest
        getWrapped().handle();
    }

}
