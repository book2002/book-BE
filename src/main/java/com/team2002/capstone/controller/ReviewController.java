package com.team2002.capstone.controller;

import com.team2002.capstone.domain.Review;
import com.team2002.capstone.dto.ReviewResponseDto;
import com.team2002.capstone.dto.ReviewSaveRequestDto;
import com.team2002.capstone.dto.ReviewUpdateRequestDto;
import com.team2002.capstone.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final BookService bookService;

    public ReviewController(BookService bookService) {
        this.bookService = bookService;
    }

    // 독서감상문 작성 후 저장
    @PostMapping("/write")
    public ResponseEntity<ReviewResponseDto> saveReview(@Valid @RequestBody ReviewSaveRequestDto requestDto) {
        ReviewResponseDto responseDto = bookService.saveReview(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 독서 감상문 조회
    @GetMapping("/book/{itemId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByItemId(@PathVariable Long itemId) {
        List<ReviewResponseDto> responseDtos = bookService.getReviewsByItemId(itemId);
        return ResponseEntity.ok(responseDtos);
    }

    //  독서 감상문 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto requestDto) { // DTO 변경
        ReviewResponseDto responseDto = bookService.updateReview(reviewId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 독서 감상문 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        bookService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 내가 쓴 모든 리뷰 조회
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
        List<ReviewResponseDto> responseDtos = bookService.getMyReviews();
        return ResponseEntity.ok(responseDtos);
    }

}