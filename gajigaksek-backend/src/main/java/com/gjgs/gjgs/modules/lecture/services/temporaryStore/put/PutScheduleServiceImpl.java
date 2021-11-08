package com.gjgs.gjgs.modules.lecture.services.temporaryStore.put;

import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLecture;
import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLectureProcessResponse;
import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLectureStep;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureJdbcRepository;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureQueryRepository;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PutScheduleServiceImpl implements PutLectureService {

    private final SecurityUtil securityUtil;
    private final LectureQueryRepository lectureQueryRepository;
    private final LectureJdbcRepository lectureJdbcRepository;

    @Override
    public CreateLectureStep getCreateLectureStep() {
        return CreateLectureStep.SCHEDULE;
    }

    @Override
    public CreateLectureProcessResponse putLectureProcess(CreateLecture request, List<MultipartFile> files) {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        Lecture lecture = lectureQueryRepository.findWithSchedulesByDirectorUsername(username).orElseThrow(() -> new LectureException(LectureErrorCodes.TEMPORARY_NOT_SAVE_LECTURE));
        lecture.putSchedule((CreateLecture.ScheduleRequest) request);
        saveSchedules(lecture);
        return CreateLectureProcessResponse.completeSchedule(lecture.getId());
    }

    private void saveSchedules(Lecture lecture) {
        if (!lecture.getScheduleList().isEmpty()) {
            lectureQueryRepository.deleteSchedulesByLectureId(lecture.getId());
        }
        lectureJdbcRepository.insertSchedule(lecture);
    }
}
