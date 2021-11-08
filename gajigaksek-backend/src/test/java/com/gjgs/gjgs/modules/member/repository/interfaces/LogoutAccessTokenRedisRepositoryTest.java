package com.gjgs.gjgs.modules.member.repository.interfaces;

import com.gjgs.gjgs.config.CustomDataRedisTest;
import com.gjgs.gjgs.modules.member.redis.LogoutAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@CustomDataRedisTest
class LogoutAccessTokenRedisRepositoryTest {

    @Autowired
    LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    @BeforeEach
    void clear(){
        logoutAccessTokenRedisRepository.deleteAll();
    }

    @DisplayName("save")
    @Test
    void save() throws Exception{
        //given
        String accessToken = "accessToken";
        String username = "username";
        Long expiration = 3000L;
        LogoutAccessToken logoutAccessToken = LogoutAccessToken.createLogoutAccessToken(accessToken, username, expiration);

        //when
        logoutAccessTokenRedisRepository.save(logoutAccessToken);

        //then
        LogoutAccessToken find = logoutAccessTokenRedisRepository.findById(accessToken).get();

        assertAll(
                () -> assertEquals(accessToken,find.getId()),
                () -> assertEquals(username,find.getUsername()),
                () -> assertEquals(expiration/1000,find.getExpiration())
        );


    }
}
