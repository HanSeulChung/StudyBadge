package com.tenten.studybadge.common.token.dto;

import com.tenten.studybadge.member.domain.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenCreateDto {

    private String email;

    private MemberRole role;
}
