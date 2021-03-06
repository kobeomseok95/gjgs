package com.gjgs.gjgs.modules.matching.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.category.repositories.CategoryRepository;
import com.gjgs.gjgs.modules.matching.dto.MatchingRequest;
import com.gjgs.gjgs.modules.matching.dto.MatchingStatusResponse;
import com.gjgs.gjgs.modules.matching.enums.Status;
import com.gjgs.gjgs.modules.matching.exception.MatchingErrorCodes;
import com.gjgs.gjgs.modules.matching.service.interfaces.MatchingService;
import com.gjgs.gjgs.modules.matching.validator.MatchingRequestValidator;
import com.gjgs.gjgs.modules.zone.repositories.interfaces.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.*;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {
                MatchingController.class, MatchingRequestValidator.class
        }
)
class MatchingControllerTest extends RestDocsTestSupport {

    @MockBean MatchingService matchingService;
    @MockBean CategoryRepository categoryRepository;
    @MockBean ZoneRepository zoneRepository;

    final String TOKEN = "Bearer access-token";
    final String URL = "/api/v1/matching";
    MatchingRequest matchingRequest;

    @BeforeEach
    void setUserMockSetting(){
        securityUserMockSetting();
        when(zoneRepository.existsById(any())).thenReturn(true);
        when(categoryRepository.existsById(any())).thenReturn(true);

        matchingRequest= MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();
    }

    @DisplayName("?????? ??????")
    @Test
    void success_matching() throws Exception {
        //given

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("dayType").type(STRING).description(generateLinkCode(DAY_TYPE)),
                                fieldWithPath("timeType").type(STRING).description(generateLinkCode(TIME_TYPE)),
                                fieldWithPath("preferMemberCount").type(NUMBER).description("???????????? ??? ?????? ??????")
                        )
                ))
        ;
    }

    @DisplayName("???????????? ?????? zoneId??? ?????? ??????")
    @Test
    void matching_should_exist_zone_id() throws Exception {
        //given
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();
        when(zoneRepository.existsById(any())).thenReturn(false);
        when(categoryRepository.existsById(any())).thenReturn(true);


        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(objectMapper.writeValueAsString(matchingRequest))
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors[0].field").value("zoneId"))
                .andExpect(jsonPath("errors[0].reason").value("???????????? ?????? zoneId ?????????."));
    }

    @DisplayName("???????????? ?????? categoryId??? ?????? ??????")
    @Test
    void matching_should_exist_category_id() throws Exception {
        //given
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();

        when(categoryRepository.existsById(any())).thenReturn(false);
        when(zoneRepository.existsById(any())).thenReturn(true);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors[0].field").value("categoryId"))
                .andExpect(jsonPath("errors[0].reason").value("???????????? ?????? categoryId ?????????."));
    }

    @DisplayName("???????????? ?????? timeType?????? ?????? ??????")
    @Test
    void matchingForm_validation_timeType_fail() throws Exception {
        //given
        matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("Hello")
                .preferMemberCount(4)
                .build();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors[0].field").value("timeType"))
                .andExpect(jsonPath("errors[0].reason").value("Hello" + " ??? ????????? ????????? ????????????."));
    }

    @DisplayName("???????????? ?????? ????????? ?????? ??????")
    @Test
    void matchingForm_validation_dayType_fail() throws Exception {
        //given
        matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("Hello")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();


        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors[0].field").value("dayType"))
                .andExpect(jsonPath("errors[0].reason").value("Hello" + " ??? ????????? ????????? ????????????."));
    }

    @DisplayName("????????? ?????? ?????? ?????? ???????????? ?????? ??????")
    @Test
    void matchingForm_validation_preferMemberCount_fail() throws Exception {
        //given
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("Hello")
                .timeType("AFTERNOON")
                .preferMemberCount(5)
                .build();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors[0].field").value("preferMemberCount"))
                .andExpect(jsonPath("errors[0].reason").value("?????? ????????? ???????????? 4????????????."));
    }

    @DisplayName("?????? ????????? ?????? valid fail")
    @Test
    void matchingForm_valid_all_fail() throws Exception {
        //given
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("Hello")
                .timeType("Hello")
                .preferMemberCount(5)
                .build();

        when(categoryRepository.existsById(any())).thenReturn(false);
        when(zoneRepository.existsById(any())).thenReturn(false);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(createJson(matchingRequest))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getMessage()))
                .andExpect(jsonPath("code").value(MatchingErrorCodes.MATCHING_FORM_ERROR.getCode()))
                .andExpect(jsonPath("errors", hasSize(5)));
    }

    @DisplayName("?????? ??????")
    @Test
    void cancel_matching() throws Exception{

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        )
                ))
        ;

    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void get_matching_status() throws Exception{

        when(matchingService.status()).thenReturn(MatchingStatusResponse.of(Status.MATCHING));

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL)
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("status").description(generateLinkCode(MATCHING_STATUS))
                        )
                ))
        ;

    }


}
