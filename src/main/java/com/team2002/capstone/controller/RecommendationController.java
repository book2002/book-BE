package com.team2002.capstone.controller;

import com.team2002.capstone.dto.BookDto;
import com.team2002.capstone.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getRecommendations(@RequestParam String category) {
        List<BookDto> recommendations = recommendService.getRecommendations(category);
        return ResponseEntity.ok(recommendations);
    }
}
