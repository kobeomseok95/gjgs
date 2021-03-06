package com.gjgs.gjgs.modules.favorite.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.bulletin.enums.Age;
import com.gjgs.gjgs.modules.favorite.dto.FavoriteBulletinDto;
import com.gjgs.gjgs.modules.favorite.dto.LectureMemberDto;
import com.gjgs.gjgs.modules.favorite.dto.LectureTeamDto;
import com.gjgs.gjgs.modules.favorite.dto.MyTeamAndIsIncludeFavoriteLectureDto;
import com.gjgs.gjgs.modules.favorite.service.interfaces.FavoriteService;
import com.gjgs.gjgs.modules.lecture.embedded.Price;
import com.gjgs.gjgs.modules.team.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {
                FavoriteController.class
        }
)
class FavoriteControllerTest extends RestDocsTestSupport {

    @MockBean FavoriteService favoriteService;

    final String TOKEN = "Bearer AccessToken";
    final String URL = "/api/v1/favorites";

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @DisplayName("????????? ????????? ????????? ???????????? ????????? ??????")
    @Test
    void get_my_favorite_lecture_and_team() throws Exception {
        // given
        Team team = Team.builder()
                .id(1L)
                .teamName("test")
                .build();

        MyTeamAndIsIncludeFavoriteLectureDto dto
                = MyTeamAndIsIncludeFavoriteLectureDto.of(team,true);

        when(favoriteService.getMyTeamAndIsIncludeFavoriteLecture(any())).thenReturn(Arrays.asList(dto));


        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/my-teams/info/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("myTeamAndIsIncludeFavoriteLectureDtoList[0].teamId").value(team.getId().intValue()))
                .andExpect(jsonPath("myTeamAndIsIncludeFavoriteLectureDtoList[0].teamName").value(team.getTeamName()))
                .andExpect(jsonPath("myTeamAndIsIncludeFavoriteLectureDtoList[0].include").value(dto.isInclude()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("??? ??? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("myTeamAndIsIncludeFavoriteLectureDtoList[].teamId").type(NUMBER).description("????????? ??? ID"),
                                fieldWithPath("myTeamAndIsIncludeFavoriteLectureDtoList[].teamName").type(STRING).description("????????? ??? ???"),
                                fieldWithPath("myTeamAndIsIncludeFavoriteLectureDtoList[].include").type(BOOLEAN).description("?????? ?????? ????????? ??? ??????")
                        )
                ))
        ;
    }

    @DisplayName("?????? ????????? ???")
    @Test
    void add_my_favorite_lecture() throws Exception {

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/lectures/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("?????? ??? ??? ????????? ID")
                        )
                ))
        ;
    }

    @DisplayName("?????? ??? ????????? ?????? ??????")
    @Test
    void get_my_favorite_lectures() throws Exception {
        // given
        LectureMemberDto dto = LectureMemberDto.builder()
                .lectureMemberId(1L)
                .lectureId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();
        when(favoriteService.getMyFavoriteLectures()).thenReturn(Arrays.asList(dto));

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/lectures")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("lectureMemberDtoList[0].lectureMemberId").value(dto.getLectureMemberId()))
                .andExpect(jsonPath("lectureMemberDtoList[0].lectureId").value(dto.getLectureId()))
                .andExpect(jsonPath("lectureMemberDtoList[0].thumbnailImageFileUrl").value(dto.getThumbnailImageFileUrl()))
                .andExpect(jsonPath("lectureMemberDtoList[0].zoneId").value(dto.getZoneId()))
                .andExpect(jsonPath("lectureMemberDtoList[0].price.priceOne").value(dto.getPrice().getPriceOne()))
                .andExpect(jsonPath("lectureMemberDtoList[0].price.priceTwo").value(dto.getPrice().getPriceTwo()))
                .andExpect(jsonPath("lectureMemberDtoList[0].price.priceThree").value(dto.getPrice().getPriceThree()))
                .andExpect(jsonPath("lectureMemberDtoList[0].price.priceFour").value(dto.getPrice().getPriceFour()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("lectureMemberDtoList[].lectureMemberId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("lectureMemberDtoList[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("lectureMemberDtoList[].thumbnailImageFileUrl").type(STRING).description("?????????"),
                                fieldWithPath("lectureMemberDtoList[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("lectureMemberDtoList[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("lectureMemberDtoList[].price.regularPrice").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("lectureMemberDtoList[].price.priceOne").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureMemberDtoList[].price.priceTwo").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureMemberDtoList[].price.priceThree").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureMemberDtoList[].price.priceFour").type(NUMBER).description("?????? ?????? ??? ??????"))
                        )
                )
        ;
    }

    @DisplayName("?????? ??? ??????")
    @Test
    void delete_my_favorite_lecture() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/lectures/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("??? ???????????? ????????? ????????? ID")
                        )
                ))
        ;
    }

    @DisplayName("??? ????????? ?????????")
    @Test
    void add_team_favorite_lecture() throws Exception {

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/teams/{teamId}/{lectureId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("?????? ?????? ID"),
                                parameterWithName("lectureId").description("?????? ???????????? ID")
                        )
                ))
        ;
    }

    @DisplayName("??? ??? ????????????")
    @Test
    void get_team_favorite_lectures() throws Exception {
        // given
        LectureTeamDto dto = LectureTeamDto.builder()
                .lectureTeamId(1L)
                .lectureId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();
        when(favoriteService.getTeamFavoriteLectures(any())).thenReturn(Arrays.asList(dto));

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/teams/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("lectureTeamDtoList[0].lectureTeamId").value(dto.getLectureTeamId()))
                .andExpect(jsonPath("lectureTeamDtoList[0].lectureId").value(dto.getLectureId()))
                .andExpect(jsonPath("lectureTeamDtoList[0].thumbnailImageFileUrl").value(dto.getThumbnailImageFileUrl()))
                .andExpect(jsonPath("lectureTeamDtoList[0].zoneId").value(dto.getZoneId()))
                .andExpect(jsonPath("lectureTeamDtoList[0].price.priceOne").value(dto.getPrice().getPriceOne()))
                .andExpect(jsonPath("lectureTeamDtoList[0].price.priceTwo").value(dto.getPrice().getPriceTwo()))
                .andExpect(jsonPath("lectureTeamDtoList[0].price.priceThree").value(dto.getPrice().getPriceThree()))
                .andExpect(jsonPath("lectureTeamDtoList[0].price.priceFour").value(dto.getPrice().getPriceFour()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ?????? ???????????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("lectureTeamDtoList[].lectureTeamId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("lectureTeamDtoList[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("lectureTeamDtoList[].thumbnailImageFileUrl").type(STRING).description("?????????"),
                                fieldWithPath("lectureTeamDtoList[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("lectureTeamDtoList[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("lectureTeamDtoList[].price.regularPrice").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("lectureTeamDtoList[].price.priceOne").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureTeamDtoList[].price.priceTwo").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureTeamDtoList[].price.priceThree").type(NUMBER).description("?????? ?????? ??? ??????"),
                                fieldWithPath("lectureTeamDtoList[].price.priceFour").type(NUMBER).description("?????? ?????? ??? ??????"))
                        )
                );
        ;
    }

    @DisplayName("??? ??? ??????")
    @Test
    void delete_team_favorite_lecture() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/teams/{teamId}/{lectureId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("??? ???????????? ????????? ?????? ID"),
                                parameterWithName("lectureId").description("??? ???????????? ????????? ???????????? ID")
                        )
                ));
    }

    @DisplayName("?????? ????????? ?????????")
    @Test
    void add_my_favorite_bulletin() throws Exception {

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/bulletins/{bulletinId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("?????? ???????????? ID")
                        )
                ));
        ;
    }

    @DisplayName("?????? ?????? ????????? ??????")
    @Test
    void get_my_favorite_bulletins() throws Exception {
        // given
        FavoriteBulletinDto dto = FavoriteBulletinDto.builder()
                .bulletinId(1L)
                .bulletinMemberId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .age(Age.THIRTY_TO_THIRTYFIVE)
                .timeType("AFTERNOON")
                .currentPeople(3)
                .maxPeople(5)
                .build();

        when(favoriteService.getMyFavoriteBulletins()).thenReturn(Arrays.asList(dto));

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("favoriteBulletinDtoList[0].bulletinId").value(dto.getBulletinId()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].bulletinMemberId").value(dto.getBulletinId()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].thumbnailImageFileUrl").value(dto.getThumbnailImageFileUrl()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].zoneId").value(dto.getZoneId()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].title").value(dto.getTitle()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].timeType").value(dto.getTimeType()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].currentPeople").value(dto.getCurrentPeople()))
                .andExpect(jsonPath("favoriteBulletinDtoList[0].maxPeople").value(dto.getMaxPeople()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("favoriteBulletinDtoList[].bulletinId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("favoriteBulletinDtoList[].bulletinMemberId").type(NUMBER).description("????????? ?????? ???????????? ID"),
                                fieldWithPath("favoriteBulletinDtoList[].thumbnailImageFileUrl").type(STRING).description("?????????"),
                                fieldWithPath("favoriteBulletinDtoList[].zoneId").type(NUMBER).description("??????"),
                                fieldWithPath("favoriteBulletinDtoList[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("favoriteBulletinDtoList[].timeType").type(STRING).description("???????????? ???????????? ?????? ?????????"),
                                fieldWithPath("favoriteBulletinDtoList[].currentPeople").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("favoriteBulletinDtoList[].maxPeople").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("favoriteBulletinDtoList[].age").type(STRING).description("???????????? ?????????")
                        )
                ))
        ;
    }

    @DisplayName("?????? ?????? ????????? ??????")
    @Test
    void delete_my_favorite_bulletin() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/bulletins/{bulletinId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("????????? ?????? ????????? ID")
                        )
                ))
        ;
    }
}
