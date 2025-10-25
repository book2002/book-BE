package com.team2002.capstone.dto;

import com.team2002.capstone.domain.BookShelfItem;
import lombok.Getter;

import java.time.LocalDateTime;

// '내 책장 목록'에 보여줄 간단한 정보만 담는 DTO
@Getter
public class BookShelfItemDto {
    private Long itemId;
    private String isbn;
    private String title;
    private String author;
    private String thumbnail;
    private BookShelfItem.ReadState state;
    private LocalDateTime createdAt;
    private Integer currentPage;
    private Integer totalPage;

    // Entity를 DTO로 변환하는 생성자
    public BookShelfItemDto(BookShelfItem item) {
        this.itemId = item.getItemId();
        this.isbn = item.getIsbn();
        this.title = item.getTitle();
        this.author = item.getAuthor();
        this.thumbnail = item.getThumbnail();
        this.state = item.getState();
        this.createdAt = item.getCreatedAt();
        this.currentPage = item.getCurrentPage();
        this.totalPage = item.getTotalPage();
    }
}
