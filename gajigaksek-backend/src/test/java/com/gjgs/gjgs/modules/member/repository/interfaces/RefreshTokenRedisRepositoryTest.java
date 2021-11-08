package com.gjgs.gjgs.modules.member.repository.interfaces;


import com.gjgs.gjgs.config.CustomDataRedisTest;
import com.gjgs.gjgs.modules.member.redis.RefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@CustomDataRedisTest
class RefreshTokenRedisRepositoryTest {

    @Autowired RefreshTokenRedisRepository refreshTokenRedisRepository;

    @BeforeEach
    void clear(){
        refreshTokenRedisRepository.deleteAll();
    }

    @DisplayName("save")
    @Test
    void save() throws Exception{
        //given
        //given
        String username = "username";
        String refreshToken = "refreshToken";
        Long expiration = 3000L;
        RefreshToken refreshTokenRedis = RefreshToken.createRefreshToken(username, refreshToken, expiration);

        //when
        refreshTokenRedisRepository.save(refreshTokenRedis);

        //then
        RefreshToken findRefreshToken = refreshTokenRedisRepository.findById(username).get();
        assertAll(
                () -> assertEquals(username,findRefreshToken.getId()),
                () -> assertEquals(refreshToken,findRefreshToken.getRefreshToken()),
                () -> assertEquals(expiration/1000,findRefreshToken.getExpiration())
        );
    }


}
