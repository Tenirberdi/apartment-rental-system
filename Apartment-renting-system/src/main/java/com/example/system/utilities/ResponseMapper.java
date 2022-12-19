package com.example.system.utilities;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponseMapper {
    public Map<String, ?> mapResponse(Object data, Object meta){
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("meta",meta);
        return response;
    }

    public Map<String, ?> mapResponse(Object data){
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    public Map<String, ?> mapResponse(Object data, List<Object> links){
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("links", links);
        return response;
    }

    public Map<String, ?> mapResponse(Object data, Object meta, Object links){
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("meta", meta);
        response.put("links", links);
        return response;
    }
}
