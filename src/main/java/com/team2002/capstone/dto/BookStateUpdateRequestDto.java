package com.team2002.capstone.dto;

import com.team2002.capstone.domain.BookShelfItem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookStateUpdateRequestDto {
    @NotNull (message = "변경할 독서 상태를 지정해주세요")
    private BookShelfItem.ReadState newState;

    private Integer currentPage;
    private Integer totalPage;
}
