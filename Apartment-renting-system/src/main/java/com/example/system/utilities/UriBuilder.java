package com.example.system.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.net.URI;


@Component
public class UriBuilder {
    @Value("${server.servlet.context-path}")
    private String context_path;
    private static String CONTEXT_PATH_STATIC;

    @Value("${server.servlet.context-path}")
    public void setContext_path(){
        CONTEXT_PATH_STATIC = context_path;
    }


    public static String buildUrl(String url){
        return ServletUriComponentsBuilder.fromCurrentRequestUri().replacePath(null).build().toUriString() + CONTEXT_PATH_STATIC  + url;
    }

}
