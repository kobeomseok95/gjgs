package com.gjgs.gjgs.modules.notice.service.impl;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.enums.Authority;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.notice.aop.CheckAuthority;
import com.gjgs.gjgs.modules.notice.dto.NoticeForm;
import com.gjgs.gjgs.modules.notice.dto.NoticeResponse;
import com.gjgs.gjgs.modules.notice.entity.Notice;
import com.gjgs.gjgs.modules.notice.enums.NoticeType;
import com.gjgs.gjgs.modules.notice.exception.NoticeErrorCodes;
import com.gjgs.gjgs.modules.notice.exception.NoticeException;
import com.gjgs.gjgs.modules.notice.repository.interfaces.NoticeQueryRepository;
import com.gjgs.gjgs.modules.notice.repository.interfaces.NoticeRepository;
import com.gjgs.gjgs.modules.notice.service.interfaces.NoticeService;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeQueryRepository noticeQueryRepository;
    private final NoticeRepository noticeRepository;
    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;

    @Override
    @CheckAuthority
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getNotice(String noticeType, Pageable pageable) {
        return noticeQueryRepository.findPagingNotice(pageable,NoticeType.valueOf(noticeType));
    }

    @Override
    public void createNotice(NoticeForm noticeForm) {
        noticeRepository.save(Notice.of(noticeForm));
    }

    @Override
    public void updateNotice(NoticeForm noticeForm, Long noticeId) {
        noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCodes.NOTICE_NOT_FOUND))
                .changeNotice(noticeForm);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        long count = noticeQueryRepository.deleteById(noticeId);
        if (count == 0){
            throw new NoticeException(NoticeErrorCodes.NOTICE_NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponse getOneNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCodes.NOTICE_NOT_FOUND));

        if (notice.getNoticeType().equals(NoticeType.DIRECTOR) &&
                getCurrentUserOrThrow().getAuthority().equals(Authority.ROLE_USER)){
                throw new MemberException(MemberErrorCodes.FORBIDDEN);
        }
        return notice.toNoticeResponse();
    }

    private Member getCurrentUserOrThrow(){
        return memberRepository.findByUsername(getUsernameOrThrow())
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }

    private String getUsernameOrThrow() {
        return securityUtil.getCurrentUsername()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }
}

