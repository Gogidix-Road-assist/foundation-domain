package com.gogidix.rapidassist.user.profile.service.domain.port.in;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

public interface UpsertUserProfileCommand {

    UserProfile upsert(String tenantId, String subject, String displayName, String email);
}
