package com.gom.weatherqueue.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gom.weatherqueue.constraint.Constraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * Created by goukumar on 2/24/2018.
 */
@Service
public class ConfigService {

    public Constraint constraint = null;

    private ResourceLoader resourceLoader;

    @Autowired
    public ConfigService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:config.json");
        ObjectMapper jsonMapper = new ObjectMapper();
        File jsonFile = resource.getFile();
        constraint = jsonMapper.readValue(jsonFile, new TypeReference<Constraint>() {});
    }
}
