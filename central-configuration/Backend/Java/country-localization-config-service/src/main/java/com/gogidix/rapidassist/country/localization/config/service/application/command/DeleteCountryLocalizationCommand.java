package com.gogidix.rapidassist.country.localization.config.service.application.command;

/**
 * Command object for deleting a CountryLocalization.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a localization entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteCountryLocalizationCommand(
    String id,
    String deletedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && deletedBy != null && !deletedBy.isBlank()
            && reason != null && !reason.isBlank();
    }
}
