package com.gogidix.rapidassist.user.profile.service.application.usecase;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.in.UpsertUserProfileCommand;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Deprecated
public class UpsertUserProfileUseCase implements UpsertUserProfileCommand {

    private final UserProfileStore store;

    public UpsertUserProfileUseCase(UserProfileStore store) {
        this.store = store;
    }

    @Override
    public UserProfile upsert(String tenantId, String subject, String displayName, String email) {
        UserProfile profile = UserProfile.create(
            subject, tenantId, email,
            displayName, null, "system"
        );
        return store.upsert(profile);
    }
}
