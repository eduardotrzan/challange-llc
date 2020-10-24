package com.challenge.llc.controller.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.challenge.generic.controller.config.RestControllerConfig;
import com.challenge.generic.controller.config.SwaggerConfig;
import com.challenge.llc.service.config.ServiceConfig;

@Configuration
@ComponentScan(basePackages = "com.challenge.llc.controller")
@Import({
                ServiceConfig.class,

                RestControllerConfig.class
})
public class ControllerConfig {

}
