package com.kpushpad.springboot.kvstore.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.config")
public class AppConfigProperties {
    private String name;

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}

