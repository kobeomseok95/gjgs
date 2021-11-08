package com.gjgs.gjgs.modules.member.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.category.entity.Category;
import com.gjgs.gjgs.modules.dummy.BulletinDummy;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.dummy.QuestionDummy;
import com.gjgs.gjgs.modules.dummy.RewardDummy;
import com.gjgs.gjgs.modules.member.dto.mypage.*;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.service.mypage.interfaces.MyPageService;
import com.gjgs.gjgs.modules.member.validator.NicknameModifyRequestValidator;
import com.gjgs.gjgs.modules.member.validator.PhoneModifyRequestValidator;
import com.gjgs.gjgs.modules.notification.enums.NotificationType;
import com.gjgs.gjgs.modules.payment.entity.OrderStatus;
import com.gjgs.gjgs.modules.utils.querydsl.RepositorySliceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.*;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static com.gjgs.gjgs.modules.member.dto.mypage.MyLectureResponse.CurrentLectureStatus.DONE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        value = {MyPageController.class,
                NicknameModifyRequestValidator.class, PhoneModifyRequestValidator.class
        }
)
class MyPageControllerTest extends RestDocsTestSupport {

    private final String URL = "/api/v1/mypage";
    final String BEARER_ACCESS_TOKEN = "Bearer access_token";

    @MockBean MyPageService myPageService;

    @BeforeEach
    void setUserMockSetting(){
        securityUserMockSetting();
    }

    @DisplayName("마이페이지 조회")
    @Test
    void get_mypage() throws Exception {
        // given
        Member member = MemberDummy.createTestMember();
        MyPageResponse myPageResponse = MyPageResponse.builder()
                .nickname(member.getNickname())
                .imageFileUrl(member.getImageFileUrl())
                .authority(member.getAuthority())
                .memberCategoryIdList(List.of(1L))
                .build();
        when(myPageService.getMyPage()).thenReturn(myPageResponse);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("imageFileUrl").type(STRING).description("프로필 이미지 URL (S3)"),
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("memberCategoryIdList[]").type(ARRAY).description("취미 카테고리 ID 리스트"),
                                fieldWithPath("authority").type(STRING).description(generateLinkCode(MEMBER_AUTHORITY)),
                                fieldWithPath("totalReward").type(NUMBER).description("회원이 보유한 리워드 액수")
                        )
                ));
    }

    @DisplayName("내 게시글 조회")
    @Test
    void get_my_bulletins() throws Exception {
        // given
        List<MyBulletinDto> myBulletinDtoList = Arrays.asList(BulletinDummy.createBulletinDto());
        when(myPageService.getMyBulletins()).thenReturn(myBulletinDtoList);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/bulletins")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("myBulletinDtoList", hasSize(1)))
//                .andExpect(jsonPath("myBulletinDtoList[0].thumbnailImageFileUrl").value(myBulletinDtoList.get(0).getThumbnailImageFileUrl()))
//                .andExpect(jsonPath("myBulletinDtoList[0].zoneId").value(myBulletinDtoList.get(0).getZoneId()))
//                .andExpect(jsonPath("myBulletinDtoList[0].title").value(myBulletinDtoList.get(0).getTitle()))
//                .andExpect(jsonPath("myBulletinDtoList[0].age").value(myBulletinDtoList.get(0).getAge().name()))
//                .andExpect(jsonPath("myBulletinDtoList[0].timeType").value(myBulletinDtoList.get(0).getTimeType()))
//                .andExpect(jsonPath("myBulletinDtoList[0].currentPeople").value(myBulletinDtoList.get(0).getCurrentPeople()))
//                .andExpect(jsonPath("myBulletinDtoList[0].maxPeople").value(myBulletinDtoList.get(0).getMaxPeople()))
//                .andExpect(jsonPath("myBulletinDtoList[0].status").value(myBulletinDtoList.get(0).isStatus()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("myBulletinDtoList[].bulletinId").type(NUMBER).description("모집글 ID"),
                                fieldWithPath("myBulletinDtoList[].thumbnailImageFileUrl").type(STRING).description("모집글 썸네일 URL"),
                                fieldWithPath("myBulletinDtoList[].zoneId").type(NUMBER).description("지역 ID"),
                                fieldWithPath("myBulletinDtoList[].title").type(STRING).description("모집글 제목"),
                                fieldWithPath("myBulletinDtoList[].age").type(STRING).description("모집글의 모집 선호 나이대"),
                                fieldWithPath("myBulletinDtoList[].timeType").type(STRING).description("모집글의 모집 선호 시간대"),
                                fieldWithPath("myBulletinDtoList[].currentPeople").type(NUMBER).description("팀의 현재 인원"),
                                fieldWithPath("myBulletinDtoList[].maxPeople").type(NUMBER).description("팀의 모집 가능 최대 인원"),
                                fieldWithPath("myBulletinDtoList[].status").type(BOOLEAN).description("모집글의 모집 상태 여부")
                        )
                ));
    }

    @DisplayName("내가 작성한 문의글 조회")
    @Test
    void get_my_questions() throws Exception {
        // given
        List<MyQuestionDto> myQuestionDtoList = Arrays.asList(QuestionDummy.createMyQuestionDto());
        when(myPageService.getMyQuestions()).thenReturn(myQuestionDtoList);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/question")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("myQuestionDtoList", hasSize(1)))
//                .andExpect(jsonPath("myQuestionDtoList[0].questionId").value(myQuestionDtoList.get(0).getQuestionId()))
//                .andExpect(jsonPath("myQuestionDtoList[0].classTitle").value(myQuestionDtoList.get(0).getClassTitle()))
//                .andExpect(jsonPath("myQuestionDtoList[0].directorNickname").value(myQuestionDtoList.get(0).getDirectorNickname()))
//                .andExpect(jsonPath("myQuestionDtoList[0].mainText").value(myQuestionDtoList.get(0).getMainText()))
//                .andExpect(jsonPath("myQuestionDtoList[0].replyText").value(myQuestionDtoList.get(0).getReplyText()))
//                .andExpect(jsonPath("myQuestionDtoList[0].questionStatus").value(myQuestionDtoList.get(0).getQuestionStatus().name()))
//                .andExpect(jsonPath("myQuestionDtoList[0].questionDate").isNotEmpty())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("myQuestionDtoList[].questionId").type(NUMBER).description("문의글 ID"),
                                fieldWithPath("myQuestionDtoList[].classTitle").type(STRING).description("클래스 제목"),
                                fieldWithPath("myQuestionDtoList[].directorNickname").type(STRING).description("디렉터 닉네임"),
                                fieldWithPath("myQuestionDtoList[].mainText").type(STRING).description("문의 내용"),
                                fieldWithPath("myQuestionDtoList[].replyText").type(STRING).description("문의 답변"),
                                fieldWithPath("myQuestionDtoList[].questionStatus").type(STRING).description(generateLinkCode(QUESTION_STATUS)),
                                fieldWithPath("myQuestionDtoList[].questionDate").type(STRING).description("질문글 작성 날짜")
                        )
                ));
    }

    @DisplayName("내가 작성한 문의글 내용 수정")
    @Test
    void update_question() throws Exception {
        //given
        QuestionMainTextModifyRequest questionMainTextModifyRequest = QuestionMainTextModifyRequest.of("hello world I'm Iron man");

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL + "/question/{questionId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .content(createJson(questionMainTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("questionId").description("수정할 질문글 ID")
                        ),
                        requestFields(
                                fieldWithPath("mainText").type(STRING).description("문의 text").attributes(field("constraints", "문의글은 10 ~ 1000자 이내로 작성"))

                        )
                ));
    }

    @DisplayName("내가 작성한 문의글 내용 수정 @valid test - 10자 미만인 경우")
    @Test
    void update_question_should_over_10_length() throws Exception {
        //given
        QuestionMainTextModifyRequest questionMainTextModifyRequest = QuestionMainTextModifyRequest.of("안녕하세요감사합니");

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL + "/question/{questionId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .content(createJson(questionMainTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("errors", hasSize(1)));
    }

    @DisplayName("내가 작성한 문의글 내용 수정 @valid test - 1000자를 넘을 경우")
    @Test
    void update_question_should_not_over_1000_length() throws Exception {
        // given
        QuestionMainTextModifyRequest questionMainTextModifyRequest = QuestionMainTextModifyRequest.of("안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요아");

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL + "/question/{questionId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .content(createJson(questionMainTextModifyRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                ;
    }

    @DisplayName("내가 작성한 문의글 삭제")
    @Test
    void delete_my_question() throws Exception {
        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/question/{questionId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("questionId").description("삭제할 질문글 ID")
                        )
                ));
    }

    @DisplayName("회원 정보 창")
    @Test
    void get_info() throws Exception {
        //given
        Member member = MemberDummy.createTestMember();
        Category category = Category.builder()
                .id(10L)
                .build();
        member.setMemberCategories(Arrays.asList(category));
        InfoResponse infoResponse = InfoResponse.builder()
                .imageFileUrl(member.getImageFileUrl())
                .sex(member.getSex())
                .age(member.getAge())
                .zoneId(1L)
                .categoryIdList(member.getMemberCategories().stream()
                        .map(mc -> mc.getCategory().getId()).collect(Collectors.toList()))
                .profileText(member.getProfileText())
                .nickname(member.getNickname())
                .build();

        when(myPageService.getInfo(any())).thenReturn(infoResponse);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/info/{memberId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("imageFileUrl").value(infoResponse.getImageFileUrl()))
//                .andExpect(jsonPath("sex").value(infoResponse.getSex().name()))
//                .andExpect(jsonPath("age").value(infoResponse.getAge()))
//                .andExpect(jsonPath("zoneId").value(infoResponse.getZoneId()))
//                .andExpect(jsonPath("categoryIdList[0]").value(infoResponse.getCategoryIdList().get(0)))
//                .andExpect(jsonPath("profileText").value(infoResponse.getProfileText()))
//                .andExpect(jsonPath("nickname").value(infoResponse.getNickname()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("조회할 멤버 ID")
                        ),
                        responseFields(
                                fieldWithPath("imageFileUrl").type(STRING).description("프로필 이미지 URL"),
                                fieldWithPath("sex").type(STRING).description(generateLinkCode(SEX_TYPE)),
                                fieldWithPath("age").type(NUMBER).description("나이"),
                                fieldWithPath("zoneId").type(NUMBER).description("지역 ID"),
                                fieldWithPath("categoryIdList[]").type(ARRAY).description("선호하는 카테고리 ID 리스트"),
                                fieldWithPath("profileText").type(STRING).description("자기소개"),
                                fieldWithPath("nickname").type(STRING).description("닉네임")
                        )
                ));
    }

    @DisplayName("디렉터로 전환")
    @Test
    void switch_director() throws Exception{
        // given
        securityUserMockSetting();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/switch-director")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        )
                ))
        ;
    }

    @DisplayName("User 권한이 아닌데 디렉터로 전환할 경우")
    @Test
    void switch_director_should_user_authorization() throws Exception{
        //given
        securityDirectorMockSetting();

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/switch-director")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

   @DisplayName("리워드 상세 조회")
   @Test
   void get_my_reward_use() throws Exception{
       // given
       when(myPageService.getMyRewardList(any(),any())).thenReturn(createRewardResponse());

       // when then
       mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/reward/{rewardType}?page=0", "SAVE")
               .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(restDocs.document(
                       requestHeaders(
                               headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                       ),
                       pathParameters(
                               parameterWithName("rewardType").description((generateLinkCode(REWARD_TYPE)))
                       ),
                       responseFields(
                               fieldWithPath("totalReward").type(NUMBER).description("회원이 보유한 총 리워드 금액"),
                               fieldWithPath("rewardDtoList.content[0].amount").type(NUMBER).description("적립 혹은 사용 금액"),
                               fieldWithPath("rewardDtoList.content[0].text").type(STRING).description("적립 혹은 사용 내역"),
                               fieldWithPath("rewardDtoList.content[0].rewardType").type(STRING).description("적립(SAVE) 혹은 사용(USE) 상태"),
                               fieldWithPath("rewardDtoList.content[0].createdDate").type(STRING).description("적립 혹은 사용 날짜"),
                               fieldWithPath("rewardDtoList.pageable.sort.sorted").type(BOOLEAN).description("정렬 여부"),
                               fieldWithPath("rewardDtoList.pageable.sort.unsorted").type(BOOLEAN).description("정렬 안하는지 여부"),
                               fieldWithPath("rewardDtoList.pageable.sort.empty").type(BOOLEAN).description("정렬 조건이 아무것도 없는지의 여부"),
                               fieldWithPath("rewardDtoList.pageable.offset").type(NUMBER).description("현재 페이지"),
                               fieldWithPath("rewardDtoList.pageable.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                               fieldWithPath("rewardDtoList.pageable.pageSize").type(NUMBER).description("한 페이지에 보여줄 컨텐츠 크기"),
                               fieldWithPath("rewardDtoList.pageable.paged").type(BOOLEAN).description("페이징 되었는지 여부"),
                               fieldWithPath("rewardDtoList.pageable.unpaged").type(BOOLEAN).description("페이징 안되었는지 여부"),
                               fieldWithPath("rewardDtoList.empty").type(BOOLEAN).description("조회가 아무것도 되지 않았는지 여부"),
                               fieldWithPath("rewardDtoList.last").type(BOOLEAN).description("마지막 인지의 여부"),
                               fieldWithPath("rewardDtoList.number").type(NUMBER).description("페이지"),
                               fieldWithPath("rewardDtoList.size").type(NUMBER).description("한 페이지의 크기"),
                               fieldWithPath("rewardDtoList.numberOfElements").type(NUMBER).description("이 페이지에서 검색된 크기(size와 다른 개념)"),
                               fieldWithPath("rewardDtoList.first").type(BOOLEAN).description("첫 페이지인지의 여부"),
                               fieldWithPath("rewardDtoList.sort.sorted").type(BOOLEAN).description("정렬 여부"),
                               fieldWithPath("rewardDtoList.sort.unsorted").type(BOOLEAN).description("정렬 안하는지 여부"),
                               fieldWithPath("rewardDtoList.sort.empty").type(BOOLEAN).description("정렬 조건이 아무것도 없는지의 여부")
                       ))
               );
   }

    @DisplayName("내가 신청했던 클래스 목록 가져오기")
    @Test
    void get_my_lectures() throws Exception {

        // given
        when(myPageService.getMyLectures(any()))
                .thenReturn(createMyLectureResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/lectures")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("content[].orderId").type(NUMBER).description("주문 ID"),
                                fieldWithPath("content[].lectureId").type(NUMBER).description("클래스 ID"),
                                fieldWithPath("content[].lectureThumbnailUrl").type(STRING).description("클래스 썸네일 URL"),
                                fieldWithPath("content[].lectureTitle").type(STRING).description("클래스 제목"),
                                fieldWithPath("content[].scheduleId").type(NUMBER).description("스케줄 ID"),
                                fieldWithPath("content[].startDateTime").type(STRING).description("스케줄 시작 시간"),
                                fieldWithPath("content[].endDateTime").type(STRING).description("스케줄 종료 시간"),
                                fieldWithPath("content[].teamId").type(NUMBER).description("팀 ID").optional(),
                                fieldWithPath("content[].orderStatus").type(STRING).description(generateLinkCode(ORDER_STATUS)),
                                fieldWithPath("content[].leader").type(BOOLEAN).description("내가 그 팀의 리더인지 여부"),
                                fieldWithPath("content[].currentLectureStatus").type(STRING).description(generateLinkCode(CURRENT_LECTURE_STATUS)),
                                fieldWithPath("content[].reviewed").type(BOOLEAN).description("리뷰를 남겼는지 여부")
                        ).and(sliceDescriptor())
                ));
    }

    private Slice<MyLectureResponse> createMyLectureResponse() {
        List<MyLectureResponse> content = List.of(MyLectureResponse.builder()
                .orderId(1L)
                .lectureId(1L)
                .lectureThumbnailUrl("test")
                .lectureTitle("test")
                .scheduleId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(1))
                .teamId(1L)
                .orderStatus(OrderStatus.WAIT.name())
                .leader(true)
                .currentLectureStatus(DONE)
                .reviewed(true)
                .build());

        return RepositorySliceHelper.toSlice(content, PageRequest.of(0, 20));
    }

    @DisplayName("결제했던 클래스 정보 상세 조회")
    @Test
    void get_my_payment_lecture_detail() throws Exception {

        // given
        when(myPageService.getCompleteLecturePayment(any()))
                .thenReturn(createCompleteLecturePaymentResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/payment/{orderId}", 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("결제했던 주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("클래스 ID"),
                                fieldWithPath("lectureThumbnailUrl").type(STRING).description("클래스 썸네일 URL"),
                                fieldWithPath("lectureTitle").type(STRING).description("클래스 제목"),
                                fieldWithPath("lectureAddress").type(STRING).description("클래스 주소"),
                                fieldWithPath("scheduleId").type(NUMBER).description("스케줄 ID"),
                                fieldWithPath("startDateTime").type(STRING).description("스케줄 시작 시간"),
                                fieldWithPath("endDateTime").type(STRING).description("스케줄 종료 시간"),
                                fieldWithPath("paymentPrice").type(NUMBER).description("결제했던 금액")
                        )
                ));
    }

    private CompleteLecturePaymentResponse createCompleteLecturePaymentResponse() {
        return CompleteLecturePaymentResponse.builder()
                .lectureId(1L)
                .lectureThumbnailUrl("test")
                .lectureTitle("test")
                .lectureAddress("test")
                .scheduleId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(1))
                .paymentPrice(10000)
                .build();
    }

    @DisplayName("내가 신청했던 클래스 목록 가져오기 / 현재 팀의 결제 진행상황 보기")
    @Test
    void get_team_member_orders() throws Exception {

        // given
        when(myPageService.getTeamOrderStatus(any(), any()))
                .thenReturn(createTeamMemberPaymentStatusResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/lectures/payment/{scheduleId}/teams/{teamId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("scheduleId").description("신청한 스케쥴 ID"),
                                parameterWithName("teamId").description("신청한 팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberStatusList[].nickname").type(STRING).description("팀 멤버의 닉네임"),
                                fieldWithPath("memberStatusList[].orderState").type(STRING).description(generateLinkCode(ORDER_STATUS))
                        )
                ));
    }

    private TeamMemberPaymentStatusResponse createTeamMemberPaymentStatusResponse() {
        return TeamMemberPaymentStatusResponse.builder()
                .memberStatusList(List.of(
                        TeamMemberPaymentStatusResponse.MemberPaymentStatus.builder()
                                .nickname("test")
                                .orderState("test")
                                .build()
                ))
                .build();
    }

    @DisplayName("내가 작성한 리뷰 조회하기")
    @Test
    void get_my_reviews() throws Exception {

        // given
        when(myPageService.getMyReviews(any()))
                .thenReturn(createMyReviewResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/reviews")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("클래스 ID"),
                                fieldWithPath("content[].lectureTitle").type(STRING).description("클래스 제목"),
                                fieldWithPath("content[].reviewImageFileUrl").type(STRING).description("후기 사진 URL").optional(),
                                fieldWithPath("content[].text").type(STRING).description("리뷰 후기글"),
                                fieldWithPath("content[].replyText").type(STRING).description("디렉터의 리뷰 답글"),
                                fieldWithPath("content[].score").type(NUMBER).description("내가 남긴 평점")
                        ).and(pageDescriptor())
                ));
    }

    private Page<MyReviewResponse> createMyReviewResponse() {
        List<MyReviewResponse> content = List.of(MyReviewResponse.builder()
                .lectureId(1L)
                .lectureTitle("test")
                .reviewImageFileUrl("test")
                .text("test")
                .replyText("test")
                .score(5)
                .build());
        return new PageImpl<>(content, PageRequest.of(0, 10), 1);
    }

    @DisplayName("내가 갖고 있는 쿠폰 조회하기")
    @Test
    void get_my_coupons()throws Exception {

        // given
        when(myPageService.getMyAvailableCoupons())
                .thenReturn(createMyCouponResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/coupons")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("myCouponList[].memberCouponId").type(NUMBER).description("발급받은 쿠폰 ID"),
                                fieldWithPath("myCouponList[].title").type(STRING).description("쿠폰명"),
                                fieldWithPath("myCouponList[].discountPrice").type(NUMBER).description("할인 가격"),
                                fieldWithPath("myCouponList[].issueDate").type(STRING).description("발행 날짜"),
                                fieldWithPath("myCouponList[].closeDate").type(STRING).description("발행 만료 날짜")
                        )
                ));
    }

    private MyAvailableCouponResponse createMyCouponResponse() {
        return MyAvailableCouponResponse.builder()
                .myCouponList(Set.of(
                        MyAvailableCouponResponse.MyCoupon.builder()
                                .memberCouponId(1L)
                                .title("test")
                                .discountPrice(100)
                                .issueDate(LocalDateTime.now())
                                .closeDate(LocalDateTime.now().plusDays(30))
                                .build()
                ))
                .build();
    }

    @DisplayName("알림 조회")
    @Test
    void get_my_notification() throws Exception{

        // given
        when(myPageService.getMyNotifications(any()))
                .thenReturn(createMyNotifications());

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/notifications")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("content[].title").type(STRING).description("알림 제목"),
                                fieldWithPath("content[].message").type(STRING).description("알림 내용"),
                                fieldWithPath("content[].checked").type(BOOLEAN).description("읽었는지 여부"),
                                fieldWithPath("content[].notificationType").type(STRING).description(generateLinkCode(NOTIFICATION_TYPE)),
                                fieldWithPath("content[].uuid").type(STRING).description("고유 uuid"),
                                fieldWithPath("content[].teamId").type(NUMBER).description("알림을 받은 팀 ID").optional(),
                                fieldWithPath("content[].createdDate").type(STRING).description("알림 발신 날짜")
                        ).and(sliceDescriptor())
                ));

    }

    @DisplayName("나의 이벤트 알림 수신 여부 확인")
    @Test
    void get_my_event_alarm() throws Exception{
        //given
        AlarmStatusResponse alarmStatusResponse = AlarmStatusResponse.builder()
                .eventAlarm(true)
                .build();
        when(myPageService.getMyAlarmStatus()).thenReturn(alarmStatusResponse);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/alarm")
                .header(HttpHeaders.AUTHORIZATION,BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("eventAlarm").description("이벤트 알림 수신 동의 여부")
                        )
                ));
    }

    private Slice<NotificationResponse> createMyNotifications() {
        List<NotificationResponse> content = List.of(
                NotificationResponse.builder()
                        .title("test")
                        .message("test")
                        .checked(true)
                        .notificationType(NotificationType.ADMIN_CUSTOM)
                        .uuid("uuid")
                        .teamId(1L)
                        .createdDate(LocalDateTime.now())
                        .build()
        );
        return RepositorySliceHelper.toSlice(content, PageRequest.of(0, 20));
    }


    private RewardResponse createRewardResponse() {
        List<RewardDto> content = RewardDummy.createRewardDtoList(MemberDummy.createTestMember());
        Slice<RewardDto> rewardDtos = RepositorySliceHelper.toSlice(content, PageRequest.of(0, 20));
        return RewardResponse.of(rewardDtos,3000);
    }


}
