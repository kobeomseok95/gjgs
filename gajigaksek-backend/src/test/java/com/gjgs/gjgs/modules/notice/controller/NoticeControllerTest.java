package com.gjgs.gjgs.modules.notice.controller;


import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.dummy.NoticeDummy;
import com.gjgs.gjgs.modules.notice.dto.NoticeForm;
import com.gjgs.gjgs.modules.notice.dto.NoticeResponse;
import com.gjgs.gjgs.modules.notice.enums.NoticeType;
import com.gjgs.gjgs.modules.notice.service.interfaces.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDateTime;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.NOTICE_TYPE;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        value = {NoticeController.class}
)
class NoticeControllerTest extends RestDocsTestSupport {

    @MockBean NoticeService noticeService;

    final String TOKEN = "Bearer AccessToken";
    final String URL = "/api/v1/notices";

    @DisplayName("?????? ?????? 1. ????????? ?????? ?????? ?????? ??????")
    @Test
    void common_errors_forbidden() throws Exception{
        // given
        securityUserMockSetting();
        NoticeForm noticeForm = NoticeForm.builder()
                .text("text")
                .title("title")
                .noticeType(NoticeType.ALL)
                .build();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(objectMapper.writeValueAsString(noticeForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("???????????? ??????")
    @Test
    void create_notice() throws Exception{

        // given
        securityAdminMockSetting();
        NoticeForm noticeForm = NoticeForm.builder()
                .text("text")
                .title("title")
                .noticeType(NoticeType.ALL)
                .build();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(objectMapper.writeValueAsString(noticeForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("title").type(STRING).description("???????????? ??????"),
                                fieldWithPath("text").type(STRING).description("???????????? ??????"),
                                fieldWithPath("noticeType").type(STRING).description(generateLinkCode(NOTICE_TYPE))
                        )
                ));
    }

    @DisplayName("?????? ???????????? ????????????")
    @Test
    void get_notices() throws Exception{
        // given
        securityUserMockSetting();
        when(noticeService.getNotice(any(),any())).thenReturn(createPageNoticeDto(PageRequest.of(0,4)));

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL+"?page=1&type=ALL")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].title").value("AllTitle"))
                .andExpect(jsonPath("content[0].text").value("AllText"))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
                .andExpect(jsonPath("pageable.pageSize").value(4))
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(1))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ?????????"),
                                parameterWithName("type").description(generateLinkCode(NOTICE_TYPE))
                        ),
                        responseFields(
                                fieldWithPath("content[].noticeId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("content[].title").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].text").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].createdDate").type(STRING).description("???????????? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    @DisplayName("Authentication ?????? ?????? ???????????? ????????????")
    @Test
    void get_notices_forbidden_exception() throws Exception{

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL+"?page=1&type=ALL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("????????? ???????????? ????????????")
    @Test
    void get_notices_director() throws Exception{
        // given
        securityDirectorMockSetting();
        when(noticeService.getNotice(any(),any())).thenReturn(createPageNoticeDto(PageRequest.of(0,4)));

        // when then
        mockMvc.perform(get(URL+"?page=1&type=DIRECTOR")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].title").value("AllTitle"))
                .andExpect(jsonPath("content[0].text").value("AllText"))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
                .andExpect(jsonPath("pageable.pageSize").value(4))
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(1))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ?????????"),
                                parameterWithName("type").description(generateLinkCode(NOTICE_TYPE))
                        ),
                        responseFields(
                                fieldWithPath("content[].noticeId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("content[].title").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].text").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].createdDate").type(STRING).description("???????????? ?????? ??????")
                        ).and(pageDescriptor())
                ))
        ;
    }

    @DisplayName("?????? ?????? ????????? ???????????? ????????????")
    @Test
    void get_notices_director_forbidden() throws Exception{

        // when then
        mockMvc.perform(get(URL+"/?type=director?page=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
        ;
    }

    @DisplayName("???????????? ?????? ?????? ????????????")
    @Test
    void get_notice() throws Exception {

        // given
        securityUserMockSetting();
        when(noticeService.getOneNotice(any()))
                .thenReturn(NoticeResponse.builder()
                        .noticeId(1L)
                        .title("test")
                        .text("test")
                        .createdDate(LocalDateTime.now())
                        .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL+"/{noticeId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("noticeId").description("?????? ????????? ???????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("noticeId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("title").type(STRING).description("???????????? ??????"),
                                fieldWithPath("text").type(STRING).description("???????????? ??????"),
                                fieldWithPath("createdDate").type(STRING).description("???????????? ?????? ??????")
                        )
                ));
    }

    @DisplayName("???????????? ????????????")
    @Test
    void update_notice() throws Exception{

        // given
        securityAdminMockSetting();
        NoticeForm noticeForm = NoticeForm.builder()
                .text("text")
                .title("title")
                .noticeType(NoticeType.ALL)
                .build();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL+"/{noticeId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(objectMapper.writeValueAsString(noticeForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("noticeId").description("????????? ???????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("text").type(STRING).description("????????? ???????????? ??????"),
                                fieldWithPath("title").type(STRING).description("????????? ???????????? ??????"),
                                fieldWithPath("noticeType").type(STRING).description(generateLinkCode(NOTICE_TYPE))
                        )
                ))
        ;
    }

    @DisplayName("?????? ?????? ???????????? ???????????? ????????????")
    @Test
    void update_notice_forbidden() throws Exception{
        // given
        NoticeForm noticeForm = NoticeForm.builder()
                .text("text")
                .title("title")
                .noticeType(NoticeType.ALL)
                .build();

        // when then
        mockMvc.perform(put(URL+"/1")
                .content(objectMapper.writeValueAsString(noticeForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
        ;
    }

    @DisplayName("???????????? ????????????")
    @Test
    void delete_notice() throws Exception{
        // given
        securityAdminMockSetting();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL+"/{noticeId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("noticeId").description("????????? ???????????? ID")
                        )
                ))
        ;
    }

    @DisplayName("admin ???????????? ?????? ??? ???????????? ????????????")
    @Test
    void delete_notice_forbidden() throws Exception{
        // when then
        securityUserMockSetting();

        mockMvc.perform(delete(URL+"/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
        ;
    }

    private Page<NoticeResponse> createPageNoticeDto(Pageable pageable) {
        return new PageImpl<>(NoticeDummy.createNoticeDtoList(),
                pageable,
                1);
    }
}
