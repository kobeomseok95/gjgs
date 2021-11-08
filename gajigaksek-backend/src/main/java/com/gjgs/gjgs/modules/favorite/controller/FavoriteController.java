package com.gjgs.gjgs.modules.favorite.controller;

import com.gjgs.gjgs.modules.favorite.dto.FavoriteBulletinDtoResponse;
import com.gjgs.gjgs.modules.favorite.dto.LectureMemberDtoResponse;
import com.gjgs.gjgs.modules.favorite.dto.LectureTeamDtoResponse;
import com.gjgs.gjgs.modules.favorite.dto.MyTeamAndIsIncludeFavoriteLectureDtoResponse;
import com.gjgs.gjgs.modules.favorite.service.interfaces.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
//@PreAuthorize("hasAnyRole('USER,DIRECTOR')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 클래스 찜하기 클릭시 조회창에 필요한 정보
     *
     * @param lectureId   클래스 Id
     */
    @GetMapping("/my-teams/info/{lectureId}")
    public ResponseEntity<MyTeamAndIsIncludeFavoriteLectureDtoResponse> getMyTeamAndIsIncludeFavoriteLecture
                                                                    (@PathVariable Long lectureId) {

        return ResponseEntity.ok(MyTeamAndIsIncludeFavoriteLectureDtoResponse
                .of(favoriteService.getMyTeamAndIsIncludeFavoriteLecture(lectureId)));
    }


    /**
     * 개인 찜하기 - 클래스
     *
     * @param lectureId   찜할 lecture Id
     * @return
     */
    @PostMapping("/lectures/{lectureId}")
    public ResponseEntity<Void> saveMyFavoriteLecture(@PathVariable Long lectureId) {
        favoriteService.saveMyFavoriteLecture(lectureId);
        return ResponseEntity.ok().build();
    }


    /**
     * 개인 찜 클래스 목록 조회
     *
     */
    @GetMapping("/lectures")
    public ResponseEntity<LectureMemberDtoResponse> getMyFavoriteLectures() {
        return ResponseEntity.ok(LectureMemberDtoResponse.of(favoriteService.getMyFavoriteLectures()));
    }


    /**
     * 개인 찜 클래스 삭제
     *
     * @param lectureId   삭제할 찜 Id
     */
    @DeleteMapping("/lectures/{lectureId}")
    public ResponseEntity<Void> deleteMyFavoriteLecture(@PathVariable Long lectureId) {
        favoriteService.deleteMyFavoriteLecture(lectureId);
        return ResponseEntity.ok().build();
    }


    /**
     * 팀 클래스 찜하기
     *
     * @param teamId      팀 id
     * @param lectureId   찜할 클래스 Id
     * @return
     */
    @PostMapping("/teams/{teamId}/{lectureId}")
    public ResponseEntity<Void> saveTeamFavoriteLecture(@PathVariable("teamId") Long teamId,
                                                        @PathVariable("lectureId") Long lectureId) {
        favoriteService.saveTeamFavoriteLecture(teamId, lectureId);
        return ResponseEntity.ok().build();
    }


    /**
     * 팀 찜 조회
     *
     * @param teamId      팀 id
     * @return
     */
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<LectureTeamDtoResponse> getTeamFavoriteLectures(@PathVariable Long teamId) {
        return ResponseEntity.ok(LectureTeamDtoResponse.of(favoriteService.getTeamFavoriteLectures(teamId)));
    }


    /**
     * 팀 찜 삭제
     *
     * @param teamId      팀 Id
     * @param lectureId   찜 Id
     */
    @DeleteMapping("/teams/{teamId}/{lectureId}")
    public ResponseEntity<Void> deleteTeamFavoriteLecture(@PathVariable("teamId") Long teamId,
                                                          @PathVariable("lectureId") Long lectureId) {

        favoriteService.deleteTeamFavoriteLecture(teamId, lectureId);
        return ResponseEntity.ok().build();
    }



    /**
     * 개인 게시글 찜하기
     *
     * @param bulletinId  찜할 게시글 Id
     * @return
     */
    @PostMapping("/bulletins/{bulletinId}")
    public ResponseEntity<Void> saveMyFavoriteBulletin(@PathVariable Long bulletinId) {
        favoriteService.saveMyFavoriteBulletin(bulletinId);
        return ResponseEntity.ok().build();
    }


    /**
     * 내가 찜한 게시글 조회
     *
     */
    @GetMapping("/bulletins")
    public ResponseEntity<FavoriteBulletinDtoResponse> getMyFavoriteBulletins() {
        return ResponseEntity.ok(FavoriteBulletinDtoResponse.of(favoriteService.getMyFavoriteBulletins()));
    }


    /**
     * 내가 찜한 게시글 삭제
     *
     * @param bulletinId  찜 아이디
     * @return
     */
    @DeleteMapping("/bulletins/{bulletinId}")
    public ResponseEntity<Void> deleteMyFavoriteBulletin(@PathVariable Long bulletinId) {
        favoriteService.deleteMyFavoriteBulletin(bulletinId);
        return ResponseEntity.ok().build();
    }


}
