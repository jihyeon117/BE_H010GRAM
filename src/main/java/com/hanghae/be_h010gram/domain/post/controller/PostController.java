package com.hanghae.be_h010gram.domain.post.controller;

import com.hanghae.be_h010gram.domain.post.dto.MainPostResponseDto;
import com.hanghae.be_h010gram.domain.post.dto.PostRequestDto;
import com.hanghae.be_h010gram.domain.post.dto.PostResponseDto;
import com.hanghae.be_h010gram.domain.post.service.PostService;
import com.hanghae.be_h010gram.security.auth.UserDetailsImpl;
import com.hanghae.be_h010gram.util.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/posts")
@Tag(name = "post", description = "게시글 API")
@CrossOrigin
public class PostController {
    private final PostService postService;

    // 목록 조회
    @GetMapping
    @Operation(summary = "게시글 전체 조회")
    public ResponseDto<List<MainPostResponseDto>> getAllPosts() {
        return postService.getAllPosts();
    }

    // 상세 조회
    @GetMapping("{postId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseDto<PostResponseDto> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    // 내 게시글 목록 조회
    @GetMapping("mypage")
    @Operation(summary = "내 게시글 전체 조회")
    public ResponseDto<List<MainPostResponseDto>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getMyPosts(userDetails.getMember());
    }

    // 추가
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "게시글 생성")
    public ResponseDto<PostResponseDto> createPost(@RequestPart PostRequestDto postRequestDto,
                                                   @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.createPost(postRequestDto, image, userDetails.getMember());
    }

    // 수정
    @PutMapping(value = "{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "게시글 수정")
    public ResponseDto<PostResponseDto> updatePost(@PathVariable Long postId,
                                                   @RequestPart(value = "postRequestDto", required = false) PostRequestDto postRequestDto,
                                                   @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.updatePost(postId, postRequestDto, image, userDetails.getMember());
    }

    // 삭제
    @DeleteMapping("{postId}")
    @Operation(summary = "게시글 삭제")
    public ResponseDto<?> deletePost(@PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails.getMember());
    }
}
