package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequestDTO {
    @NotEmpty(message = "닉네임 입력은 필수 입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;
    @Size(max = 200, message = "소개는 200자 이하여야 합니다.")
    private String bio;
    private boolean deleteProfileImage;
}
