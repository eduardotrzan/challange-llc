package com.challenge.generic.controller.mapper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class JsonMapper extends ObjectMapper {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public JsonMapper() {
        super();

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
        setTimeZone(TimeZone.getTimeZone("America/Montreal"));

        registerModule(new JavaTimeModule());
        registerModule(new JaxbAnnotationModule());

        findAndRegisterModules();
        enable(SerializationFeature.INDENT_OUTPUT);
    }
}
