package com.team2002.capstone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewSaveRequestDto {
    @NotNull(message = "리뷰를 작성할 책을 선택해주세요.")
    private Long itemId;      // 저장한 책의 itemid

    @NotBlank(message = "감상문 내용을 입력해주세요.")
    private String content;   // 감상문 내용

    private Double rating;// 별점
    private Boolean isPublic; // 공개 여부
}
