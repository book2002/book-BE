package com.team2002.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ProfileViewResponseDTO {

    private String nickname;
    private String bio;
    private String profileImageUrl;

    private int followerCount;
    private int followingCount;

    private boolean isMyProfile;
    private boolean isFollowing; // 현재 로그인한 사용자가 조회한 프로필을 팔로우 했는지 여부
}
