package com.gjgs.gjgs.modules.member.dto.mypage;

import lombok.*;
import org.springframework.data.domain.Slice;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    private int totalReward;

    Slice<RewardDto> rewardDtoList;

    public static RewardResponse of(Slice<RewardDto> rewardDtoList, int totalReward){
        return RewardResponse.builder()
                .totalReward(totalReward)
                .rewardDtoList(rewardDtoList)
                .build();
    }
}
