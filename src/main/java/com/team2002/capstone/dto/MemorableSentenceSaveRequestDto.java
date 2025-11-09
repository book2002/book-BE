package com.team2002.capstone.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemorableSentenceSaveRequestDto {
    @NotNull(message = "어떤 책에 대한 문장인지 itemId가 필요합니다.")
    private Long itemId;

    @NotBlank(message = "기억에 남는 문장을 입력해주세요.")
    private String content;

    private Integer page;
}
