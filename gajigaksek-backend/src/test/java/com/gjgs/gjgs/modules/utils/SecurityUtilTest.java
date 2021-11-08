package com.gjgs.gjgs.modules.utils;

import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.enums.Authority;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @InjectMocks
    SecurityUtil securityUtil;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
    }


    @DisplayName("사용자 존재")
    @Test
    void exist_user() throws Exception {
        // given
        Authentication authentication = MemberDummy.createAuthentication();

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertEquals(authentication.getName(), securityUtil.getCurrentUsername().get());


    }

    @DisplayName("익명 사용자")
    @Test
    void anonymous_user() throws Exception {
        assertEquals(securityUtil.getCurrentUsername(), Optional.empty());
    }

    @DisplayName("권한 추출")
    @Test
    void get_authority() throws Exception{
        // given
        Authentication authentication = MemberDummy.createAuthentication();

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertEquals(Authority.ROLE_USER, securityUtil.getAuthority().get());
    }

    @DisplayName("익명 권한 추출")
    @Test
    void get_none_authority() throws Exception{

        // then
        assertEquals(Optional.empty(), securityUtil.getAuthority());
    }


}

