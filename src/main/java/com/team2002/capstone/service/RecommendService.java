package com.team2002.capstone.service;

import com.team2002.capstone.dto.BookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final BookService bookService;

    public List<BookDto> getRecommendations(String category) {

        log.info("--- RecommendationService: Received category string: '{}'", category);
        String searchKeyword = translateCategoryToKeyword(category);
        return bookService.searchBooks(searchKeyword);
    }

    private String translateCategoryToKeyword(String category) {
        String nextYear = String.valueOf(LocalDate.now().getYear()+1);
        switch (category) {
            case "힐링" :
                return "행복 휴식";
            case "재테크" :
                return "주식 투자";
            case "자기개발" :
                return "성공 목표";
            case "여행" :
                return "여행";
            case "추리/공포" :
                return "추리 스릴러";
            case "트렌드" :
                return "트렌드 전망 " + nextYear;
            case "10대" :
                return "청소년 성장";
            case "20대" :
                return "취업 스펙";
            case "30대" :
                return "주식 재테크";
            case "40대" :
                return "건강 육아";
            case "50대" :
                return "은퇴 노후";
            default:
                return "신작";
        }
    }
}
