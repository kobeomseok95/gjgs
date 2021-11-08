package com.gjgs.gjgs.modules.member.service.login.impl;

import com.gjgs.gjgs.modules.member.dto.login.KakaoProfile;
import com.gjgs.gjgs.modules.member.dto.login.LoginResponse;
import com.gjgs.gjgs.modules.member.dto.login.SignUpRequest;
import com.gjgs.gjgs.modules.member.dto.login.TokenDto;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.enums.Authority;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.exception.TokenErrorCodes;
import com.gjgs.gjgs.modules.member.exception.TokenException;
import com.gjgs.gjgs.modules.member.redis.LogoutAccessToken;
import com.gjgs.gjgs.modules.member.redis.RefreshToken;
import com.gjgs.gjgs.modules.member.repository.interfaces.LogoutAccessTokenRedisRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberJdbcRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.RefreshTokenRedisRepository;
import com.gjgs.gjgs.modules.member.service.login.interfaces.LoginService;
import com.gjgs.gjgs.modules.utils.aop.saveReward;
import com.gjgs.gjgs.modules.utils.jwt.JwtTokenUtil;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;
    private final MemberJdbcRepository memberJdbcRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final KakaoService kakaoService;
    private final JwtTokenUtil jwtTokenUtil;
    private final SecurityUtil securityUtil;



    /**
     * @param accessToken : kakao accessToken
     * @return 기존 가입된 유저의 경우 JWT ACCESS,REFRESH 토큰 반환
     * 가입되지 않은 유저의 경우 KAKAO PROFILE 반환
     */
    @Override
    public LoginResponse login(String accessToken, String fcmToken) {
        KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(accessToken);
        Optional<Member> member = memberRepository.findByUsername(String.valueOf(kakaoProfile.getId()));
        if (member.isEmpty()) {
            return LoginResponse.createKakaoProfileLoginResponse(kakaoProfile);
        }
        Member currentUser = member.get();
        currentUser.changeFcmToken(fcmToken);
        TokenDto tokenDto = jwtTokenUtil.generateTokenDto(currentUser);
        createRedisRefreshTokenAndSave(currentUser.getUsername(), tokenDto.getRefreshToken());
        return LoginResponse.createTokenLoginResponse(currentUser.getId(), tokenDto, kakaoProfile);
    }

    @Override
    public LoginResponse webLogin(String accessToken, String authority) {
        KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(accessToken);
        Optional<Member> member = memberRepository.findByUsername(String.valueOf(kakaoProfile.getId()));
        checkCurrentUserAuthority(authority, member);
        Member currentUser = member.get();
        TokenDto tokenDto = jwtTokenUtil.generateTokenDto(currentUser);
        createRedisRefreshTokenAndSave(currentUser.getUsername(), tokenDto.getRefreshToken());
        return LoginResponse.createTokenLoginResponse(currentUser.getId(), tokenDto, kakaoProfile);
    }

    private void checkCurrentUserAuthority(String authority, Optional<Member> member) {
        if (member.isEmpty()){
            throw new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND);
        }
        if(!authority.equals("director") && !authority.equals("admin")){
            throw new MemberException(MemberErrorCodes.NOT_EXIST_AUTHORITY);
        }
        String auth =  "ROLE_"+ authority.toUpperCase();
        if (!member.get().getAuthority().equals(Authority.valueOf(auth))){
            if (auth.equals(Authority.ROLE_DIRECTOR.name())){
                throw new MemberException(MemberErrorCodes.MEMBER_NOT_DIRECTOR);
            }
            else{
                throw new MemberException(MemberErrorCodes.MEMBER_NOT_ADMIN);
            }
        }
    }

    /**
     * @param signUpRequest 회원가입을 위한 회원 정보
     * @return JWT 토큰 반환
     */
    @Override
    @saveReward
    public LoginResponse saveAndLogin(SignUpRequest signUpRequest) {
        Member member = memberRepository.save(Member.of(signUpRequest));
        memberJdbcRepository.insertMemberCategoryList(member.getId(), signUpRequest.getCategoryIdList());
        TokenDto tokenDto = jwtTokenUtil.generateTokenDto(member);
        createRedisRefreshTokenAndSave(member.getUsername(), tokenDto.getRefreshToken());
        return LoginResponse.createTokenLoginResponse(member.getId(), tokenDto, signUpRequest.getId(),signUpRequest.getImageFileUrl());
    }

    /**
     * refreshToken이 7일 이하로 남았을 경우 Refresh도 재발급
     * refreshToken이 7일 초과로 남았을 경우 Access만 재발급
     *
     * @param refreshToken JWT RefreshToken
     * @return JWT 토큰 반환
     */
    @Override
    public TokenDto reissue(String refreshToken) {
        String token = resolveToken(refreshToken);
        String username = getUsername();
        Authentication authentication = getAuthentication(token);
        String refreshFromRedis = refreshTokenRedisRepository.findById(username)
                .orElseThrow(() -> new TokenException(TokenErrorCodes.NO_REFRESH_TOKEN)).getRefreshToken();

        if (token.equals(refreshFromRedis)) {
            if (jwtTokenUtil.getRemainingMilliSeconds(refreshFromRedis) < JwtTokenUtil.REFRESH_TOKEN_REISSUE_TIME) {
                TokenDto tokenDto = jwtTokenUtil.generateTokenDto(authentication);
                createRedisRefreshTokenAndSave(username,tokenDto.getRefreshToken());
                return tokenDto;
            }

            String newAccessToken = jwtTokenUtil.generateAccessToken(authentication);
            return TokenDto.of(
                    JwtTokenUtil.BEARER_TYPE,
                    newAccessToken,
                    refreshFromRedis,
                    jwtTokenUtil.getRemainingMilliSeconds(newAccessToken),
                    jwtTokenUtil.getRemainingMilliSeconds(refreshFromRedis));
        }
        throw new TokenException(TokenErrorCodes.INVALID_TOKEN);
    }

    private Authentication getAuthentication(String token) {
        Authority authority = securityUtil.getAuthority()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        Authentication authentication = jwtTokenUtil.getAuthentication(token,authority);
        return authentication;
    }

    private String getUsername() {
        return securityUtil.getCurrentUsername()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }

    /**
     * redis에서 refreshToken을 제거
     * redis에 accessToken을 남은 시간동안 삽입 -> 해당 토큰으로 로그인 막기 위함
     *
     * @param accessToken  JWT
     * @param refreshToken JWT
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        String token = resolveToken(accessToken);
        String username = getUsername();

        memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND))
                .clearFcmToken();
        refreshTokenRedisRepository.deleteById(username);
        logoutAccessTokenRedisRepository
                .save(LogoutAccessToken.createLogoutAccessToken(token,username, jwtTokenUtil.getRemainingMilliSeconds(token)));
    }

    private String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(JwtTokenUtil.BEARER_TYPE)) {
            return token.substring(7);
        }
        throw new TokenException(TokenErrorCodes.TOKEN_TYPE_IS_NOT_BEARER);
    }

    private void createRedisRefreshTokenAndSave(String username, String refreshToken) {
        RefreshToken redisRefreshToken = RefreshToken.createRefreshToken(username, refreshToken
                , jwtTokenUtil.getRemainingMilliSeconds(refreshToken));
        refreshTokenRedisRepository.save(redisRefreshToken);
    }


    // todo TEST용으로 추후 삭제
    public LoginResponse fakeLogin(String username){
        Optional<Member> member = memberRepository.findByUsername(username);
        Member mem = member.get();
        TokenDto tokenDto = jwtTokenUtil.generateTokenDto(mem);
        createRedisRefreshTokenAndSave(mem.getUsername(), tokenDto.getRefreshToken());
        KakaoProfile kakaoProfile = KakaoProfile.builder()
                .id(1L)
                .properties(KakaoProfile.Properties.builder()
                        .nickname(mem.getNickname())
                        .profile_image(mem.getImageFileUrl())
                        .thumbnail_image(mem.getImageFileUrl())
                        .build())
                .build();
        return LoginResponse.createTokenLoginResponse(mem.getId(), tokenDto, kakaoProfile);
    }

}

