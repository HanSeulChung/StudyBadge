package com.tenten.studybadge.member.dto;

import com.tenten.studybadge.member.domain.entity.Member;
import com.tenten.studybadge.type.member.BadgeLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberResponse {

    private String email;

    private String name;

    private String nickname;

    private BadgeLevel badgeLevel;

    private String account;

    private String introduction;

    private String imgUrl;

    private int point;

    private int banCnt;


    public static MemberResponse of(Member member) {

        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .account(member.getAccount())
                .badgeLevel(member.getBadgeLevel())
                .introduction(member.getIntroduction())
                .imgUrl(member.getImgUrl())
                .point(member.getPoint())
                .banCnt(member.getBanCnt())
                .build();
    }
}