package com.team2002.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    private boolean isNewUser;
    private Long profileId;
}
