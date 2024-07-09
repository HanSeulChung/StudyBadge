package com.tenten.studybadge.schedule.controller;

import com.tenten.studybadge.schedule.dto.ScheduleCreateRequest;
import com.tenten.studybadge.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Schedule API", description = "특정 study channel의 일정을 등록, 조회, 수정, 삭제할 수 있는 API")
public class ScheduleController {
  private final ScheduleService scheduleService;

  @PostMapping("/study-channels/{studyChannelId}/schedules")
  @Operation(summary = "장소 저장", description = "지도 api에서 선택한 장소 저장" ,security = @SecurityRequirement(name = "bearerToken"))
  @Parameter(name = "studyChannelId", description = "일정을 만드는 study channel의 id 값", required = true)
  @Parameter(name = "ScheduleRequest", description = "일정 등록 request, type의 값이 single, repeat에 따라 단일 일정 / 반복 일정 등록으로 나뉜다.", required = true )
  public ResponseEntity<Void> postSchedule(
      @PathVariable Long studyChannelId,
      @Valid @RequestBody ScheduleCreateRequest scheduleCreateRequest)  {
    scheduleService.postSchedule(scheduleCreateRequest, studyChannelId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
