package com.gjgs.gjgs.modules.reward.repository.impl;

import com.gjgs.gjgs.modules.member.dto.mypage.QRewardDto;
import com.gjgs.gjgs.modules.member.dto.mypage.RewardDto;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import com.gjgs.gjgs.modules.reward.repository.interfaces.RewardQueryRepository;
import com.gjgs.gjgs.modules.utils.querydsl.QueryDslUtil;
import com.gjgs.gjgs.modules.utils.querydsl.RepositorySliceHelper;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.gjgs.gjgs.modules.reward.entity.QReward.reward;
import static org.springframework.util.ObjectUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class RewardQueryRepositoryImpl implements RewardQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Slice<RewardDto> findByMemberIdAndRewardTypeSortedByCreatedDateDesc(Long memberId, RewardType rewardType, Pageable pageable) {

        List<OrderSpecifier> ORDERS = getAllOrderSpecifiers(pageable);

        List<RewardDto> results = query
                .select(new QRewardDto(
                        reward.amount,
                        reward.text,
                        reward.rewardType,
                        reward.createdDate
                ))
                .from(reward)
                .where(reward.member.id.eq(memberId), reward.rewardType.eq(rewardType))
                .orderBy(ORDERS.stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return RepositorySliceHelper.toSlice(results, pageable);

    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "createdDate":
                        OrderSpecifier<?> createdDate = QueryDslUtil
                                .getSortedColumn(direction, reward, "createdDate");
                        ORDERS.add(createdDate);
                        break;
                    default:
                        break;
                }
            }
        }

        return ORDERS;
    }
}
