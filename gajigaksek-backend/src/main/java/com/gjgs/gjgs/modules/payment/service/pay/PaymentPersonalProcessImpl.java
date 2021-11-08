package com.gjgs.gjgs.modules.payment.service.pay;

import com.gjgs.gjgs.modules.coupon.services.MemberCouponService;
import com.gjgs.gjgs.modules.lecture.entity.Schedule;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import com.gjgs.gjgs.modules.lecture.repositories.participant.ParticipantJdbcRepository;
import com.gjgs.gjgs.modules.lecture.repositories.participant.ParticipantQueryRepository;
import com.gjgs.gjgs.modules.lecture.repositories.schedule.ScheduleQueryRepository;
import com.gjgs.gjgs.modules.lecture.services.apply.timepolicy.CheckTimeType;
import com.gjgs.gjgs.modules.lecture.services.apply.timepolicy.TimePolicyFactory;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.payment.dto.OrderIdDto;
import com.gjgs.gjgs.modules.payment.dto.PayType;
import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.payment.exceptions.OrderErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.OrderException;
import com.gjgs.gjgs.modules.payment.repository.OrderQueryRepository;
import com.gjgs.gjgs.modules.payment.service.order.OrderService;
import com.gjgs.gjgs.modules.utils.converter.CheckTimeTypeConverter;
import com.gjgs.gjgs.modules.utils.jwt.CurrentMemberUtil;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.gjgs.gjgs.modules.payment.dto.PayType.PERSONAL;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentPersonalProcessImpl implements PaymentProcess {

    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ParticipantJdbcRepository participantJdbcRepository;
    private final TimePolicyFactory timePolicyFactory;
    private final OrderService orderService;
    private final MemberCouponService memberCouponService;
    private final CurrentMemberUtil currentMemberUtil;
    private final ParticipantQueryRepository participantQueryRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final CheckTimeTypeConverter checkTimeTypeConverter;

    @Override
    public PayType getType() {
        return PERSONAL;
    }

    @Override
    public OrderIdDto payProcess(Long scheduleId, PaymentRequest paymentRequest) {
        Schedule schedule = scheduleQueryRepository.findWithLectureParticipantsByLectureScheduleId(paymentRequest.getLectureId(), scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCodes.SCHEDULE_NOT_FOUND));
        Member member = memberCouponService.getMemberOrWithCoupon(paymentRequest);

        CheckTimeType checkTimeType = checkTimeTypeConverter.getTimeTypeFromPayType(getType());
        timePolicyFactory.getPolicy(checkTimeType).checkCloseTime(schedule.getStartLocalDateTime());
        schedule.addParticipants(List.of(member));
        participantJdbcRepository.insertParticipants(schedule);

        return OrderIdDto.ofComplete(orderService.paymentPersonal(member, schedule, paymentRequest));
    }

    @Override
    public void cancelProcess(Long orderId) throws IamportResponseException, IOException {
        Member cancelMember = currentMemberUtil.getCurrentMemberOrThrow();
        Schedule schedule = orderQueryRepository.findScheduleParticipantsById(orderId).orElseThrow(() -> new OrderException(OrderErrorCodes.ORDER_NOT_FOUND)).getSchedule();
        List<Long> exitMemberIdList = schedule.cancelAndReturnParticipantIdList(List.of(cancelMember));
        participantQueryRepository.deleteParticipants(exitMemberIdList);
       orderService.cancel(cancelMember, orderId);
    }
}
