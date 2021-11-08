package com.gjgs.gjgs.modules.notification.dto;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class MemberFcmIncludeNicknameDto {

    private Long memberId;

    private String fcmToken;

    private String nickname;

    @QueryProjection
    public MemberFcmIncludeNicknameDto(Long memberId, String fcmToken, String nickname) {
        this.memberId = memberId;
        this.fcmToken = fcmToken;
        this.nickname = nickname;
    }

    public static MemberFcmIncludeNicknameDto of (Long memberId, String fcmToken, String nickname){
        return MemberFcmIncludeNicknameDto.builder()
                .memberId(memberId)
                .fcmToken(fcmToken)
                .nickname(nickname)
                .build();
    }

    public Member toEntity(){
        return Member.of(memberId,fcmToken,nickname);
    }
}
