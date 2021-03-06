package com.gjgs.gjgs.modules.notification.service.impl;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberQueryRepository;
import com.gjgs.gjgs.modules.notification.dto.MemberFcmDto;
import com.gjgs.gjgs.modules.notification.dto.NotificationCreateRequest;
import com.gjgs.gjgs.modules.notification.dto.NotificationIncludeFcmToken;
import com.gjgs.gjgs.modules.notification.entity.Notification;
import com.gjgs.gjgs.modules.notification.enums.NotificationType;
import com.gjgs.gjgs.modules.notification.exception.NotificationErrorCodes;
import com.gjgs.gjgs.modules.notification.exception.NotificationException;
import com.gjgs.gjgs.modules.notification.repository.interfaces.NotificationJdbcRepository;
import com.gjgs.gjgs.modules.notification.repository.interfaces.NotificationQueryRepository;
import com.gjgs.gjgs.modules.notification.service.interfaces.NotificationService;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final MemberQueryRepository memberQueryRepository;
    private final NotificationJdbcRepository notificationJdbcRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final SecurityUtil securityUtil;

    // Firebase Admin SDK ?????????????????? FCM HTTP v1 API??? ???????????? ???????????????.
    // HTTP v1 ?????? ????????? ?????? ?????? ???????????? ???????????? ?????? ??? ????????? ?????? OAuth 2.0 ????????? ????????? ???????????????.
    // Admin SDK??? ???????????? ???????????? ????????? ?????? ????????????????????? ????????? ???????????? ???????????????.

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    // https://developers.google.com/identity/protocols/oauth2/scopes#fcm
    @Value("${fcm.key.scope}")
    private String fireBaseScope;

    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of(fireBaseScope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void sendCustomNotification(NotificationCreateRequest notificationCreateRequest) {
        List<MemberFcmDto> memberFcmDto = memberQueryRepository
                .findMemberFcmDtoByTargetTypeAndMemberIdListAndMemberEventAlarm(notificationCreateRequest.getTargetType(), notificationCreateRequest.getMemberIdList(),true);
        List<NotificationIncludeFcmToken> notificationIncludeFcmToken
                                        = NotificationIncludeFcmToken.createCustomType(notificationCreateRequest, memberFcmDto);

        saveNotification(notificationIncludeFcmToken);

        List<NotificationIncludeFcmToken> NotificationOnlyIncludeFcmToken = notificationIncludeFcmToken.stream()
                .filter(dto -> dto.getFcmToken() != null && !dto.getFcmToken().isBlank()).collect(Collectors.toList());
        if (!NotificationOnlyIncludeFcmToken.isEmpty()){
            sendMessageList(NotificationOnlyIncludeFcmToken);
        }
    }


    @Override
    public void readNotification(String uuid) {
        notificationQueryRepository.findNotificationByUuidAndUsername(uuid, getUsernameOrThrow())
                .orElseThrow(() -> new NotificationException(NotificationErrorCodes.NOT_EXIST_NOTIFICATION))
                .changeCheckStatus(true);
    }

    private String getUsernameOrThrow() {
        return securityUtil.getCurrentUsername()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }

    @Override
    public void sendMatchingNotification(List<Member> memberList, Long teamId, NotificationType notificationType) {
        List<NotificationIncludeFcmToken> notificationIncludeFcmToken
                                            = NotificationIncludeFcmToken.createMatchingCompleteType(memberList, teamId);
        saveNotification(notificationIncludeFcmToken);
        List<NotificationIncludeFcmToken> NotificationOnlyIncludeFcmToken = notificationIncludeFcmToken.stream()
                .filter(dto -> dto.getFcmToken() != null && !dto.getFcmToken().isBlank()).collect(Collectors.toList());

        if (!NotificationOnlyIncludeFcmToken.isEmpty()){
            sendMessageList(NotificationOnlyIncludeFcmToken);
        }
    }

    @Override
    public void sendApplyNotification(Team team) {
        List<NotificationIncludeFcmToken> notificationIncludeFcmToken = NotificationIncludeFcmToken.createTeamApplyType(team);
        saveNotification(notificationIncludeFcmToken);
        List<NotificationIncludeFcmToken> NotificationOnlyIncludeFcmToken = notificationIncludeFcmToken.stream()
                .filter(dto -> dto.getFcmToken() != null && !dto.getFcmToken().isBlank()).collect(Collectors.toList());

        if (!NotificationOnlyIncludeFcmToken.isEmpty()){
            sendMessageList(NotificationOnlyIncludeFcmToken);
        }
    }

    private void saveNotification(List<NotificationIncludeFcmToken> notificationIncludeFcmToken) {
        notificationJdbcRepository.insertNotification(Notification.of(notificationIncludeFcmToken));
    }

    /**
     * message send??? ????????? ?????? 500??? ??????
     * ????????? ????????? ?????? ????????? response??? ???????????? ????????? ??????.
     */
    private void sendMessageList(List<NotificationIncludeFcmToken> NotificationOnlyIncludeFcmToken) {
        List<Message> messageList = createMessages(NotificationOnlyIncludeFcmToken);
        List<String> fcmTokenList = NotificationOnlyIncludeFcmToken.stream().map(dto -> dto.getFcmToken()).collect(Collectors.toList());
        List<List<String>> fcmPartition = Lists.partition(fcmTokenList, 500);
        List<List<Message>> messagesPartition = Lists.partition(messageList, 500);

        for(int i=0;i<fcmPartition.size();i++){
            BatchResponse response;
            try {
                response = FirebaseMessaging.getInstance().sendAll(messagesPartition.get(i));
                if (response.getFailureCount() > 0) {
                    List<SendResponse> responses = response.getResponses();
                    List<String> failedTokens = new ArrayList<>();
                    for (int j = 0; j < responses.size(); j++) {
                        if (!responses.get(j).isSuccessful()) {
                            failedTokens.add(fcmPartition.get(i).get(j));
                        }
                    }
                    log.error("List of tokens are not valid FCM token : " + failedTokens);
                }
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to memberList push message. error info : {}", e.getMessage());
            }
        }
    }

    private List<Message> createMessages(List<NotificationIncludeFcmToken> notificationIncludeFcmTokenList) {
        final String finalTeamId = getTeamId(notificationIncludeFcmTokenList);
        return notificationIncludeFcmTokenList.stream().map(notification -> Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .putData("notificationType",notification.getNotificationType().name())
                .putData("uuid",notification.getUuid())
                .putData("teamId",finalTeamId)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getMessage())
                        .build())
                .setToken(notification.getFcmToken())
                .build()).collect(Collectors.toList());
    }

    private String getTeamId(List<NotificationIncludeFcmToken> notificationIncludeFcmTokenList) {
        String teamId = "";
        if (notificationIncludeFcmTokenList.get(0).getTeamId() != null){
            teamId = notificationIncludeFcmTokenList.get(0).getTeamId().toString();
        }
        return teamId;
    }
}
