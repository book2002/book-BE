package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GroupCommentRequestDTO {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
