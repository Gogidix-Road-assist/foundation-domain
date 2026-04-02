package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.Instant;
import java.util.Date;

@WritingConverter
public class MongoWriteConverter implements Converter<Instant, java.util.Date> {

    @Override
    public java.util.Date convert(Instant source) {
        return Date.from(source);
    }
}