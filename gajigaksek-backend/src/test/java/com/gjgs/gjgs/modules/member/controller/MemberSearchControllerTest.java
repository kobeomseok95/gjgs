package com.gjgs.gjgs.modules.member.controller;


import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.dto.search.MemberSearchResponse;
import com.gjgs.gjgs.modules.member.service.search.interfaces.MemberSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {MemberSearchController.class}
)
class MemberSearchControllerTest extends RestDocsTestSupport {
    final String TOKEN = "Bearer AccessToken";
    final String URL = "/api/v1/members";

    @MockBean MemberSearchService memberSearchService;

    @BeforeEach
    void setUpMockUser() {
        securityAdminMockSetting();
    }

    @DisplayName("con, pageable ????????? member search")
    @Test
    void search_member() throws Exception{

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.Direction.DESC, "createdDate");
        PageImpl<MemberSearchResponse> dtos = new PageImpl<>(MemberDummy.createMemberSearchDtoList(1),
                pageRequest,
                1);

        when(memberSearchService.searchMember(any(),any())).thenReturn(dtos);

        // when then
        mockMvc.perform(get(URL+"?nickname=nick&createdDateStart=2021-10-20&createdDateEnd=2021-10-20&authority=ROLE_USER")
                .header(HttpHeaders.AUTHORIZATION,TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(
                                parameterWithName("nickname").description("??????????????? ?????? ?????????").optional(),
                                parameterWithName("createdDateStart").description("??????????????? ?????? ?????? ??????(yyyy-mm-dd)").optional(),
                                parameterWithName("createdDateEnd").description("??????????????? ?????? ????????? ??????(yyyy-mm-dd)").optional(),
                                parameterWithName("authority").description(generateLinkCode(DocUrl.MEMBER_AUTHORITY)).optional()
                        ),
                        responseFields(
                                fieldWithPath("content[0].id").description("?????? id"),
                                fieldWithPath("content[0].nickname").description("?????????"),
                                fieldWithPath("content[0].phone").description("????????????"),
                                fieldWithPath("content[0].authority").description("??????"),
                                fieldWithPath("content[0].createdDate").description("???????????????")
                        ).and(pageDescriptor())
                        ));
    }
}
