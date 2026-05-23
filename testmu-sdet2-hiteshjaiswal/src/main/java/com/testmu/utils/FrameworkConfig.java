package com.testmu.utils;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)

@Config.Sources({
        "System:properties",
        "System:env",
        "classpath:project.properties"
})

public interface FrameworkConfig extends Config {

    @DefaultValue("dev")
    String environment();

    @Key("headless")
    String headless();

    @Key("${environment}.url")
    String url();

    @Key("${environment}.uri")
    String uri();

    @Key("${environment}.username")
    String username();

    @Key("${environment}.password")
    String password();
}
