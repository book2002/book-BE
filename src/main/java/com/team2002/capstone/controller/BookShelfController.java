package com.team2002.capstone.controller;

import com.team2002.capstone.domain.BookShelfItem;
import com.team2002.capstone.dto.*;
import com.team2002.capstone.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/my-shelf")
public class BookShelfController {

    private final BookService bookService;

    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> saveItemToMyShelf(@Valid @RequestBody BookSaveRequestDto requestDto) {
        try {
            // Service 메소드가 DTO를 반환하도록 수정했다고 가정
            BookShelfItemDto savedItemDto = bookService.saveBookToMyShelf(requestDto);
            return ResponseEntity.ok(savedItemDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping("/items") // 내 책장에 책 조회
    public ResponseEntity<List<BookShelfItemDto>> getMyShelfItems() {
        List<BookShelfItemDto> items = bookService.getMyShelfItems();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/items/{itemId}") // 책 삭제
    public ResponseEntity<Void> deleteItemFromMyShelf(@PathVariable Long itemId) {
        bookService.deleteBookFromMyShelf(itemId);
        return ResponseEntity.noContent().build(); // 성공 시 204 No Content 반환
    }

    @PatchMapping("/items/{itemId}/progress") // 독서 진행률
    public ResponseEntity<BookShelfItemDto> updateBookProgress(
            @PathVariable Long itemId,
            @Valid @RequestBody ProgressUpdateRequestDto dto) {
        BookShelfItemDto updatedItemDto = bookService.updateBookProgress(itemId, dto);
        return ResponseEntity.ok(updatedItemDto);
    }

    @PatchMapping("/items/{itemId}/state")
    public ResponseEntity<BookShelfItemDto> updateItemState( // 반환 타입을 DTO로 변경
                                                             @PathVariable Long itemId,
                                                             @Valid @RequestBody BookStateUpdateRequestDto dto) {
        // 올바른 Service 메소드 호출
        BookShelfItemDto updatedItemDto = bookService.updateBookState(itemId, dto);
        return ResponseEntity.ok(updatedItemDto);
    }




}
