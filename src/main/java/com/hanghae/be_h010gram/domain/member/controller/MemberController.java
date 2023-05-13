package com.hanghae.be_h010gram.domain.member.controller;

import com.hanghae.be_h010gram.domain.member.Service.MemberService;
import com.hanghae.be_h010gram.domain.member.dto.LoginRequestDto;
import com.hanghae.be_h010gram.domain.member.dto.MemberResponseDto;
import com.hanghae.be_h010gram.domain.member.dto.ProfileRequestDto;
import com.hanghae.be_h010gram.security.auth.UserDetailsImpl;
import com.hanghae.be_h010gram.util.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/login")
    public ResponseDto<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return memberService.login(loginRequestDto, response);
    }

    @GetMapping("/member/{memberId}")
    public ResponseDto<MemberResponseDto> getProfile (@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.getProfil(memberId, userDetails.getMember());
    }

    @PutMapping(value = "/member/{memberId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<String> updateProfile(@PathVariable Long memberId,
                                             @RequestPart(value = "imageFile", required = false) ProfileRequestDto profileRequestDto,
                                             @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return memberService.updateProfile(memberId, profileRequestDto, image, userDetails.getMember());
    }
}