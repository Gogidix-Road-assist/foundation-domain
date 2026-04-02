package com.gogidix.rapidassist.user.profile.service.application.usecase;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.in.GetUserProfileQuery;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class GetUserProfileUseCase implements GetUserProfileQuery {

    private final UserProfileStore store;

    public GetUserProfileUseCase(UserProfileStore store) {
        this.store = store;
    }

    @Override
    public UserProfile get(String tenantId, String subject) {
        return store.find(tenantId, subject).orElse(null);
    }
}
