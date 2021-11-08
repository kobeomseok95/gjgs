package com.gjgs.gjgs.modules.member.service.mypage.interfaces;

import com.gjgs.gjgs.modules.member.dto.myinfo.*;
import com.gjgs.gjgs.modules.member.dto.mypage.DirectorMyPageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyInfoEditService {

    MyPageEditResponse editMyPage();

    void editNickname(NicknameModifyRequest nickname);

    void editPhone(PhoneModifyRequest phoneModifyRequest);

    void editCategory(List<Long> categoryIdList);

    void editProfileText(ProfileTextModifyRequest profileText);

    void editImage(MultipartFile file);

    void editZone(Long zoneId);

    DirectorMyPageResponse getDirectorEditPage();

    void editDirectorText(DirectorTextModifyRequest directorText);

    void editMyEventAlarm(AlarmEditRequest alarmEditRequest);

}
