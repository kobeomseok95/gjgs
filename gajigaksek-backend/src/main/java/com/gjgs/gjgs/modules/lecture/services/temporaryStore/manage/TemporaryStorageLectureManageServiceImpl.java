package com.gjgs.gjgs.modules.lecture.services.temporaryStore.manage;

import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLecture;
import com.gjgs.gjgs.modules.lecture.dtos.create.PutLectureResponse;
import com.gjgs.gjgs.modules.lecture.dtos.create.TemporaryStorageLectureManageResponse;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureQueryRepository;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureRepository;
import com.gjgs.gjgs.modules.lecture.repositories.temporaryStore.TemporaryStoredLectureQueryRepository;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.utils.s3.FilePaths;
import com.gjgs.gjgs.modules.utils.s3.interfaces.AmazonS3Service;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TemporaryStorageLectureManageServiceImpl implements TemporaryStorageLectureManageService {

    private final FilePaths PATH = FilePaths.LECTURE_IMAGE_PATH;

    private final LectureRepository lectureRepository;
    private final TemporaryStoredLectureQueryRepository tempStoreQueryRepository;
    private final LectureQueryRepository lectureQueryRepository;
    private final SecurityUtil securityUtil;
    private final AmazonS3Service s3Service;

    @Override
    public TemporaryStorageLectureManageResponse saveLecture(CreateLecture.TermsRequest request) {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        Lecture lecture = lectureRepository.findCreatingLectureByDirectorUsername(username).orElseThrow(() -> new LectureException(LectureErrorCodes.TEMPORARY_NOT_SAVE_LECTURE));
        lecture.putTermsChangeStatus(request);
        return TemporaryStorageLectureManageResponse.save(lecture.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PutLectureResponse getTemporaryStoredLecture() {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        return tempStoreQueryRepository.findByDirectorUsername(username).orElseThrow(() -> new LectureException(LectureErrorCodes.TEMPORARY_NOT_SAVE_LECTURE));
    }

    @Override
    public TemporaryStorageLectureManageResponse deleteTemporaryStorageLecture() {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_AUTHORITY_NOT_FOUND));
        PutLectureResponse response = tempStoreQueryRepository.findByDirectorUsername(username).orElseThrow(() -> new LectureException(LectureErrorCodes.LECTURE_NOT_FOUND));
        deleteCreatingLectureProcess(response);
        return TemporaryStorageLectureManageResponse.delete();
    }

    private void deleteCreatingLectureProcess(PutLectureResponse response) {
        deleteFiles(response);
        Long lectureId = response.getLectureId();
        lectureQueryRepository.deleteSchedulesByLectureId(lectureId);
        lectureQueryRepository.deleteCurriculumsByLectureId(lectureId);
        lectureQueryRepository.deleteFinishedProductsByLectureId(lectureId);
        lectureRepository.deleteById(lectureId);
    }

    private void deleteFiles(PutLectureResponse response) {
        List<String> allFilenames = response.getAllFilenames();
        allFilenames.forEach(filename ->
                s3Service.delete(PATH.getPath(), filename));
    }
}
