package com.gjgs.gjgs.modules.member.controller;

import com.gjgs.gjgs.modules.member.dto.myinfo.*;
import com.gjgs.gjgs.modules.member.dto.mypage.DirectorMyPageResponse;
import com.gjgs.gjgs.modules.member.service.mypage.interfaces.MyInfoEditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MyInfoEditController {

    private final MyInfoEditService myInfoEditService;


    /**
     * mypage 편집창
     *
     * @return 내 상세 정보
     */
    @GetMapping("/edit")
    public ResponseEntity<MyPageEditResponse> editMyPage() {
        return ResponseEntity.ok(myInfoEditService.editMyPage());
    }

    /**
     * nickname 수정
     *
     * @param nicknameModifyRequest 닉네임 수정 폼
     */
    @PutMapping("/nickname")
    public ResponseEntity<Void> editNickname(@RequestBody @Valid NicknameModifyRequest nicknameModifyRequest) {
        myInfoEditService.editNickname(nicknameModifyRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 전화번호 수정
     *
     * @param phoneModifyRequest   전화번호 수정 폼
     */
    @PutMapping("/phone")
    public ResponseEntity<Void> editPhone(@RequestBody @Valid PhoneModifyRequest phoneModifyRequest) {
        myInfoEditService.editPhone(phoneModifyRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 카테고리 수정
     *
     * @param categoryModifyRequest 카테고리 Id List
     * @return
     */
    @PutMapping("/category")
    public ResponseEntity<Void> editCategory(@RequestBody @Valid CategoryModifyRequest categoryModifyRequest) {
        myInfoEditService.editCategory(categoryModifyRequest.getCategoryIdList());
        return ResponseEntity.ok().build();
    }

    /**
     * 자기소개 수정
     *
     * @param profileTextModifyRequest 자기소개 수정 폼
     */
    @PutMapping("/profile-text")
    public ResponseEntity<Void> editProfileText(@RequestBody @Valid ProfileTextModifyRequest profileTextModifyRequest) {
        myInfoEditService.editProfileText(profileTextModifyRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 이미지 수정
     *
     * @param file
     */
    @PostMapping("/image")
    public ResponseEntity<Void> editImage(@RequestPart MultipartFile file) {
        myInfoEditService.editImage(file);
        return ResponseEntity.ok().build();
    }


    @PutMapping("zone")
    public ResponseEntity<Void> editZone(@RequestBody @Valid ZoneModifyRequest zoneModifyRequest) {
        myInfoEditService.editZone(zoneModifyRequest.getZoneId());
        return ResponseEntity.ok().build();
    }



    /**
     * 디렉터 편집 페이지
     *
     */
    @PreAuthorize("hasAnyRole('ADMIN,DIRECTOR')")
    @GetMapping("/directors/edit")
    public ResponseEntity<DirectorMyPageResponse> editDirector() {
        return ResponseEntity.ok(myInfoEditService.getDirectorEditPage());
    }


    /**
     * 디렉터 소개 수정
     *
     * @param directorTextModifyRequest 디렉터 소개 수정 폼
     */
    @PreAuthorize("hasAnyRole('ADMIN,DIRECTOR')")
    @PutMapping("/directors/director-text")
    public ResponseEntity<Void> editDirectorText(@RequestBody @Valid DirectorTextModifyRequest directorTextModifyRequest) {
        myInfoEditService.editDirectorText(directorTextModifyRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/alarm")
    public ResponseEntity<Void> editMyEventAlarm(@RequestBody AlarmEditRequest alarmEditRequest){
        myInfoEditService.editMyEventAlarm(alarmEditRequest);
        return ResponseEntity.ok().build();
    }

}
