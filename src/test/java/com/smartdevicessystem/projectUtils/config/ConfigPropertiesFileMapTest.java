package com.smartdevicessystem.projectUtils.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPropertiesFileMapTest {

    static ConfigPropertiesFileMap config = new ConfigPropertiesFileMap("/home/yana/Documents/config/smartDeviceProj/mySqlConfig.properties");
    @Test
    void get() {
        System.out.println(config.get("db.url"));
        System.out.println(config.get("db.password"));
        System.out.println(config.get("db.username"));
    }
}