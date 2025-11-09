package com.team2002.capstone.dto;

import com.team2002.capstone.domain.Review;
import lombok.Getter;
import java.time.LocalDateTime;

// 리뷰 조회 또는 생성/수정 후 응답으로 보낼 데이터
@Getter
public class ReviewResponseDto {

    private Long reviewId;
    private String content;
    private String bookTitle;
    private Double rating;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private Long itemId; // 어떤 책에 대한 리뷰인지

    // Entity를 Response DTO로 변환하는 생성자
    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.isPublic = review.isPublic();
        this.createdAt = review.getCreatedAt();

        // 연결된 BookShelfItem에서 책 정보를 가져옴
        if (review.getBookShelfItem() != null) {
            this.itemId = review.getBookShelfItem().getItemId();
            this.bookTitle = review.getBookShelfItem().getTitle();
        }
    }
}
