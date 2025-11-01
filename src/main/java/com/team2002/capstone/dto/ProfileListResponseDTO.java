package com.team2002.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileListResponseDTO {
    private Long profileId;
    private String nickname;
    private String bio;
    private String profileImageUrl;

    private boolean isFollowing; // 내가 팔로우 했는지 여부
}