package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DiscussionStatusUpdateRequestDTO {
    @NotNull(message = "상태 값은 필수입니다.")
    private Boolean isClosed;
}
