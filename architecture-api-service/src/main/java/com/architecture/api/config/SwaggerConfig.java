package com.architecture.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.service.ApiInfo.DEFAULT;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static ApiInfo API_INFO = new ApiInfo("Architecture-API", "Api exposing service to ingest site data", "1.0", DEFAULT.getTermsOfServiceUrl()
            , DEFAULT.getContact(), DEFAULT.getLicense(), DEFAULT.getLicenseUrl(), DEFAULT.getVendorExtensions());

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(API_INFO)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.architecture.api.controller"))
                .build();
    }
}