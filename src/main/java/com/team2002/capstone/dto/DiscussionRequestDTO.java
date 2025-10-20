package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DiscussionRequestDTO {
    @NotBlank(message = "토론 주제 제목은 필수입니다.")
    private String topicTitle;

    @NotBlank(message = "토론 상세 내용은 필수입니다.")
    private String topicContent;
}
