package com.gjgs.gjgs.infra.config.jwt;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.exception.TokenErrorCodes;
import com.gjgs.gjgs.modules.member.exception.TokenException;
import com.gjgs.gjgs.modules.member.repository.interfaces.LogoutAccessTokenRedisRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.utils.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenUtil jwtTokenUtil;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final MemberRepository memberRepository;
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 서블릿 실행 전 작업

        // 1. Request Header 에서 토큰을 꺼냄 -> Bearer이 아닌 경우 null 반환
        String jwt = resolveToken(request);
        Optional<Member> member = null;

        // null이 아니면서 유효하지 않거나 redis(로그아웃된 토큰)에 들어있으면 토큰 에러 발생
        if (StringUtils.hasText(jwt) &&
                (!jwtTokenUtil.validateToken(jwt) || logoutAccessTokenRedisRepository.existsById(jwt) == true)) {
            throw new TokenException(TokenErrorCodes.INVALID_TOKEN);
        }

        // jwt가 null이 아니면 DB에서 조회
        if (StringUtils.hasText(jwt)){
            member = memberRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwt));
        }

        // null 이 아니면서 DB에 해당 멤버가 존재하지 않으면(탈퇴한 회원) 에러 발생
        if (StringUtils.hasText(jwt)
                && member.isEmpty()) {
            throw new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND);
        }


        // 두가지 if 문을 통해 토큰이 완전 정상이거나 null 인경우로 나뉨
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        if (StringUtils.hasText(jwt)) {
            Authentication authentication = jwtTokenUtil.getAuthentication(jwt,member.get().getAuthority());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 앞서 요청에 대해 필터링(서블릿 실행 전 작업을) 했고 이제 다음 필터로 넘기는 작업
        filterChain.doFilter(request, response);

        // 이후의 코드는 서블릿 실행 후의 작업
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenUtil.BEARER_TYPE)) {
            return bearerToken.substring(7);
        }

        // 토큰을 가지고 있으나 bearer 토큰이 아닌 경우
        if (StringUtils.hasText(bearerToken)) {
            throw new TokenException(TokenErrorCodes.TOKEN_TYPE_IS_NOT_BEARER);
        }
        return null;
    }
}