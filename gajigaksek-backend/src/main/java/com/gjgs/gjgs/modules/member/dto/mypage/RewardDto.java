package com.gjgs.gjgs.modules.member.dto.mypage;

import com.gjgs.gjgs.modules.reward.entity.Reward;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RewardDto {

    private int amount;

    private String text;

    private RewardType rewardType;

    private LocalDateTime createdDate;

    public static RewardDto of(Reward reward){
        return RewardDto.builder()
                .amount(reward.getAmount())
                .text(reward.getText())
                .rewardType(reward.getRewardType())
                .createdDate(reward.getCreatedDate())
                .build();
    }

    @QueryProjection
    public RewardDto(int amount, String text, RewardType rewardType, LocalDateTime createdDate) {
        this.amount = amount;
        this.text = text;
        this.rewardType = rewardType;
        this.createdDate = createdDate;
    }
}
