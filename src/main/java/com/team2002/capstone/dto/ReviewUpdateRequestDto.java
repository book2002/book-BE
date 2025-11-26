package com.team2002.capstone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// '리뷰 수정' 전용 DTO (itemId가 필요 없음)
@Getter
@NoArgsConstructor
public class ReviewUpdateRequestDto {

    @NotBlank(message = "감상문 내용을 입력해주세요.")
    private String content;

    private Double rating;
    private Boolean isPublic;
}
