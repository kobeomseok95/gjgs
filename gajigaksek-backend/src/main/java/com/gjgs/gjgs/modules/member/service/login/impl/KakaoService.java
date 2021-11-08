package com.gjgs.gjgs.modules.member.service.login.impl;

import com.gjgs.gjgs.modules.member.dto.login.KakaoProfile;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${spring.social.kakao.profile}")
    private String kakaoProfileUrl;

    /**
     * 카카오 accessToken을 통해 Kakao와 통신으로 해당 사용자 정보 가져오기
     *
     * @param accessToken : 카카오 토큰
     * @return KakaoProfile : 사용자 정보
     */
    public KakaoProfile getKakaoProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoProfileUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(response.getBody(), KakaoProfile.class);
        } catch (Exception e) {
            throw new MemberException(MemberErrorCodes.KAKAO_INTERACTION_FAIL);
        }
        throw new MemberException(MemberErrorCodes.KAKAO_INTERACTION_FAIL);
    }
}
