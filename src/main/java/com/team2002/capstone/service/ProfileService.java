package com.team2002.capstone.service;

import com.team2002.capstone.domain.Follow;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.enums.GenderEnum;
import com.team2002.capstone.dto.*;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.FollowRepository;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FileService fileService;

    @Transactional
    public ProfileResponseDTO createProfile(ProfileRequestDTO requestDTO, MultipartFile image) throws IOException {
        // 현재 로그인된 사용자 정보
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        if (profileRepository.findByMember(member).isPresent()) {
            throw new IllegalStateException("이미 프로필을 생성했습니다.");
        }

        String profileImageUrl = null;
        if (image != null && !image.isEmpty()) {
            // 파일을 'profiles' 폴더에 업로드하고 URL 획득
            profileImageUrl = fileService.uploadFile(image, "profiles");
        }

        GenderEnum gender = requestDTO.getGender().equals("MALE") ? GenderEnum.MALE : GenderEnum.FEMALE;

        Profile profile = Profile.builder()
                .nickname(requestDTO.getNickname())
                .birth(requestDTO.getBirth())
                .gender(gender)
                .bio(requestDTO.getBio())
                .profileImageUrl(profileImageUrl)
                .member(member)
                .followings(new HashSet<>())
                .followers(new HashSet<>())
                .build();

        profileRepository.save(profile);
        return ProfileResponseDTO.builder()
                .profileId(profile.getId())
                .nickName(profile.getNickname())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .followerCount(profile.getFollowers().size())
                .followingCount(profile.getFollowings().size())
                .createdAt(profile.getCreatedAt())
                .build();
    }

    @Transactional
    public ProfileResponseDTO updateProfile(ProfileUpdateRequestDTO requestDTO, MultipartFile image) throws IOException {
        Profile profile = getCurrentProfile();
        String oldImageUrl = profile.getProfileImageUrl();
        String newImageUrl = oldImageUrl;

        if(requestDTO.isDeleteProfileImage()){
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                fileService.deleteFile(oldImageUrl);
            }
            newImageUrl = null;
        }
        else if (image != null && !image.isEmpty()) {
            newImageUrl = fileService.uploadFile(image, "profiles");
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                fileService.deleteFile(oldImageUrl);
            }
        }

        if(requestDTO.getNickname() != null && !requestDTO.getNickname().isBlank()) {
            profile.setNickname(requestDTO.getNickname());
        }
        if(requestDTO.getBio() != null && !requestDTO.getBio().isBlank()) {
            profile.setBio(requestDTO.getBio());
        }

        profile.setProfileImageUrl(newImageUrl);
        return ProfileResponseDTO.builder()
                .profileId(profile.getId())
                .nickName(profile.getNickname())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .followerCount(profile.getFollowers().size())
                .followingCount(profile.getFollowings().size())
                .createdAt(profile.getCreatedAt())
                .build();
    }

    @Transactional
    public ProfileViewResponseDTO getProfile(Long profileId) {
        Profile currentProfile = getCurrentProfile();

        Profile viewProfile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("프로필을 찾을 수 없습니다.")); // 조회 대상

        boolean isMyProfile = false;
        boolean isFollowing = false;

        if (currentProfile != null) {
            isMyProfile = viewProfile.getId() == currentProfile.getId();
            if (!isMyProfile) { // 내 프로필이 아닌 경우에만 팔로우 여부 확인
                isFollowing = followRepository.findByFollowerAndFollowing(currentProfile, viewProfile).isPresent();
            }
        }

        int followerCount = viewProfile.getFollowers().size();
        int followingCount = viewProfile.getFollowings().size();

        return ProfileViewResponseDTO.builder()
                .nickname(viewProfile.getNickname())
                .bio(viewProfile.getBio())
                .profileImageUrl(viewProfile.getProfileImageUrl())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isMyProfile(isMyProfile)
                .isFollowing(isFollowing)
                .build();

    }

    @Transactional
    public List<ProfileListResponseDTO> getFollowingList(){
        Profile currentProfile = getCurrentProfile();

        List<Follow> followings = followRepository.findByFollower(currentProfile);

        return followings.stream()
                .map(follow -> {
                    Profile followingProfile = follow.getFollowing();
                    return ProfileListResponseDTO.builder()
                            .profileId(followingProfile.getId())
                            .nickname(followingProfile.getNickname())
                            .bio(followingProfile.getBio())
                            .profileImageUrl(followingProfile.getProfileImageUrl())
                            .isFollowing(true) // 팔로잉 목록이므로 항상 true
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProfileListResponseDTO> getFollowerList(){
        Profile currentProfile = getCurrentProfile();

        List<Follow> followers = followRepository.findByFollowing(currentProfile);

        return followers.stream()
                .map(follow -> {
                    Profile followerProfile = follow.getFollower();
                    boolean isFollowing = followRepository.findByFollowerAndFollowing(currentProfile, followerProfile).isPresent();
                    return ProfileListResponseDTO.builder()
                            .profileId(followerProfile.getId())
                            .nickname(followerProfile.getNickname())
                            .bio(followerProfile.getBio())
                            .profileImageUrl(followerProfile.getProfileImageUrl())
                            .isFollowing(isFollowing)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}
