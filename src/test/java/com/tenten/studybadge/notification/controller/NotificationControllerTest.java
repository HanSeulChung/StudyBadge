package com.tenten.studybadge.notification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tenten.studybadge.common.jwt.JwtTokenProvider;
import com.tenten.studybadge.common.oauth2.CustomOAuth2UserService;
import com.tenten.studybadge.common.oauth2.OAuth2FailureHandler;
import com.tenten.studybadge.common.oauth2.OAuth2SuccessHandler;
import com.tenten.studybadge.common.security.CustomAuthenticationEntryPoint;
import com.tenten.studybadge.common.security.CustomUserDetails;
import com.tenten.studybadge.common.security.LoginUserArgumentResolver;
import com.tenten.studybadge.member.domain.entity.Member;
import com.tenten.studybadge.member.domain.type.MemberRole;
import com.tenten.studybadge.notification.domain.entitiy.Notification;
import com.tenten.studybadge.notification.dto.NotificationResponse;
import com.tenten.studybadge.notification.service.NotificationService;
import com.tenten.studybadge.type.notification.NotificationType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(NotificationController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private RedisTemplate<?, ?> redisTemplate;
    @MockBean
    private CustomOAuth2UserService oAuth2UserService;
    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    @MockBean
    private OAuth2FailureHandler oAuth2FailureHandler;
    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;
    @MockBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomUserDetails customUserDetails;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private Notification notificationScheduleCreate;
    private Notification notificationScheduleDelete;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.
            standaloneSetup(new NotificationController(notificationService))
            .setCustomArgumentResolvers(loginUserArgumentResolver)
            .build();

        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long memberId = 1L;
        notificationScheduleCreate =  Notification.builder()
            .content("일정 생성 알림")
            .url("관련 url")
            .isRead(false)
            .notificationType(NotificationType.SCHEDULE_CREATE)
            .receiver(Member.builder()
                .id(memberId)
                .role(MemberRole.USER)
                .build())
            .build();

        notificationScheduleDelete = Notification.builder()
            .content("일정 삭제 알림")
            .url("관련 url")
            .isRead(false)
            .notificationType(NotificationType.SCHEDULE_DELETE)
            .receiver(Member.builder()
                .id(memberId)
                .role(MemberRole.USER)
                .build())
            .build();
    }

    @Test
    @DisplayName("특정 사용자(member id: 1의 전체 알림 조회 성공")
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetNotifications() throws Exception {
        // given

        List<Notification> notifications = Arrays.asList(notificationScheduleCreate, notificationScheduleDelete);
        List<NotificationResponse> notificationResponses = notifications.stream()
            .map(Notification::toResponse)
            .collect(Collectors.toList());

        when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(1L);
        when(customUserDetails.getId()).thenReturn(1L);
        when(notificationService.getNotifications(1L))
            .thenReturn(notifications);

        // when & then
        mockMvc.perform(get("/api/notifications"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].content").value("일정 생성 알림"))
              .andExpect(jsonPath("$[1].content").value("일정 삭제 알림"));
    }
}