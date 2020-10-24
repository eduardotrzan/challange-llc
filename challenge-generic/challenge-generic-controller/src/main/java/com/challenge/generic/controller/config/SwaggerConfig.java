package com.challenge.generic.controller.config;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BASIC_AUTH = "basic-auth";

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder().title("LLC API").build())
                .forCodeGeneration(true)
                .globalOperationParameters(this.globalOperationParameters());

    }

    private List<Parameter> globalOperationParameters() {
        return List.of(new ParameterBuilder().name("Authorization")
                .description("Bearer <token> ")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .build(), new ParameterBuilder().name("reqSessionId")
                .description("Request Session UUID")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue(UUID.randomUUID().toString())
                .required(true)
                .build());
    }

    private List<SecurityScheme> securitySchemes() {
        return List.of(new BasicAuth(BASIC_AUTH));
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityReference> securityReferences = List.of(
                SecurityReference.builder()
                        .reference(BASIC_AUTH)
                        .scopes(new AuthorizationScope[0])
                        .build()
        );

        return List.of(SecurityContext.builder()
                .securityReferences(securityReferences)
                .build());
    }
}

