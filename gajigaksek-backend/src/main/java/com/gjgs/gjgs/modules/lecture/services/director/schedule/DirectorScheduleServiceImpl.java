package com.gjgs.gjgs.modules.lecture.services.director.schedule;

import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLecture;
import com.gjgs.gjgs.modules.lecture.dtos.director.schedule.DirectorScheduleResponse;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.entity.Schedule;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureQueryRepository;
import com.gjgs.gjgs.modules.lecture.repositories.schedule.ScheduleRepository;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectorScheduleServiceImpl implements DirectorScheduleService{

    private final SecurityUtil securityUtil;
    private final ScheduleRepository scheduleRepository;
    private final LectureQueryRepository lectureQueryRepository;

    @Override
    public DirectorScheduleResponse.PostDelete createSchedule(Long lectureId, CreateLecture.ScheduleDto scheduleDto) {
        Lecture lecture = getDirectorsLecture(lectureId);

        scheduleDto.setEndTimes();
        Schedule schedule = scheduleRepository.save(Schedule.of(scheduleDto, lecture));
        lecture.addSchedule(schedule);
        return DirectorScheduleResponse.PostDelete.post(schedule.getId());
    }

    @Override
    public DirectorScheduleResponse.PostDelete deleteSchedule(Long lectureId, Long scheduleId) {
        Lecture lecture = getDirectorsLecture(lectureId);

        Schedule schedule = lecture.getSchedule(scheduleId);
        schedule.canDelete();
        lecture.removeSchedule(schedule);
        scheduleRepository.delete(schedule);
        return DirectorScheduleResponse.PostDelete.delete(scheduleId);
    }

    private Lecture getDirectorsLecture(Long lectureId) {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        return lectureQueryRepository.findAcceptLectureWithSchedulesByIdUsername(lectureId, username).orElseThrow(() -> new LectureException(LectureErrorCodes.INVALID_LECTURE));
    }

    @Override
    public DirectorScheduleResponse getLectureSchedules(Long lectureId) {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        return lectureQueryRepository.findSchedulesByLectureIdUsername(lectureId, username).orElseThrow(() -> new LectureException(LectureErrorCodes.DIRECTOR_HAVE_NOT_LECTURE));
    }
}
