package com.gjgs.gjgs.modules.lecture.services.temporaryStore.put;

import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLectureStep;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PutLectureServiceFactory {

    private final List<PutLectureService> lectureServiceList;

    public PutLectureService getService(CreateLectureStep step) throws Exception {
        return lectureServiceList.stream()
                .filter(service -> service.getCreateLectureStep().equals(step))
                .findFirst()
                .orElseThrow(() -> new LectureException(LectureErrorCodes.INVALID_TEMPORARY_STORE_STEP));
    }
}
