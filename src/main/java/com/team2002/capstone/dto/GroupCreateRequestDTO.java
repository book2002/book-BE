package com.team2002.capstone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GroupCreateRequestDTO {

    @NotBlank(message = "모임 이름은 필수입니다.")
    private String name;
    private String description;
    private String goal;

    @NotNull(message = "최대 인원수는 필수입니다.")
    @Min(value = 2, message = "모임은 최소 2명 이상이어야 합니다.")
    @Max(value = 30, message = "모임은 최대 30명까지 입니다.")
    private int maxMembers;
}
