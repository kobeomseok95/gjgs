package com.gjgs.gjgs.modules.payment.service.pay;

import com.gjgs.gjgs.modules.coupon.services.MemberCouponService;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.payment.dto.OrderIdDto;
import com.gjgs.gjgs.modules.payment.dto.PayType;
import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.payment.service.order.OrderService;
import com.gjgs.gjgs.modules.utils.jwt.CurrentMemberUtil;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.gjgs.gjgs.modules.payment.dto.PayType.TEAM;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTeamMemberProcessImpl implements PaymentProcess{

    private final OrderService orderService;
    private final MemberCouponService memberCouponService;
    private final CurrentMemberUtil currentMemberUtil;

    @Override
    public PayType getType() {
        return TEAM;
    }

    @Override
    public OrderIdDto payProcess(Long scheduleId, PaymentRequest paymentRequest) {
        Member member = memberCouponService.getMemberOrWithCoupon(paymentRequest);
        return OrderIdDto.ofComplete(orderService.paymentTeamMember(member, scheduleId, paymentRequest));
    }

    @Override
    public void cancelProcess(Long orderId) throws IamportResponseException, IOException {
        Member refundMember = currentMemberUtil.getCurrentMemberOrThrow();
        orderService.cancel(refundMember, orderId);
    }
}
