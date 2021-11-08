package com.gjgs.gjgs.modules.bulletin.services;

import com.gjgs.gjgs.modules.bulletin.dto.BulletinChangeRecruitResponse;
import com.gjgs.gjgs.modules.bulletin.dto.BulletinDetailResponse;
import com.gjgs.gjgs.modules.bulletin.dto.BulletinIdResponse;
import com.gjgs.gjgs.modules.bulletin.dto.CreateBulletinRequest;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchCondition;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchResponse;
import com.gjgs.gjgs.modules.bulletin.entity.Bulletin;
import com.gjgs.gjgs.modules.bulletin.exceptions.BulletinErrorCodes;
import com.gjgs.gjgs.modules.bulletin.exceptions.BulletinNotFoundException;
import com.gjgs.gjgs.modules.bulletin.repositories.BulletinRepository;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureRepository;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamNotFoundException;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamRepository;
import com.gjgs.gjgs.modules.utils.aop.LoginMemberFavoriteBulletin;
import com.gjgs.gjgs.modules.utils.aop.LoginMemberFavoriteLecture;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gjgs.gjgs.modules.lecture.enums.LectureStatus.ACCEPT;

@Service
@Transactional
@RequiredArgsConstructor
public class BulletinServiceImpl implements BulletinService {

    private final TeamRepository teamRepository;
    private final LectureRepository lectureRepository;
    private final BulletinRepository bulletinRepository;
    private final SecurityUtil securityUtil;

    @Override
    public BulletinIdResponse createBulletin(CreateBulletinRequest request) {
        Optional<Bulletin> teamHasBulletin = bulletinRepository.findWithTeamByTeamId(request.getTeamId());
        if (teamHasBulletin.isPresent()) {
            return modifyBulletin(teamHasBulletin.get().getId(), request);
        }

        Bulletin bulletin = Bulletin.of(request);
        Long lectureId = relateLecture(request.getLectureId(), bulletin);
        Long teamId = relateTeam(request.getTeamId(), bulletin);

        Bulletin savedBulletin = bulletinRepository.save(bulletin);
        return BulletinIdResponse.of(savedBulletin.getId(), teamId, lectureId);
    }

    private Long relateTeam(Long teamId, Bulletin bulletin) {
        Team relateTeam = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(TeamErrorCodes.TEAM_NOT_FOUND));
        relateTeam.checkTeamIsFullDoThrow();
        bulletin.setTeam(relateTeam);
        return relateTeam.getId();
    }

    @Override
    public BulletinIdResponse deleteBulletin(Long bulletinId) {
        Bulletin deleteBulletin = bulletinRepository.findById(bulletinId).orElseThrow(() -> new BulletinNotFoundException(BulletinErrorCodes.BULLETIN_NOT_FOUND));

        deleteBulletin.stopRecruit();
        return BulletinIdResponse.of(bulletinId);
    }

    @Override
    public BulletinIdResponse modifyBulletin(Long bulletinId, CreateBulletinRequest request) {
        Bulletin findBulletin = bulletinRepository.findWithLectureTeam(bulletinId)
                .orElseThrow(() -> new BulletinNotFoundException(BulletinErrorCodes.BULLETIN_NOT_FOUND));

        Long modifiedLectureId = modifyLecture(findBulletin, request);
        findBulletin.modify(request);
        return BulletinIdResponse.of(findBulletin.getId(), modifiedLectureId);
    }

    private Long modifyLecture(Bulletin findBulletin, CreateBulletinRequest request) {
        if (findBulletin.isDifferenceLecture(request.getLectureId())) {
            return relateLecture(request.getLectureId(), findBulletin);
        }
        return findBulletin.getLectureId();
    }

    private Long relateLecture(Long lectureId, Bulletin bulletin) {
        if (!lectureRepository.existsLectureByIdAndLectureStatus(lectureId, ACCEPT)) {
            throw new LectureException(LectureErrorCodes.LECTURE_NOT_FOUND);
        }

        Lecture lecture = Lecture.of(lectureId);
        bulletin.setLecture(lecture);
        return lecture.getId();
    }

    @LoginMemberFavoriteBulletin
    @Override
    @Transactional(readOnly = true)
    public Page<BulletinSearchResponse> getBulletins(Pageable pageable, BulletinSearchCondition condition) {
        return bulletinRepository.searchBulletin(pageable, condition);
    }

    @LoginMemberFavoriteLecture
    @Override
    @Transactional(readOnly = true)
    public BulletinDetailResponse getBulletinDetails(Long bulletinId) {
        return bulletinRepository.findBulletinDetail(bulletinId);
    }

    @Override
    public BulletinChangeRecruitResponse changeRecruitStatus(Long bulletinId) {
        String username = securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
        Bulletin bulletin = bulletinRepository.findWithTeamLeaderByBulletinIdLeaderUsername(bulletinId, username)
                .orElseThrow(() -> new BulletinNotFoundException(BulletinErrorCodes.BULLETIN_NOT_FOUND_OR_NOT_LEADER));
        bulletin.changeRecruitStatus();
        return BulletinChangeRecruitResponse.of(bulletin.getId(), bulletin.isStatus());
    }
}
