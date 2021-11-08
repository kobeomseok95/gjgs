package com.gjgs.gjgs.modules.notification.repository.impl;

import com.gjgs.gjgs.modules.member.dto.mypage.NotificationResponse;
import com.gjgs.gjgs.modules.member.dto.mypage.QNotificationResponse;
import com.gjgs.gjgs.modules.notification.entity.Notification;
import com.gjgs.gjgs.modules.notification.repository.interfaces.NotificationQueryRepository;
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
import java.util.Optional;

import static com.gjgs.gjgs.modules.member.entity.QMember.member;
import static com.gjgs.gjgs.modules.notification.entity.QNotification.notification;
import static org.springframework.util.ObjectUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Optional<Notification> findNotificationByUuidAndUsername(String uuid, String username) {
        return Optional.ofNullable(
                query
                    .select(notification)
                    .from(notification)
                    .join(notification.member,member)
                    .where(notification.uuid.eq(uuid),notification.member.username.eq(username))
                    .fetchOne());
    }

    @Override
    public Slice<NotificationResponse> findNotificationByUsername(String username, Pageable pageable) {

        List<OrderSpecifier> ORDERS = getAllOrderSpecifiers(pageable);

        List<NotificationResponse> results = query
                .select(new QNotificationResponse(
                        notification.title,
                        notification.message,
                        notification.checked,
                        notification.notificationType,
                        notification.uuid,
                        notification.TeamId,
                        notification.createdDate
                ))
                .from(notification)
                .join(notification.member, member)
                .where(notification.member.username.eq(username))
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
                                .getSortedColumn(direction, notification, "createdDate");
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
