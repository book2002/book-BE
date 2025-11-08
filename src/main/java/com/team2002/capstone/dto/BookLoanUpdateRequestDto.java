package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookLoanUpdateRequestDto {

    @NotBlank(message = "책 제목을 입력해주세요.")
    private String bookTitle;

    @NotBlank(message = "도서관 이름을 입력해주세요.")
    private String libraryName;

    @NotBlank(message = "대출일을 입력해주세요.")
    private String checkoutDate;
    private String dueDate;
}
