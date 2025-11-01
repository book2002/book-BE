package com.team2002.capstone.controller;

import com.team2002.capstone.dto.*;
import com.team2002.capstone.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 생성 (최초 로그인)")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponseDTO> createProfile(
            @Validated @RequestPart(value = "request") ProfileRequestDTO profileRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image)
    throws IOException {
        ProfileResponseDTO profileResponseDTO = profileService.createProfile(profileRequestDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileResponseDTO);
    }

    @Operation(summary = "프로필 수정 (이미지, 닉네임, 바이오)")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @Validated @RequestPart(value = "request") ProfileUpdateRequestDTO profileUpdateRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image)
    throws IOException {
        ProfileResponseDTO profileResponseDTO = profileService.updateProfile(profileUpdateRequestDTO, image);
        return ResponseEntity.status(HttpStatus.OK).body(profileResponseDTO);
    }

    @Operation(summary = "프로필 조회")
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileViewResponseDTO> getProfile(@PathVariable Long profileId) {
        ProfileViewResponseDTO profileViewResponseDTO = profileService.getProfile(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(profileViewResponseDTO);
    }

    @Operation(summary = "내 팔로잉 목록 조회")
    @GetMapping("/me/followings")
    public ResponseEntity<List<ProfileListResponseDTO>> getMyFollowingList() {
        List<ProfileListResponseDTO> followingList = profileService.getFollowingList();
        return ResponseEntity.status(HttpStatus.OK).body(followingList);
    }

    @Operation(summary = "내 팔로워 목록 조회")
    @GetMapping("/me/followers")
    public ResponseEntity<List<ProfileListResponseDTO>> getMyFollowerList() {
        List<ProfileListResponseDTO> followerList = profileService.getFollowerList();
        return ResponseEntity.status(HttpStatus.OK).body(followerList);
    }
}
