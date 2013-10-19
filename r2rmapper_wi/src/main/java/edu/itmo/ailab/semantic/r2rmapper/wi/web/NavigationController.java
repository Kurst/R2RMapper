package edu.itmo.ailab.semantic.r2rmapper.wi.web;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 13.10.13
 */
@ManagedBean(name = "navigationController", eager = true)
@RequestScoped
public class NavigationController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value="#{param.pageId}")
    private String pageId;

    public String showBarPage(){

        if(pageId == null){
            return "index";
        }
        if(pageId.equals("1")){
            return "settings";
        }else{
            return "index";
        }
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageId() {
        return pageId;
    }
}