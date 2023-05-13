package com.hanghae.be_h010gram.domain.member.Service;

import com.hanghae.be_h010gram.domain.member.dto.LoginRequestDto;
import com.hanghae.be_h010gram.domain.member.dto.MemberResponseDto;
import com.hanghae.be_h010gram.domain.member.dto.ProfileRequestDto;
import com.hanghae.be_h010gram.domain.member.entity.Member;
import com.hanghae.be_h010gram.domain.member.repository.MemberRepository;
import com.hanghae.be_h010gram.exception.CustomException;
import com.hanghae.be_h010gram.exception.ExceptionEnum;
import com.hanghae.be_h010gram.security.jwt.JwtUtil;
import com.hanghae.be_h010gram.util.ResponseDto;
import com.hanghae.be_h010gram.util.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;

    @Transactional
    public ResponseDto<String> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        //멤버 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ExceptionEnum.USER_NOT_FOUND)
        );

        //비밀번호 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ExceptionEnum.INVALID_USER_PASSWORD);
        }

        //토큰 생성 및 헤더 반환
        String token = jwtUtil.createToken(member.getNickname(), member.getEmail(), member.getId());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseDto.setSuccess("로그인 성공");
    }

    @Transactional(readOnly = true)
    public ResponseDto<MemberResponseDto> getProfil(Long memberId, Member member) {
        //현재 로그인 멤버 조회
        Member loginMember = findMember(memberId);

        //로그인된 멤버와 토큰 정보의 멤버가 동일한지 확인
        isMemberEqual(member.getId(), loginMember.getId());

        //성공
        MemberResponseDto memberResponseDto = new MemberResponseDto(member);
        return ResponseDto.setSuccess("success", memberResponseDto);
    }

    @Transactional
    public ResponseDto<String> updateProfile(Long memberId, ProfileRequestDto profileRequestDto,
                                             MultipartFile image, Member member) throws IOException {
        //회원 조회
        Member updateMember = findMember(memberId);

        //로그인된 멤버와 토큰 정보의 멤버가 동일한지 확인
        isMemberEqual(member.getId(), updateMember.getId());

        //닉네임 수정
        updateMember.setNickname(profileRequestDto.getNickname());
        if (!image.isEmpty()) {
            //기존에 있던 이미지 파일 s3에서 삭제
            s3Service.delete(updateMember.getProfileImage());
            //새로 등록한 사진 s3에 업로드
            String uploadFilename = s3Service.uploadFile(image);
            //업로드 된 사진으로 수정
            updateMember.setProfileImage(uploadFilename);
        }

        memberRepository.save(updateMember);
        return ResponseDto.setSuccess("success");
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND));
    }

    public void isMemberEqual(Long pathMemberId, Long tokenMemberId) {
        if (!Objects.equals(pathMemberId, tokenMemberId)) {
            //토큰의 정보가 유효하지 않아서 실패
            throw new CustomException(ExceptionEnum.INVALID_AUTH_TOKEN);
        }
    }
}