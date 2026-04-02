package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;

@ReadingConverter
public class MongoReadConverter implements Converter<java.util.Date, Instant> {

    @Override
    public Instant convert(java.util.Date source) {
        return source.toInstant();
    }
}