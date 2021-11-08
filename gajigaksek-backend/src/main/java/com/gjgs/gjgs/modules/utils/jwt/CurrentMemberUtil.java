package com.gjgs.gjgs.modules.utils.jwt;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class CurrentMemberUtil {

    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    public Member getCurrentMemberOrThrow() {
        String currentUsername = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
        return memberRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }
}
