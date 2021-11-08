package com.gjgs.gjgs.modules.member.controller;


import com.gjgs.gjgs.modules.member.dto.mypage.*;
import com.gjgs.gjgs.modules.member.service.mypage.interfaces.MyPageService;
import com.gjgs.gjgs.modules.reward.enums.RewardType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 마이페이지
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping
    public ResponseEntity<MyPageResponse> myPage() {
        return ResponseEntity.ok(myPageService.getMyPage());
    }


    /**
     * 나의 게시글
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/bulletins")
    public ResponseEntity<MyBulletinResponse> myBulletins() {
        return ResponseEntity.ok(MyBulletinResponse.of(myPageService.getMyBulletins()));
    }


    /**
     *  나의 문의
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/question")
    public ResponseEntity<MyQuestionResponse> myQuestion() {
        return ResponseEntity.ok(MyQuestionResponse.of(myPageService.getMyQuestions()));
    }

    /**
     * 나의 문의 수정
     * @param questionId 문의글 번호
     * @param mainText 문의 내용
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @PutMapping("/question/{questionId}")
    public ResponseEntity<Void> editMyQuestion(@PathVariable("questionId") Long questionId,
                                               @RequestBody @Valid QuestionMainTextModifyRequest mainText) {
        myPageService.editMyQuestion(questionId, mainText);
        return ResponseEntity.ok().build();
    }


    /**
     *  나의 문의 삭제
     * @param questionId 문의 번호
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteMyQuestion(@PathVariable("questionId") Long questionId) {
        myPageService.deleteMyQuestion(questionId);
        return ResponseEntity.ok().build();
    }


    /**
     * 타인의 프로필 정보 확인
     * @param memberId 타인의 회원 번호
     * @return
     */
    @GetMapping("/info/{memberId}")
    public ResponseEntity<InfoResponse> info(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(myPageService.getInfo(memberId));
    }

    /**
     *  나의 알림
     * @param pageable
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/notifications")
    public ResponseEntity<Slice<NotificationResponse>> getMyNotifications(
                                        @PageableDefault(size=20,sort = "createdDate",direction = Sort.Direction.DESC)
                                                Pageable pageable){
        return ResponseEntity.ok(myPageService.getMyNotifications(pageable));
    }

    /**
     * 나의 리워드
     * @param rewardType SAVE, USE
     * @param pageable
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/reward/{rewardType}")
    public ResponseEntity<RewardResponse> getMyReward(@PathVariable RewardType rewardType,
                                                      @PageableDefault(size=20,sort = "createdDate",direction = Sort.Direction.DESC)
                                                              Pageable pageable){
        return ResponseEntity.ok(myPageService.getMyRewardList(rewardType,pageable));
    }


    /**
     * 디렉터로 전환
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/switch-director")
    public ResponseEntity<Void> switchToDirector (){
        myPageService.switchToDirector();
        return ResponseEntity.ok().build();
    }


    /**
     * 나의 클래스
     * @param pageable
     * @return
     */
    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/lectures")
    public ResponseEntity<Slice<MyLectureResponse>> getMyLectures(Pageable pageable) {
        return ResponseEntity.ok(myPageService.getMyLectures(pageable));
    }

    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/payment/{orderId}")
    public ResponseEntity<CompleteLecturePaymentResponse> getMyPaymentLecture(@PathVariable Long orderId) {
        return ResponseEntity.ok(myPageService.getCompleteLecturePayment(orderId));
    }

    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/lectures/payment/{scheduleId}/teams/{teamId}")
    public ResponseEntity<TeamMemberPaymentStatusResponse> getTeamOrderStatus(@PathVariable Long scheduleId,
                                                                         @PathVariable Long teamId) {
        return ResponseEntity.ok(myPageService.getTeamOrderStatus(scheduleId, teamId));
    }

    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/reviews")
    public ResponseEntity<Page<MyReviewResponse>> getMyReviews(Pageable pageable) {
        return ResponseEntity.ok(myPageService.getMyReviews(pageable));
    }

    @PreAuthorize("hasAnyRole('USER,DIRECTOR')")
    @GetMapping("/coupons")
    public ResponseEntity<MyAvailableCouponResponse> getMyCoupons() {
        return ResponseEntity.ok(myPageService.getMyAvailableCoupons());
    }

    @GetMapping("/alarm")
    public ResponseEntity<AlarmStatusResponse> getMyEventAlarm(){
        return ResponseEntity.ok(myPageService.getMyAlarmStatus());
    }

}
