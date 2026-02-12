package com.gogidix.rapidassist.api.keys.service.domain.port.in;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;

public interface IssueApiKeyCommand {

    ApiKey issue(String tenantId, String subject);
}
