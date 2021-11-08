package com.gjgs.gjgs.modules.member.service.mypage.interfaces;


import com.gjgs.gjgs.modules.member.dto.mypage.*;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MyPageService {
    MyPageResponse getMyPage();

    List<MyBulletinDto> getMyBulletins();

    List<MyQuestionDto> getMyQuestions();

    void editMyQuestion(Long questionId, QuestionMainTextModifyRequest mainText);

    void deleteMyQuestion(Long questionId);

    InfoResponse getInfo(Long memberId);

    void switchToDirector();

    RewardResponse getMyRewardList(RewardType rewardType, Pageable pageable);

    Slice<MyLectureResponse> getMyLectures(Pageable pageable);

    TeamMemberPaymentStatusResponse getTeamOrderStatus(Long scheduleId, Long teamId);

    Page<MyReviewResponse> getMyReviews(Pageable pageable);

    CompleteLecturePaymentResponse getCompleteLecturePayment(Long orderId);

    MyAvailableCouponResponse getMyAvailableCoupons();

    Slice<NotificationResponse> getMyNotifications(Pageable pageable);

    AlarmStatusResponse getMyAlarmStatus();
}
