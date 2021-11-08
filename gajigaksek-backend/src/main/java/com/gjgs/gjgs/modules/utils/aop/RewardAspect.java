package com.gjgs.gjgs.modules.utils.aop;

import com.gjgs.gjgs.modules.member.dto.login.SignUpRequest;
import com.gjgs.gjgs.modules.reward.service.interfaces.RewardService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RewardAspect {

    private final RewardService rewardService;

    @AfterReturning("@annotation(saveReward)")
    public void saveReward(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        if (methodSignature.getMethod().getName().equals("saveAndLogin")
                && joinPoint.getArgs()[0] instanceof SignUpRequest){
            SignUpRequest signUpRequest = (SignUpRequest) joinPoint.getArgs()[0];
            checkRecommendNicknameAndSaveReward(signUpRequest);
        }
    }

    private void checkRecommendNicknameAndSaveReward(SignUpRequest signUpRequest) {
        if (signUpRequest.getRecommendNickname() != null && !signUpRequest.getRecommendNickname().isBlank()){
            rewardService.SaveRecommendReward(signUpRequest.getNickname(), signUpRequest.getRecommendNickname());
        }
    }


}
