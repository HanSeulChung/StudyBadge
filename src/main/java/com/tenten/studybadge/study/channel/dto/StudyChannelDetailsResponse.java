package com.tenten.studybadge.study.channel.dto;

import com.tenten.studybadge.type.study.channel.Category;
import com.tenten.studybadge.type.study.channel.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class StudyChannelDetailsResponse {

    private Long studyChannelId;
    private String studyChannelName;
    private String studyChannelDescription;
    private String chattingUrl;
    private int capacity;
    private Category category;
    private MeetingType meetingType;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
    private int deposit;
    private String leaderName;
    private String subLeaderName;

}
