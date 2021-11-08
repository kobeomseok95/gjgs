package com.gjgs.gjgs.modules.reward.repository.interfaces;

import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RewardQueryRepository {

    //Slice<RewardDto> findByMemberIdAndRewardTypeSortedByCreatedDateDesc(Long memberId, String rewardType, Pageable pageable);
    Slice<RewardDto> findByMemberIdAndRewardTypeSortedByCreatedDateDesc(Long memberId, RewardType rewardType, Pageable pageable);
}
