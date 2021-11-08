package com.gjgs.gjgs.modules.reward.repository.impl;

import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.reward.entity.Reward;
import com.gjgs.gjgs.modules.reward.enums.RewardSaveType;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import com.gjgs.gjgs.modules.reward.repository.interfaces.RewardQueryRepository;
import com.gjgs.gjgs.modules.reward.repository.interfaces.RewardRepository;
import com.gjgs.gjgs.testutils.repository.SetUpMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        RewardQueryRepositoryImpl.class
})
@CustomDataJpaTest
public class RewardQueryRepositoryTest extends SetUpMemberRepository {

    @Autowired RewardQueryRepository rewardQueryRepository;
    @Autowired RewardRepository rewardRepository;

    @AfterEach
    void tearDown() throws Exception {
        rewardRepository.deleteAll();
    }

    @DisplayName("멤버 id와 리워드 타입으로 페이징 처리해서 리워드 상세 가져오기")
    @Test
    void find_by_memberId_and_rewardType() throws Exception{
        //given
        Member member = anotherMembers.get(0);
        saveReward(member);
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"createdDate"));

        flushAndClear();        
        
        //when
        Slice<RewardDto> dtos = rewardQueryRepository.
                findByMemberIdAndRewardTypeSortedByCreatedDateDesc(member.getId(), RewardType.SAVE, pageRequest);
        //then
        assertAll(
                () -> assertEquals(20,dtos.getNumberOfElements()),
                () -> assertEquals(20,dtos.getSize()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getRewardType(),dtos.getContent().get(0).getRewardType()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getAmount(),dtos.getContent().get(0).getAmount()),
                () -> assertEquals(RewardSaveType.RECOMMEND.getText(),dtos.getContent().get(0).getText())

        );
    }

    private void saveReward(Member member){
        List<Reward> rewards = new ArrayList<>();
        for (int i=0;i<35;i++){
            rewards.add(Reward.createSaveReward(member, RewardSaveType.RECOMMEND));
        }
        rewardRepository.saveAll(rewards);
    }
}
