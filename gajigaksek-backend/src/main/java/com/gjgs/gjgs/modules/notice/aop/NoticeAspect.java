package com.gjgs.gjgs.modules.notice.aop;

import com.gjgs.gjgs.modules.member.enums.Authority;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.notice.enums.NoticeType;
import com.gjgs.gjgs.modules.notice.exception.NoticeErrorCodes;
import com.gjgs.gjgs.modules.notice.exception.NoticeException;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class NoticeAspect {

    private final SecurityUtil securityUtil;
    private static final String ALL = "ALL";
    private static final String DIRECTOR = "DIRECTOR";

    @Before(value = "@annotation(CheckAuthority)")
    public void checkAuthority(JoinPoint joinPoint) {
        String noticeType = (String) joinPoint.getArgs()[0];
        if (!noticeType.equals(ALL) && !noticeType.equals(DIRECTOR)){
            throw new NoticeException(NoticeErrorCodes.NOTICE_TYPE_NOT_FOUND);
        }
        if(NoticeType.valueOf(noticeType).equals(NoticeType.DIRECTOR) &&
                    getAuthorityOrThrow().equals(Authority.ROLE_USER)){
            throw new MemberException(MemberErrorCodes.FORBIDDEN);
        }
    }

    private Authority getAuthorityOrThrow(){
        return securityUtil.getAuthority()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
    }
}
