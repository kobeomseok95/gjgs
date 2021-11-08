package com.gjgs.gjgs.modules.dummy;

import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.reward.entity.Reward;
import com.gjgs.gjgs.modules.reward.enums.RewardSaveType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RewardDummy {

    public static List<RewardDto> createRewardDtoList(Member member) {
        List<RewardDto> resList = new ArrayList<>();
        Reward reward = Reward.createSaveReward(member, RewardSaveType.RECOMMEND);
        for (int i = 0; i < 35; i++) {
            resList.add(RewardDto.builder()
                    .amount(reward.getAmount())
                    .text(reward.getText())
                    .rewardType(reward.getRewardType())
                    .createdDate(LocalDateTime.now())
                    .build());
        }
        return resList;
    }


}
