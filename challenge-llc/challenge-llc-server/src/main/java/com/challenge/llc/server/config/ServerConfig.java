package com.challenge.llc.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.challenge.generic.server.filter.config.ServerFilterConfig;
import com.challenge.generic.server.filter.config.SpringSecurityConfig;
import com.challenge.llc.controller.config.ControllerConfig;

@Import({
                ControllerConfig.class,

                ServerFilterConfig.class,
                SpringSecurityConfig.class
})
@EnableConfigurationProperties({ ServerPropConfig.class })
public class ServerConfig {

}
