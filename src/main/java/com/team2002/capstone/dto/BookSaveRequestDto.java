package com.team2002.capstone.dto;

import com.team2002.capstone.domain.BookShelfItem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BookSaveRequestDto {
    @NotNull(message = "ISBN 정보가 필요합니다.")
    private String isbn;

    @NotNull(message = "책 제목 정보가 필요합니다.")
    private String title;

    private List<String> authors;

    private String thumbnail;

    @NotNull(message = "독서 상태를 선택해주세요")
    private BookShelfItem.ReadState state;

    private Integer currentPage;
    private Integer totalPage;
}
