package com.gjgs.gjgs.modules.member.dto;

import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.reward.entity.Reward;
import com.gjgs.gjgs.modules.reward.enums.RewardSaveType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardDtoTest {

    @DisplayName("createRewardDto 생성")
    @Test
    void create_rewardDto() throws Exception{
        //given
        Member member = MemberDummy.createTestMember();
        Reward reward = Reward.createSaveReward(member, RewardSaveType.RECOMMEND);

        //when
        RewardDto rewardDto = RewardDto.of(reward);

        //then
        assertAll(
                () -> assertEquals(RewardSaveType.RECOMMEND.getRewardType(),rewardDto.getRewardType()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getText(),rewardDto.getText()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getAmount(),rewardDto.getAmount())
        );
    }
}
