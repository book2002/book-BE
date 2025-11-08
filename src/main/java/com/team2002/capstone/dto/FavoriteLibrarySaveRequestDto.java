package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteLibrarySaveRequestDto {
    @NotBlank(message = "도서관 이름은 필수입니다.")
    private String libName;

}
