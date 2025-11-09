package com.team2002.capstone.controller;

import com.team2002.capstone.domain.MemorableSentence;
import com.team2002.capstone.dto.MemorableSentenceResponseDto;
import com.team2002.capstone.dto.MemorableSentenceSaveRequestDto;
import com.team2002.capstone.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sentences") // '문장' 관련 API
public class MemorableSentenceController {

    private final BookService bookService;

    /**
     * 기억에 남는 문장 저장
     */
    @PostMapping
    public ResponseEntity<MemorableSentenceResponseDto> saveSentence(
            @Valid @RequestBody MemorableSentenceSaveRequestDto requestDto) {

        // ▼▼▼ "MemorableSentence" -> "MemorableSentenceResponseDto"로 수정 ▼▼▼
        MemorableSentenceResponseDto responseDto = bookService.saveMemorableSentence(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 책의 문장 목록 조회
     */
    @GetMapping("/book/{itemId}")
    public ResponseEntity<List<MemorableSentenceResponseDto>> getSentencesByItemId(@PathVariable Long itemId) {
        List<MemorableSentenceResponseDto> responseDtos = bookService.getMemorableSentencesByItemId(itemId);
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 기억에 남는 문장 수정
     */
    @PutMapping("/{sentenceId}")
    public ResponseEntity<MemorableSentenceResponseDto> updateSentence(
            @PathVariable Long sentenceId,
            @Valid @RequestBody MemorableSentenceSaveRequestDto requestDto) {

        MemorableSentenceResponseDto responseDto = bookService.updateMemorableSentence(sentenceId, requestDto);
        return ResponseEntity.ok(responseDto);
    }


     //기억에 남는 문장 삭제
    @DeleteMapping("/{sentenceId}")
    public ResponseEntity<Void> deleteSentence(@PathVariable Long sentenceId) {
        bookService.deleteMemorableSentence(sentenceId);
        return ResponseEntity.noContent().build();
    }
}
