package com.tenten.studybadge.member.controller;

import com.tenten.studybadge.common.security.CustomUserDetails;
import com.tenten.studybadge.common.token.dto.TokenCreateDto;
import com.tenten.studybadge.common.token.dto.TokenDto;
import com.tenten.studybadge.common.utils.CookieUtils;
import com.tenten.studybadge.member.dto.*;
import com.tenten.studybadge.member.service.MemberService;
import com.tenten.studybadge.common.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import static com.tenten.studybadge.common.constant.TokenConstant.AUTHORIZATION;
import static com.tenten.studybadge.type.member.Platform.LOCAL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member API", description = "Member API")
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;
    @Operation(summary = "회원가입", description = "회원가입")
    @Parameter(name = "signUpRequest", description = "회원가입 요청 Dto" )
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody MemberSignUpRequest signUpRequest) {

        memberService.signUp(signUpRequest, LOCAL);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "인증", description = "인증 요청")
    @Parameter(name = "email", description = "이메일")
    @Parameter(name = "code", description = "인증코드")
    @GetMapping("/auth")
    public ResponseEntity<Void> auth(@RequestParam(name = "email") String email,
                                     @RequestParam(name = "code") String code) {

        memberService.auth(email, code, LOCAL);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Operation(summary = "로그인", description = "일반 로그인")
    @Parameter(name = "loginRequest", description = "로그인 요청 Dto")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody MemberLoginRequest loginRequest) {

        TokenCreateDto createDto = memberService.login(loginRequest, LOCAL);
        TokenDto tokenDto = tokenService.create(createDto.getEmail(), createDto.getRole(), LOCAL);
        ResponseCookie addCookie = CookieUtils.addCookie(tokenDto.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, addCookie.toString())
                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                .body(tokenDto);
    }
    @Operation(summary = "로그아웃", description = "로그아웃" , security = @SecurityRequirement(name = "bearerToken"))
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(AUTHORIZATION) String token) {

        memberService.logout(token);
        ResponseCookie deleteCookie = CookieUtils.deleteCookie(null);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
    }
    @Operation(summary = "내 정보", description = "회원의 나의 정보", security = @SecurityRequirement(name = "bearerToken"))
    @GetMapping("/my-info")
    public ResponseEntity<MemberResponse> myInfo(@AuthenticationPrincipal CustomUserDetails principal) {

        MemberResponse memberResponse = memberService.myInfo(principal.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }
    @PutMapping("/my-info/update")
    public ResponseEntity<MemberResponse> update(@AuthenticationPrincipal CustomUserDetails principal,
                                                 @RequestPart("updateRequest") MemberUpdateRequest updateRequest,
                                                 @RequestPart(value = "file", required = false) MultipartFile profile) {

        memberService.updateMember(principal.getUsername(), updateRequest, profile);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
