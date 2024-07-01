package com.tenten.studybadge.member.domain.dto;

import com.tenten.studybadge.member.domain.entity.Member;
import com.tenten.studybadge.type.member.BadgeLevel;
import com.tenten.studybadge.type.member.MemberStatus;
import com.tenten.studybadge.type.member.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 DTO")
public class MemberSignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String introduction;

    private String imgUrl;

    @NotBlank(message = "계좌번호를 입력해주세요.")
    private String account;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "확인 비밀번호를 입력해주세요.")
    private String checkPassword;


    public static Member toEntity(Member member, MemberSignUpRequest signUpRequest) {
        String uuid = UUID.randomUUID().toString();
        String encPassword = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());

        return member.toBuilder()
                .platform(Platform.LOCAL)
                .email(signUpRequest.getEmail())
                .password(encPassword)
                .name(signUpRequest.getName())
                .nickname(signUpRequest.getNickname())
                .introduction(signUpRequest.getIntroduction())
                .imgUrl(signUpRequest.getImgUrl())
                .auth(false)
                .authCode(uuid)
                .point(0)
                .banCnt(0)
                .account(signUpRequest.getAccount())
                .status(MemberStatus.WAIT_FOR_APPROVAL)
                .badgeLevel(BadgeLevel.NONE)
                .build();



    }




}
