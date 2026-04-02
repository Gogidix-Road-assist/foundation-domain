package com.gogidix.rapidassist.shared.mapper.library;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

/**
 * Wrapper around ModelMapper for consistent object mapping across services.
 */
public class Mapper {

    private final ModelMapper modelMapper;

    public Mapper() {
        this.modelMapper = new ModelMapper();
        // Configure ModelMapper with strict matching
        Configuration config = modelMapper.getConfiguration();

        this.modelMapper.getConfiguration().setFieldMatchingEnabled(true);
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }

    public <S, D> void map(S source, D destination) {
        if (source != null && destination != null) {
            modelMapper.map(source, destination);
        }
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
