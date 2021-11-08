package com.gjgs.gjgs.modules.member.dto;

import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.dummy.RewardDummy;
import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.member.dto.mypage.RewardResponse;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.reward.entity.Reward;
import com.gjgs.gjgs.modules.reward.enums.RewardSaveType;
import com.gjgs.gjgs.modules.utils.querydsl.RepositorySliceHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardResponseTest {

    @DisplayName("RewardDtoResponse 만들기")
    @Test
    void create_rewardDtoResponse() throws Exception{
        //given
        Member member = MemberDummy.createTestMember();
        Reward reward = Reward.createSaveReward(member, RewardSaveType.RECOMMEND);
        Slice<RewardDto> pagingDto = createSliceRewardDto(member);

        //when
        RewardResponse rewardResponse = RewardResponse.of(pagingDto, 3000);

        //then
        assertAll(
                () -> assertEquals(3000, rewardResponse.getTotalReward()),
                () -> assertEquals(20, rewardResponse.getRewardDtoList().getSize()),
                () -> assertEquals(20, rewardResponse.getRewardDtoList().getNumberOfElements()),
                () -> assertEquals(20, rewardResponse.getRewardDtoList().getSize()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getRewardType(), rewardResponse.getRewardDtoList().getContent().get(0).getRewardType()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getText(), rewardResponse.getRewardDtoList().getContent().get(0).getText()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getAmount(), rewardResponse.getRewardDtoList().getContent().get(0).getAmount())
        );
    }

//    private Page<RewardDto> createRewardDtoResponse(Member member) {
//        return new PageImpl<>(RewardDummy.createRewardDtoList(member),
//                PageRequest.of(0, 20),
//                35);
//    }

    private Slice<RewardDto> createSliceRewardDto(Member member) {
        return RepositorySliceHelper.toSlice(RewardDummy.createRewardDtoList(member), PageRequest.of(0, 20));

    }
}
