package com.team2002.capstone.dto;

import com.team2002.capstone.domain.MemorableSentence;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemorableSentenceResponseDto {
    private Long sentenceId;
    private String content;
    private Integer page;
    private LocalDateTime createdAt;
    private Long itemId;
    private String bookTitle;

    // Entity를 Response DTO로 변환하는 생성자
    public MemorableSentenceResponseDto(MemorableSentence sentence) {
        this.sentenceId = sentence.getSentenceId();
        this.content = sentence.getContent();
        this.page = sentence.getPage();
        this.createdAt = sentence.getCreatedAt();

        if (sentence.getBookShelfItem() != null) {
            this.itemId = sentence.getBookShelfItem().getItemId();
            this.bookTitle = sentence.getBookShelfItem().getTitle();
        }
    }
}
