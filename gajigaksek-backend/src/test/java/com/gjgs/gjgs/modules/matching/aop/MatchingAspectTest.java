package com.gjgs.gjgs.modules.matching.aop;

import com.gjgs.gjgs.modules.matching.dto.MatchingRequest;
import com.gjgs.gjgs.modules.matching.exception.MatchingException;
import com.gjgs.gjgs.modules.matching.repository.interfaces.MatchingQueryRepository;
import com.gjgs.gjgs.modules.matching.service.impl.MatchingServiceImpl;
import com.gjgs.gjgs.modules.matching.service.interfaces.MatchingService;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingAspectTest {

    @InjectMocks MatchingServiceImpl matchingService;
    @InjectMocks MatchingAspect matchingAspect;

    @Mock MatchingQueryRepository matchingQueryRepository;
    @Mock SecurityUtil securityUtil;

    @DisplayName("이미 매칭중인 경우")
    @Test
    void check_is_already_matching() throws Exception{
        //given
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();
        MatchingService proxy = getProxy();
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(matchingQueryRepository.existsByUsername(any())).thenReturn(true);

        //when then
        assertThrows(MatchingException.class,
                () -> proxy.matching(matchingRequest));
    }


    private MatchingService getProxy() {
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(matchingService);
        aspectJProxyFactory.addAspect(matchingAspect);
        MatchingService proxy = aspectJProxyFactory.getProxy();
        return proxy;
    }
}
