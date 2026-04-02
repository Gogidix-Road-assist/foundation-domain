package com.gogidix.rapidassist.user.profile.service.domain.port.in;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

public interface GetUserProfileQuery {

    UserProfile get(String tenantId, String subject);
}
