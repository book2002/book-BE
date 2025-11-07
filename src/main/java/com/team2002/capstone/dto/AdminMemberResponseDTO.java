package com.team2002.capstone.dto;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class AdminMemberResponseDTO {
    private Long memberId;
    private String email;
    private String status; // 계정 상태 (ACTIVE, SUSPENDED) -> 계정 복구 버튼 활성화 여부 확인 가능
    private String role;
    private String nickname;
    private String fcmToken;
    private LocalDateTime createdAt;

    public static AdminMemberResponseDTO from(Member member, Profile profile) {
        return AdminMemberResponseDTO.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .status(member.getStatus().name())
                .role(member.getRole().name())
                .nickname(profile != null ? profile.getNickname() : "미설정")
                .fcmToken(member.getFcmToken())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
