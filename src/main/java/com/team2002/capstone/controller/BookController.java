package com.team2002.capstone.controller;

import com.team2002.capstone.dto.BookDto;
import com.team2002.capstone.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String query) {
        List<BookDto> bookDtos = bookService.searchBooks(query);
        return ResponseEntity.ok(bookDtos);
    }

    @GetMapping("/bestseller")
    public ResponseEntity<List<BookDto>> getRecommendedBooks() {
        List<BookDto> recommendedBooks = bookService.getRecommendedBooks();
        return ResponseEntity.ok(recommendedBooks);
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<BookDto>> getNewReleases() {
        List<BookDto> newReleases = bookService.getNewReleases();
        return ResponseEntity.ok(newReleases);
    }

}
