package com.gjgs.gjgs.modules.member.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.category.repositories.CategoryRepository;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.dto.myinfo.*;
import com.gjgs.gjgs.modules.member.dto.mypage.DirectorMyPageResponse;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.enums.Alarm;
import com.gjgs.gjgs.modules.member.service.mypage.interfaces.MyInfoEditService;
import com.gjgs.gjgs.modules.member.validator.CategoryModifyRequestValidator;
import com.gjgs.gjgs.modules.member.validator.NicknameModifyRequestValidator;
import com.gjgs.gjgs.modules.member.validator.PhoneModifyRequestValidator;
import com.gjgs.gjgs.modules.member.validator.ZoneModifyRequestValidator;
import com.gjgs.gjgs.modules.zone.repositories.interfaces.ZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.ALARM_TYPE;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.MEMBER_AUTHORITY;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {MyInfoEditController.class,
                NicknameModifyRequestValidator.class, PhoneModifyRequestValidator.class,
                CategoryModifyRequestValidator.class, ZoneModifyRequestValidator.class
        }
)
class MyInfoEditControllerTest extends RestDocsTestSupport {

    final String URL = "/api/v1/mypage";
    final String BEARER_ACCESS_TOKEN = "Bearer access_token";

    @MockBean MyInfoEditService myInfoEditService;
    @MockBean CategoryRepository categoryRepository;
    @MockBean ZoneRepository zoneRepository;

    @DisplayName("Authorization 헤더 없이 접근")
    @Test
    void common_errors_should_require_header() throws Exception {
        // given
        String directorText = "change directorText";
        DirectorTextModifyRequest directorTextModifyRequest = DirectorTextModifyRequest.of(directorText);

        // when then
        mockMvc.perform(put(URL+"/directors/director-text")
                .content(createJson(directorTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @DisplayName("인증은 되었으나 접근 권한이 없을 때")
    @Test
    void common_errors_should_require_authorization() throws Exception {

        // given
        securityUserMockSetting();
        String directorText = "change directorText";

        // when then
        mockMvc.perform(put(URL+"/directors/director-text")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(directorText))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("마이페이지 편집창 조회")
    @Test
    void get_member_edit_page() throws Exception {
        // given
        securityUserMockSetting();

        Member member = MemberDummy.createTestMember();
        MyPageEditResponse myPageEditResponse = MyPageEditResponse.builder()
                .imageFileUrl(member.getImageFileUrl())
                .nickname(member.getNickname())
                .name(member.getName())
                .phone(member.getPhone())
                .memberCategoryId(member.getMemberCategories().stream()
                        .map(mc -> mc.getCategory().getId())
                        .collect(Collectors.toList()))
                .authority(member.getAuthority())
                .profileText(member.getProfileText())
                .directorText(member.getDirectorText())
                .sex(member.getSex())
                .age(member.getAge())
                .zoneId(1L)
                .build();

        when(myInfoEditService.editMyPage()).thenReturn(myPageEditResponse);

        // when then
        mockMvc.perform(get(URL+"/edit")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("imageFileUrl").type(STRING).description("프로필 이미지 URL (S3)"),
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("name").type(STRING).description("실명"),
                                fieldWithPath("phone").type(STRING).description("휴대폰 번호"),
                                fieldWithPath("memberCategoryId").type(ARRAY).description("취미 카테고리 ID 리스트"),
                                fieldWithPath("authority").type(STRING).description(generateLinkCode(MEMBER_AUTHORITY)),
                                fieldWithPath("profileText").type(STRING).description("자기소개"),
                                fieldWithPath("directorText").type(STRING).description("디렉터 자기소개"),
                                fieldWithPath("sex").type(STRING).description("성별"),
                                fieldWithPath("age").type(NUMBER).description("나이"),
                                fieldWithPath("zoneId").type(NUMBER).description("위치 ID")
                        )

                ));
    }

    @DisplayName("닉네임 수정")
    @Test
    void update_nickname() throws Exception {
        // given
        securityUserMockSetting();

        NicknameModifyRequest nicknameModifyRequest = NicknameModifyRequest.builder()
                .nickname("nickname")
                .build();

        // when then
        mockMvc.perform(put(URL+"/nickname")
                .content(createJson(nicknameModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(STRING).description("닉네임").attributes(field("constraints", "한글/영어(대,소문자)/숫자 조합으로 2 ~ 20자"))
                        )
                ));
    }

    @DisplayName("@valid 닉네임 수정 특수문자 실패 ")
    @Test
    void update_nickname_should_not_special_words() throws Exception {
        // given
        securityUserMockSetting();

        NicknameModifyRequest nicknameModifyRequest = NicknameModifyRequest.builder()
                .nickname("hello!!")
                .build();

        when(memberRepository.existsByUsername(any())).thenReturn(false);

        // when then
        mockMvc.perform(put(URL+"/nickname")
                .content(objectMapper.writeValueAsString(nicknameModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));
    }

    @DisplayName("닉네임 수정 1글자 실패")
    @Test
    void update_nickname_should_over_2_length() throws Exception{
        // given
        securityUserMockSetting();

        NicknameModifyRequest nicknameModifyRequest = NicknameModifyRequest.builder()
                .nickname("h")
                .build();

        when(memberRepository.existsByUsername(any())).thenReturn(false);

        // when then
        mockMvc.perform(put(URL+"/nickname")
                .content(createJson(nicknameModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));

    }

    @DisplayName("validator 닉네임 수정 - 중복")
    @Test
    void update_nickname_should_not_duplicate() throws Exception {
        // given
        securityUserMockSetting();

        NicknameModifyRequest nicknameModifyRequest = NicknameModifyRequest.builder()
                .nickname("hello")
                .build();
        when(memberRepository.existsByNickname(any())).thenReturn(true);

        // when then
        mockMvc.perform(put(URL+"/nickname")
                .content(createJson(nicknameModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].field").value("nickname"));
    }

    @DisplayName("전화번호 수정")
    @Test
    void update_phone() throws Exception {
        // given
        securityUserMockSetting();

        PhoneModifyRequest phoneModifyRequest = PhoneModifyRequest.builder()
                .phone("01012341234")
                .build();


        // when then
        mockMvc.perform(put(URL+"/phone")
                .content(createJson(phoneModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("phone").type(STRING).description("휴대폰 번호").attributes(field("constraints", "숫자로 10 ~ 11자 가능"))
                        )
                ));
    }

    @DisplayName("@valid 전화번호 수정 / 10자 미만인 경우")
    @Test
    void update_phone_should_over_10_length() throws Exception {
        // given
        securityUserMockSetting();

        PhoneModifyRequest phoneModifyRequest = PhoneModifyRequest.builder()
                .phone("010111")
                .build();

        // when then
        mockMvc.perform(put(URL+"/phone")
                .content(createJson(phoneModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));

    }

    @DisplayName("전화번호 수정 / 11자 초과인 경우")
    @Test
    void update_phone_should_not_over_11_length() throws Exception{
        // given
        securityUserMockSetting();

        PhoneModifyRequest phoneModifyRequest = PhoneModifyRequest.builder()
                .phone("010111123123123123123")
                .build();

        // when then
        mockMvc.perform(put(URL+"/phone")
                .content(createJson(phoneModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));
    }

    @DisplayName("validator 전화번호 중복 에러")
    @Test
    void update_phone_should_not_duplicate() throws Exception {
        // given
        securityUserMockSetting();

        PhoneModifyRequest phoneModifyRequest = PhoneModifyRequest.builder()
                .phone("01011111111")
                .build();

        when(memberRepository.existsByPhone(any())).thenReturn(true);

        // when then
        mockMvc.perform(put(URL+"/phone")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(phoneModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)));

    }

    @DisplayName("카테고리 수정")
    @Test
    void update_category() throws Exception {
        // given
        securityUserMockSetting();

        List<Long> categoryId = Arrays.asList(1L, 2L, 3L);
        CategoryModifyRequest categoryModifyRequest = CategoryModifyRequest.builder().categoryIdList(categoryId).build();
        when(categoryRepository.countCategoryByIdList(any())).thenReturn(3L);

        // when then
        mockMvc.perform(put(URL+"/category")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(categoryModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("categoryIdList").type(ARRAY).description("카테고리 Id 리스트").attributes(field("constraints", "변경할 카테고리 ID, 1 ~ 3개 선택 가능"))
                        )
                ));
    }

    @DisplayName("카테고리 수정 validator 에러")
    @Test
    void update_category_not_found() throws Exception {
        // given
        securityUserMockSetting();

        List<Long> categoryId = Arrays.asList(1L, 2L, 3L);
        CategoryModifyRequest categoryModifyRequest = CategoryModifyRequest.builder().categoryIdList(categoryId).build();
        when(categoryRepository.countCategoryByIdList(any())).thenReturn(2L);

        // when then
        mockMvc.perform(put(URL+"/category")
                .content(createJson(categoryModifyRequest))
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("categoryIdList"));

    }

    @DisplayName("자기소개 수정")
    @Test
    void update_profile() throws Exception {
        // given
        securityUserMockSetting();

        String profileText = "change profileText";
        ProfileTextModifyRequest profileTextModifyRequest = ProfileTextModifyRequest.of(profileText);


        // when then
        mockMvc.perform(put(URL+"/profile-text")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(profileTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("profileText").type(STRING).description("자기소개").attributes(field("constraints", "1000자 이하로 작성"))
                        )
                ));
    }

    @DisplayName("자기소개 수정 1000자 이상 valid")
    @Test
    void update_profile_should_not_over_1000_length() throws Exception {
        // given
        securityUserMockSetting();

        String text = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요아";
        ProfileTextModifyRequest profileTextModifyRequest = ProfileTextModifyRequest.of(text);

        // when then
        mockMvc.perform(put(URL+"/profile-text")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(objectMapper.writeValueAsString(profileTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("errors", hasSize(1)));
    }

    @DisplayName("위치 수정")
    @Test
    void update_zone() throws Exception {
        // given
        securityUserMockSetting();

        ZoneModifyRequest zoneModifyRequest = ZoneModifyRequest.builder()
                .zoneId(1L)
                .build();
        when(zoneRepository.existsById(any())).thenReturn(true);

        // when then
        mockMvc.perform(put(URL+"/zone")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(zoneModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("zoneId").type(NUMBER).description("지역 id")
                        )
                ));
    }

    @DisplayName("위치 수정 validator 에러")
    @Test
    void update_zone_should_exist_zone() throws Exception{
        // given
        securityUserMockSetting();

        ZoneModifyRequest zoneModifyRequest = ZoneModifyRequest.builder()
                .zoneId(1L)
                .build();
        when(zoneRepository.existsById(any())).thenReturn(false);

        // when then
        mockMvc.perform(put(URL+"/zone")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(zoneModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("zoneId"));

    }

    @DisplayName("이미지 수정")
    @Test
    void update_image() throws Exception {

        // given
        securityUserMockSetting();

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );


        // when then
        mockMvc.perform(fileUpload(URL+"/image")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestPartBody("file")
                ));
    }

    @DisplayName("디렉터 편집 페이지")
    @Test
    void get_director_edit() throws Exception {
        // given
        securityDirectorMockSetting();

        DirectorMyPageResponse directorMyPageResponse
                = DirectorMyPageResponse.of(MemberDummy.createTestMember());
        when(myInfoEditService.getDirectorEditPage()).thenReturn(directorMyPageResponse);

        // when then
        mockMvc.perform(get(URL+"/directors/edit")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("imageFileUrl").value(directorMyPageResponse.getImageFileUrl()))
                .andExpect(jsonPath("nickname").value(directorMyPageResponse.getNickname()))
                .andExpect(jsonPath("name").value(directorMyPageResponse.getName()))
                .andExpect(jsonPath("phone").value(directorMyPageResponse.getPhone()))
                .andExpect(jsonPath("directorText").value(directorMyPageResponse.getDirectorText()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("imageFileUrl").type(STRING).description("프로필 URL"),
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("name").type(STRING).description("성함"),
                                fieldWithPath("phone").type(STRING).description("휴대폰 번호"),
                                fieldWithPath("directorText").type(STRING).description("디렉터 소개글")
                        )
                ));
    }

    @DisplayName("디렉터 소개 수정")
    @Test
    void update_director_text() throws Exception  {
        // given
        securityDirectorMockSetting();

        String directorText = "change directorText";
        DirectorTextModifyRequest directorTextModifyRequest = DirectorTextModifyRequest.of(directorText);

        // when then
        mockMvc.perform(put(URL+"/directors/director-text")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(directorTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("directorText").type(STRING).description("디렉터 자기소개").attributes(field("constraints", "1000자 이하로 작성"))
                        )
                ));
    }

    @DisplayName("디렉터소개 수정 1000자 이상 valid")
    @Test
    void update_director_text_should_not_over_1000_length() throws Exception {
        // given
        securityDirectorMockSetting();

        securityDirectorMockSetting();
        String text = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요아";
        DirectorTextModifyRequest directorTextModifyRequest = DirectorTextModifyRequest.of(text);

        // when then
        mockMvc.perform(put(URL+"/directors/director-text")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(objectMapper.writeValueAsString(directorTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("errors", hasSize(1)));

    }

    @DisplayName("이벤트 알림 수신 여부")
    @Test
    void edit_my_event_alarm() throws Exception{
        // given
        securityUserMockSetting();

        AlarmEditRequest alarmEditRequest = AlarmEditRequest.builder()
                .type(Alarm.EVENT)
                .isActive(false)
                .build();

        // when then
        mockMvc.perform(post(URL+"/alarm")
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .content(createJson(alarmEditRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                            fieldWithPath("active").description("알림 수신 동의 여부"),
                            fieldWithPath("type").description(generateLinkCode(ALARM_TYPE))
                        )
                ));
    }


}
