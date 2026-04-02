package com.gogidix.rapidassist.config.service.application.mapper;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.mapstruct.Named;

/**
 * Helper mapper for Configuration.DataType enum conversions.
 */
public class ConfigurationDataTypeMapper {

    /**
     * Converts a ConfigurationDataType enum to its string name.
     *
     * @param dataType the data type enum
     * @return the string name of the data type
     */
    @Named("dataTypeToString")
    public static String dataTypeToString(Configuration.ConfigurationDataType dataType) {
        return dataType != null ? dataType.name() : null;
    }

    /**
     * Converts a string to ConfigurationDataType enum.
     *
     * @param dataType the string data type
     * @return the ConfigurationDataType enum
     */
    @Named("stringToDataType")
    public static Configuration.ConfigurationDataType stringToDataType(String dataType) {
        if (dataType == null) {
            return null;
        }
        try {
            return Configuration.ConfigurationDataType.valueOf(dataType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Configuration.ConfigurationDataType.STRING; // Default fallback
        }
    }
}
