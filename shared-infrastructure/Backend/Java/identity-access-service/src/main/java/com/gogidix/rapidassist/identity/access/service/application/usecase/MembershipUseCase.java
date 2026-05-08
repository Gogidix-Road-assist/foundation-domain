package com.gogidix.rapidassist.identity.access.service.application.usecase;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.AssignMembershipCommand;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.MembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipUseCase implements AssignMembershipCommand {

    private final MembershipRepository membershipRepository;

    public MembershipUseCase(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Membership assign(Membership membership) {
        return membershipRepository.save(membership);
    }
}
