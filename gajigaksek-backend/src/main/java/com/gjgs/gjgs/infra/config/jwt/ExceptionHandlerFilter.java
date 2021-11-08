package com.gjgs.gjgs.infra.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.exception.TokenException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import com.gjgs.gjgs.modules.utils.response.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// 필터에서 걸리는 에러를 처리하는 필터
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // objectMapper가 LocalDateTime을 인식할 수 있도록 하는 설정
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            setErrorResponse(response, e.getErrorCodeBase(), e.getStatus());
        } catch (MemberException e) {
            setErrorResponse(response, e.getErrorCodeBase(), e.getStatus());
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorBase errorCodeBase, int status) throws IOException {
        String errorResponse = objectMapper.writeValueAsString(
                ErrorResponse.of(errorCodeBase));
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(errorResponse);
    }
}
